package noppes.npcs;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.constants.*;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.LinkedNpcController.LinkedData;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.roles.JobSpawner;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.roles.RoleTransporter;
import noppes.npcs.schematics.SchematicWrapper;
import noppes.npcs.util.IPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PacketHandlerServer {

    @SubscribeEvent
    public void onServerPacket(ServerCustomPacketEvent event) {
        final EntityPlayerMP player = ((NetHandlerPlayServer) event.getHandler()).player;
        if (CustomNpcs.OpsOnly && !NoppesUtilServer.isOp(player)) {
            warn(player, "tried to use custom npcs without being an op");
            return;
        }
        final ByteBuf buffer = event.getPacket().payload();
        player.getServer().addScheduledTask(() -> {
            EnumPacketServer type = null;
            try {
                type = EnumPacketServer.values()[buffer.readInt()];
                LogWriter.debug("Received: " + type);
                ItemStack item = player.inventory.getCurrentItem();

                EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
                if (type.needsNpc && npc == null) {

                } else if (type.hasPermission() && !CustomNpcsPermissions.hasPermission(player, type.permission)) {
                    //player doesnt have permission todo this

                } else if (!type.isExempt() && !allowItem(item, type)) {
                    warn(player, "tried to use custom npcs without a tool in hand, possibly a hacker");
                } else
                    handlePacket(type, buffer, player, npc);
            } catch (Exception e) {
                LogWriter.error("Error with EnumPacketServer." + type, e);
            } finally {
                buffer.release();
            }
        });
    }

    private boolean allowItem(ItemStack stack, EnumPacketServer type) {
        if (stack == null || stack.getItem() == null)
            return false;
        Item item = stack.getItem();
        IPermission permission = null;
        if (item instanceof IPermission)
            permission = (IPermission) item;
        else if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof IPermission) {
            permission = (IPermission) ((ItemBlock) item).getBlock();
        }

        return permission != null && permission.isAllowed(type);
    }

    private void handlePacket(EnumPacketServer type, ByteBuf buffer, EntityPlayerMP player, EntityNPCInterface npc) throws Exception {
        if (type == EnumPacketServer.Delete) {
            npc.delete();
            NoppesUtilServer.deleteNpc(npc, player);
        } else if (type == EnumPacketServer.SceneStart) {
            DataScenes.Toggle(player, buffer.readInt() + "btn");
        } else if (type == EnumPacketServer.SceneReset) {
            DataScenes.Reset(player, null);
        } else if (type == EnumPacketServer.LinkedAdd) {
            LinkedNpcController.Instance.addData(Server.readString(buffer));

            List<String> list = new ArrayList<>();
            for (LinkedData data : LinkedNpcController.Instance.list)
                list.add(data.name);
            Server.sendData(player, EnumPacketClient.SCROLL_LIST, list);
        } else if (type == EnumPacketServer.LinkedRemove) {
            LinkedNpcController.Instance.removeData(Server.readString(buffer));

            List<String> list = new ArrayList<>();
            for (LinkedData data : LinkedNpcController.Instance.list)
                list.add(data.name);
            Server.sendData(player, EnumPacketClient.SCROLL_LIST, list);
        } else if (type == EnumPacketServer.LinkedGetAll) {
            List<String> list = new ArrayList<>();
            for (LinkedData data : LinkedNpcController.Instance.list)
                list.add(data.name);
            Server.sendData(player, EnumPacketClient.SCROLL_LIST, list);
            if (npc != null)
                Server.sendData(player, EnumPacketClient.SCROLL_SELECTED, npc.linkedName);
        } else if (type == EnumPacketServer.LinkedSet) {
            npc.linkedName = Server.readString(buffer);
            LinkedNpcController.Instance.loadNpcData(npc);
        } else if (type == EnumPacketServer.NpcMenuClose) {
            npc.reset();
            if (npc.linkedData != null)
                LinkedNpcController.Instance.saveNpcData(npc);
            NoppesUtilServer.setEditingNpc(player, null);
        } else if (type == EnumPacketServer.BanksGet) {
            NoppesUtilServer.sendBankDataAll(player);
        } else if (type == EnumPacketServer.BankGet) {
            Bank bank = BankController.getInstance().getBank(buffer.readInt());
            NoppesUtilServer.sendBank(player, bank);
        } else if (type == EnumPacketServer.BankSave) {
            Bank bank = new Bank();
            bank.readEntityFromNBT(Server.readNBT(buffer));
            BankController.getInstance().saveBank(bank);
            NoppesUtilServer.sendBankDataAll(player);
            NoppesUtilServer.sendBank(player, bank);
        } else if (type == EnumPacketServer.BankRemove) {
            BankController.getInstance().removeBank(buffer.readInt());
            NoppesUtilServer.sendBankDataAll(player);
            NoppesUtilServer.sendBank(player, new Bank());
        } else if (type == EnumPacketServer.RemoteMainMenu) {
            Entity entity = player.world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface))
                return;
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.MainMenuDisplay, (EntityNPCInterface) entity);
        } else if (type == EnumPacketServer.RemoteDelete) {
            Entity entity = player.world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface))
                return;
            npc = (EntityNPCInterface) entity;
            npc.delete();
            NoppesUtilServer.deleteNpc(npc, player);
            NoppesUtilServer.sendNearbyNpcs(player);
        } else if (type == EnumPacketServer.RemoteNpcsGet) {
            NoppesUtilServer.sendNearbyNpcs(player);
            Server.sendData(player, EnumPacketClient.SCROLL_SELECTED, CustomNpcs.FreezeNPCs ? "Unfreeze Npcs" : "Freeze Npcs");
        } else if (type == EnumPacketServer.RemoteFreeze) {
            CustomNpcs.FreezeNPCs = !CustomNpcs.FreezeNPCs;
            Server.sendData(player, EnumPacketClient.SCROLL_SELECTED, CustomNpcs.FreezeNPCs ? "Unfreeze Npcs" : "Freeze Npcs");
        } else if (type == EnumPacketServer.RemoteReset) {
            Entity entity = player.world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface))
                return;
            npc = (EntityNPCInterface) entity;
            npc.reset();
        } else if (type == EnumPacketServer.RemoteTpToNpc) {
            Entity entity = player.world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface))
                return;
            npc = (EntityNPCInterface) entity;
            player.connection.setPlayerLocation(npc.posX, npc.posY, npc.posZ, 0, 0);
        } else if (type == EnumPacketServer.Gui) {
            EnumGuiType gui = EnumGuiType.values()[buffer.readInt()];
            int i = buffer.readInt();
            int j = buffer.readInt();
            int k = buffer.readInt();
            NoppesUtilServer.sendOpenGui(player, gui, npc, i, j, k);
        } else if (type == EnumPacketServer.RecipesGet) {
            NoppesUtilServer.sendRecipeData(player, buffer.readInt());
        } else if (type == EnumPacketServer.RecipeGet) {
            RecipeCarpentry recipe = RecipeController.instance.getRecipe(buffer.readInt());
            NoppesUtilServer.setRecipeGui(player, recipe);
        } else if (type == EnumPacketServer.RecipeRemove) {
            RecipeCarpentry recipe = RecipeController.instance.delete(buffer.readInt());
            NoppesUtilServer.sendRecipeData(player, recipe.isGlobal ? 3 : 4);
            NoppesUtilServer.setRecipeGui(player, new RecipeCarpentry(""));
        } else if (type == EnumPacketServer.RecipeSave) {
            RecipeCarpentry recipe = RecipeCarpentry.read(Server.readNBT(buffer));
            RecipeController.instance.saveRecipe(recipe);
            NoppesUtilServer.sendRecipeData(player, recipe.isGlobal ? 3 : 4);
            NoppesUtilServer.setRecipeGui(player, recipe);
        } else if (type == EnumPacketServer.NaturalSpawnGetAll) {
            NoppesUtilServer.sendScrollData(player, SpawnController.instance.getScroll());
        } else if (type == EnumPacketServer.NaturalSpawnGet) {
            SpawnData spawn = SpawnController.instance.getSpawnData(buffer.readInt());
            if (spawn != null) {
                Server.sendData(player, EnumPacketClient.GUI_DATA, spawn.writeNBT(new NBTTagCompound()));
            }
        } else if (type == EnumPacketServer.NaturalSpawnSave) {
            SpawnData data = new SpawnData();
            data.readNBT(Server.readNBT(buffer));
            SpawnController.instance.saveSpawnData(data);

            NoppesUtilServer.sendScrollData(player, SpawnController.instance.getScroll());
        } else if (type == EnumPacketServer.NaturalSpawnRemove) {
            SpawnController.instance.removeSpawnData(buffer.readInt());
            NoppesUtilServer.sendScrollData(player, SpawnController.instance.getScroll());
        } else if (type == EnumPacketServer.DialogCategorySave) {
            DialogCategory category = new DialogCategory();
            category.readNBT(Server.readNBT(buffer));
            DialogController.instance.saveCategory(category);
            Server.sendData(player, EnumPacketClient.GUI_UPDATE);
        } else if (type == EnumPacketServer.DialogCategoryRemove) {
            DialogController.instance.removeCategory(buffer.readInt());
            Server.sendData(player, EnumPacketClient.GUI_UPDATE);
        } else if (type == EnumPacketServer.DialogSave) {
            DialogCategory category = DialogController.instance.categories.get(buffer.readInt());
            if (category == null)
                return;
            Dialog dialog = new Dialog(category);
            dialog.readNBT(Server.readNBT(buffer));
            DialogController.instance.saveDialog(category, dialog);
            Server.sendData(player, EnumPacketClient.GUI_UPDATE);
        } else if (type == EnumPacketServer.DialogRemove) {
            Dialog dialog = DialogController.instance.dialogs.get(buffer.readInt());
            if (dialog != null && dialog.category != null) {
                DialogController.instance.removeDialog(dialog);
                Server.sendData(player, EnumPacketClient.GUI_UPDATE);
            }
        } else if (type == EnumPacketServer.QuestOpenGui) {
            Quest quest = new Quest(null);
            int gui = buffer.readInt();
            quest.readNBT(Server.readNBT(buffer));
            NoppesUtilServer.setEditingQuest(player, quest);
            player.openGui(CustomNpcs.instance, gui, player.world, 0, 0, 0);
        } else if (type == EnumPacketServer.DialogNpcGet) {
            NoppesUtilServer.sendNpcDialogs(player);
        } else if (type == EnumPacketServer.DialogNpcSet) {
            int slot = buffer.readInt();
            int dialog = buffer.readInt();
            DialogOption option = NoppesUtilServer.setNpcDialog(slot, dialog, player);
            if (option != null && option.hasDialog()) {
                NBTTagCompound compound = option.writeNBT();
                compound.setInteger("Position", slot);
                Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
            }
        } else if (type == EnumPacketServer.DialogNpcRemove) {
            npc.dialogs.remove(buffer.readInt());
        } else if (type == EnumPacketServer.QuestCategorySave) {
            QuestCategory category = new QuestCategory();
            category.readNBT(Server.readNBT(buffer));
            QuestController.instance.saveCategory(category);
            Server.sendData(player, EnumPacketClient.GUI_UPDATE);
        } else if (type == EnumPacketServer.QuestCategoryRemove) {
            QuestController.instance.removeCategory(buffer.readInt());
            Server.sendData(player, EnumPacketClient.GUI_UPDATE);
        } else if (type == EnumPacketServer.QuestSave) {
            QuestCategory category = QuestController.instance.categories.get(buffer.readInt());
            if (category == null)
                return;
            Quest quest = new Quest(category);
            quest.readNBT(Server.readNBT(buffer));
            QuestController.instance.saveQuest(category, quest);
            Server.sendData(player, EnumPacketClient.GUI_UPDATE);
        } else if (type == EnumPacketServer.QuestDialogGetTitle) {
            Dialog quest = DialogController.instance.dialogs.get(buffer.readInt());
            Dialog quest2 = DialogController.instance.dialogs.get(buffer.readInt());
            Dialog quest3 = DialogController.instance.dialogs.get(buffer.readInt());
            NBTTagCompound compound = new NBTTagCompound();
            if (quest != null)
                compound.setString("1", quest.title);
            if (quest2 != null)
                compound.setString("2", quest2.title);
            if (quest3 != null)
                compound.setString("3", quest3.title);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.QuestRemove) {
            Quest quest = QuestController.instance.quests.get(buffer.readInt());
            if (quest != null) {
                QuestController.instance.removeQuest(quest);
                Server.sendData(player, EnumPacketClient.GUI_UPDATE);
            }
        } else if (type == EnumPacketServer.TransportCategoriesGet) {
            NoppesUtilServer.sendTransportCategoryData(player);
        } else if (type == EnumPacketServer.TransportCategorySave) {
            TransportController.getInstance().saveCategory(Server.readString(buffer), buffer.readInt());
        } else if (type == EnumPacketServer.TransportCategoryRemove) {
            TransportController.getInstance().removeCategory(buffer.readInt());
            NoppesUtilServer.sendTransportCategoryData(player);
        } else if (type == EnumPacketServer.TransportRemove) {
            int id = buffer.readInt();
            TransportLocation loc = TransportController.getInstance().removeLocation(id);
            if (loc != null)
                NoppesUtilServer.sendTransportData(player, loc.category.id);
        } else if (type == EnumPacketServer.TransportsGet) {
            NoppesUtilServer.sendTransportData(player, buffer.readInt());
        } else if (type == EnumPacketServer.TransportSave) {
            int cat = buffer.readInt();
            TransportLocation location = TransportController.getInstance().saveLocation(cat, Server.readNBT(buffer), player, npc);
            if (location != null) {
                if (npc.advanced.role != RoleType.TRANSPORTER)
                    return;
                RoleTransporter role = (RoleTransporter) npc.roleInterface;
                role.setTransport(location);
            }
        } else if (type == EnumPacketServer.TransportGetLocation) {
            if (npc.advanced.role != RoleType.TRANSPORTER)
                return;
            RoleTransporter role = (RoleTransporter) npc.roleInterface;
            if (role.hasTransport()) {
                Server.sendData(player, EnumPacketClient.GUI_DATA, role.getLocation().writeNBT());
                Server.sendData(player, EnumPacketClient.SCROLL_SELECTED, role.getLocation().category.title);
            }
        } else if (type == EnumPacketServer.FactionSet) {
            npc.setFaction(buffer.readInt());
        } else if (type == EnumPacketServer.FactionSave) {
            Faction faction = new Faction();
            faction.readNBT(Server.readNBT(buffer));
            FactionController.instance.saveFaction(faction);
            NoppesUtilServer.sendFactionDataAll(player);
            NBTTagCompound compound = new NBTTagCompound();
            faction.writeNBT(compound);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.FactionRemove) {
            FactionController.instance.delete(buffer.readInt());
            NoppesUtilServer.sendFactionDataAll(player);
            NBTTagCompound compound = new NBTTagCompound();
            (new Faction()).writeNBT(compound);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.PlayerDataGet) {
            int id = buffer.readInt();
            if (EnumPlayerData.values().length <= id)
                return;
            String name = null;
            EnumPlayerData datatype = EnumPlayerData.values()[id];
            if (datatype != EnumPlayerData.Players)
                name = Server.readString(buffer);
            NoppesUtilServer.sendPlayerData(datatype, player, name);
        } else if (type == EnumPacketServer.PlayerDataRemove) {
            NoppesUtilServer.removePlayerData(buffer, player);
        } else if (type == EnumPacketServer.MainmenuDisplayGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.display.writeToNBT(new NBTTagCompound()));
        } else if (type == EnumPacketServer.MainmenuDisplaySave) {
            npc.display.readToNBT(Server.readNBT(buffer));
            npc.updateClient = true;
        } else if (type == EnumPacketServer.MainmenuStatsGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.stats.writeToNBT(new NBTTagCompound()));
        } else if (type == EnumPacketServer.MainmenuStatsSave) {
            npc.stats.readToNBT(Server.readNBT(buffer));
            npc.updateClient = true;
        } else if (type == EnumPacketServer.MainmenuInvGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.inventory.writeEntityToNBT(new NBTTagCompound()));
        } else if (type == EnumPacketServer.MainmenuInvSave) {
            npc.inventory.readEntityFromNBT(Server.readNBT(buffer));
            npc.updateAI = true;
            npc.updateClient = true;
        } else if (type == EnumPacketServer.MainmenuAIGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.ais.writeToNBT(new NBTTagCompound()));
        } else if (type == EnumPacketServer.MainmenuAISave) {
            npc.ais.readToNBT(Server.readNBT(buffer));
            npc.setHealth(npc.getMaxHealth());
            npc.updateAI = true;
            npc.updateClient = true;
        } else if (type == EnumPacketServer.MainmenuAdvancedGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.advanced.writeToNBT(new NBTTagCompound()));
        } else if (type == EnumPacketServer.MainmenuAdvancedSave) {
            npc.advanced.readToNBT(Server.readNBT(buffer));
            npc.updateAI = true;
            npc.updateClient = true;
        } else if (type == EnumPacketServer.MainmenuAdvancedMarkData) {
            MarkData data = MarkData.get(npc);
            data.setNBT(Server.readNBT(buffer));
            data.syncClients();
        } else if (type == EnumPacketServer.JobSave) {
            NBTTagCompound original = npc.jobInterface.writeToNBT(new NBTTagCompound());
            NBTTagCompound compound = Server.readNBT(buffer);
            Set<String> names = compound.getKeySet();
            for (String name : names)
                original.setTag(name, compound.getTag(name));
            npc.jobInterface.readFromNBT(original);
            npc.updateClient = true;
        } else if (type == EnumPacketServer.JobGet) {
            if (npc.jobInterface == null)
                return;
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("JobData", true);
            npc.jobInterface.writeToNBT(compound);

            if (npc.advanced.job == JobType.SPAWNER)
                ((JobSpawner) npc.jobInterface).cleanCompound(compound);

            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);

            if (npc.advanced.job == JobType.SPAWNER)
                Server.sendData(player, EnumPacketClient.GUI_DATA, ((JobSpawner) npc.jobInterface).getTitles());
        } else if (type == EnumPacketServer.JobSpawnerAdd) {
            if (npc.advanced.job != JobType.SPAWNER)
                return;
            JobSpawner job = (JobSpawner) npc.jobInterface;
            if (buffer.readBoolean()) {
                NBTTagCompound compound = ServerCloneController.Instance.getCloneData(null, Server.readString(buffer), buffer.readInt());

                job.setJobCompound(buffer.readInt(), compound);
            } else {
                job.setJobCompound(buffer.readInt(), Server.readNBT(buffer));
            }
            Server.sendData(player, EnumPacketClient.GUI_DATA, job.getTitles());
        } else if (type == EnumPacketServer.RoleCompanionUpdate) {
            if (npc.advanced.role != RoleType.COMPANION)
                return;
            ((RoleCompanion) npc.roleInterface).matureTo(EnumCompanionStage.values()[buffer.readInt()]);
            npc.updateClient = true;
        } else if (type == EnumPacketServer.JobSpawnerRemove) {
            if (npc.advanced.job != JobType.SPAWNER)
                return;
        } else if (type == EnumPacketServer.RoleSave) {
            npc.roleInterface.readFromNBT(Server.readNBT(buffer));
            npc.updateClient = true;
        } else if (type == EnumPacketServer.RoleGet) {
            if (npc.roleInterface == null)
                return;
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("RoleData", true);
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.roleInterface.writeToNBT(compound));
        } else if (type == EnumPacketServer.MerchantUpdate) {
            Entity entity = player.world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityVillager))
                return;
            MerchantRecipeList list = MerchantRecipeList.readFromBuf(new PacketBuffer(buffer));
            ((EntityVillager) entity).setRecipes(list);
        } else if (type == EnumPacketServer.ModelDataSave) {
            if (npc instanceof EntityCustomNpc)
                ((EntityCustomNpc) npc).modelData.readFromNBT(Server.readNBT(buffer));
        } else if (type == EnumPacketServer.MailOpenSetup) {
            PlayerMail mail = new PlayerMail();
            mail.readNBT(Server.readNBT(buffer));
            ContainerMail.staticmail = mail;
            player.openGui(CustomNpcs.instance, EnumGuiType.PlayerMailman.ordinal(), player.world, 1, 0, 0);
        } else if (type == EnumPacketServer.TransformSave) {
            boolean isValid = npc.transform.isValid();
            npc.transform.readOptions(Server.readNBT(buffer));
            if (isValid != npc.transform.isValid())
                npc.updateAI = true;
        } else if (type == EnumPacketServer.TransformGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.transform.writeOptions(new NBTTagCompound()));
        } else if (type == EnumPacketServer.TransformLoad) {
            if (npc.transform.isValid())
                npc.transform.transform(buffer.readBoolean());
        } else if (type == EnumPacketServer.TraderMarketSave) {
            String market = Server.readString(buffer);
            boolean bo = buffer.readBoolean();
            if (npc.roleInterface instanceof RoleTrader) {
                if (bo)
                    RoleTrader.setMarket(npc, market);
                else
                    RoleTrader.save((RoleTrader) npc.roleInterface, market);
                //NoppesUtilServer.sendRoleData(player, npc);
            }
        } else if (type == EnumPacketServer.MovingPathGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.ais.writeToNBT(new NBTTagCompound()));
        } else if (type == EnumPacketServer.MovingPathSave) {
            npc.ais.setMovingPath(NBTTags.getIntegerArrayList(Server.readNBT(buffer).getTagList("MovingPathNew", 10)));
        } else if (type == EnumPacketServer.SpawnRider) {
            Entity entity = EntityList.createEntityFromNBT(Server.readNBT(buffer), player.world);
            player.world.spawnEntity(entity);
            entity.startRiding(ServerEventsHandler.mounted, true);
        } else if (type == EnumPacketServer.PlayerRider) {
            player.startRiding(ServerEventsHandler.mounted, true);
        } else if (type == EnumPacketServer.SpawnMob) {
            boolean server = buffer.readBoolean();
            int x = buffer.readInt();
            int y = buffer.readInt();
            int z = buffer.readInt();
            NBTTagCompound compound;
            if (server)
                compound = ServerCloneController.Instance.getCloneData(player, Server.readString(buffer), buffer.readInt());
            else
                compound = Server.readNBT(buffer);
            if (compound == null)
                return;
            Entity entity = NoppesUtilServer.spawnClone(compound, x + 0.5, y + 1, z + 0.5, player.world);
            if (entity == null) {
                player.sendMessage(new TextComponentString("Failed to create an entity out of your clone"));
                return;
            }
        } else if (type == EnumPacketServer.MobSpawner) {
            boolean server = buffer.readBoolean();
            BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            NBTTagCompound compound;
            if (server)
                compound = ServerCloneController.Instance.getCloneData(player, Server.readString(buffer), buffer.readInt());
            else
                compound = Server.readNBT(buffer);
            if (compound != null)
                NoppesUtilServer.createMobSpawner(pos, compound, player);
        } else if (type == EnumPacketServer.ClonePreSave) {
            boolean bo = ServerCloneController.Instance.getCloneData(null, Server.readString(buffer), buffer.readInt()) != null;
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("NameExists", bo);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.CloneSave) {
            PlayerData data = PlayerData.get(player);
            if (data.cloned == null)
                return;
            ServerCloneController.Instance.addClone(data.cloned, Server.readString(buffer), buffer.readInt());
        } else if (type == EnumPacketServer.CloneRemove) {
            int tab = buffer.readInt();
            ServerCloneController.Instance.removeClone(Server.readString(buffer), tab);

            NBTTagList list = new NBTTagList();

            for (String name : ServerCloneController.Instance.getClones(tab))
                list.appendTag(new NBTTagString(name));

            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("List", list);

            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.CloneList) {
            NBTTagList list = new NBTTagList();

            for (String name : ServerCloneController.Instance.getClones(buffer.readInt()))
                list.appendTag(new NBTTagString(name));

            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("List", list);

            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.ScriptDataSave) {
            npc.script.readFromNBT(Server.readNBT(buffer));
            npc.updateAI = true;
            npc.script.lastInited = -1;
        } else if (type == EnumPacketServer.ScriptDataGet) {
            NBTTagCompound compound = npc.script.writeToNBT(new NBTTagCompound());
            compound.setTag("Languages", ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.DimensionsGet) {
            HashMap<String, Integer> map = new HashMap<>();
            for (int id : DimensionManager.getStaticDimensionIDs()) {
                WorldProvider provider = DimensionManager.createProviderFor(id);
                map.put(provider.getDimensionType().getName(), id);
            }
            NoppesUtilServer.sendScrollData(player, map);
        } else if (type == EnumPacketServer.DimensionTeleport) {
            int dimension = buffer.readInt();
            WorldServer world = player.getServer().getWorld(dimension);
            BlockPos coords = world.getSpawnCoordinate();
            if (coords == null) {
                coords = world.getSpawnPoint();
                if (!world.isAirBlock(coords))
                    coords = world.getTopSolidOrLiquidBlock(coords);
                else {
                    while (world.isAirBlock(coords) && coords.getY() > 0) {
                        coords = coords.down();
                    }
                    if (coords.getY() == 0)
                        coords = world.getTopSolidOrLiquidBlock(coords);
                }
            }
            NoppesUtilPlayer.teleportPlayer(player, coords.getX(), coords.getY(), coords.getZ(), dimension);
        } else if (type == EnumPacketServer.ScriptBlockDataGet) {
            TileEntity tile = player.world.getTileEntity(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
            if (!(tile instanceof TileScripted))
                return;
            NBTTagCompound compound = ((TileScripted) tile).getNBT(new NBTTagCompound());
            compound.setTag("Languages", ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.ScriptItemDataGet) {
            ItemScriptedWrapper iw = (ItemScriptedWrapper) NpcAPI.Instance().getIItemStack(player.getHeldItemMainhand());
            NBTTagCompound compound = iw.getMCNbt();
            compound.setTag("Languages", ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.ScriptItemDataSave) {
            if (!player.isCreative())
                return;
            NBTTagCompound compound = Server.readNBT(buffer);
            ItemStack item = player.getHeldItemMainhand();
            ItemScriptedWrapper wrapper = (ItemScriptedWrapper) NpcAPI.Instance().getIItemStack(player.getHeldItemMainhand());
            wrapper.setMCNbt(compound);
            wrapper.lastInited = -1;
            wrapper.saveScriptData();
            wrapper.updateClient = true;
            player.sendContainerToPlayer(player.inventoryContainer);
        } else if (type == EnumPacketServer.ScriptForgeGet) {
            ForgeScriptData data = ScriptController.Instance.forgeScripts;
            NBTTagCompound compound = data.writeToNBT(new NBTTagCompound());
            compound.setTag("Languages", ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.ScriptForgeSave) {
            ScriptController.Instance.setForgeScripts(Server.readNBT(buffer));
        } else if (type == EnumPacketServer.ScriptPlayerGet) {
            NBTTagCompound compound = ScriptController.Instance.playerScripts.writeToNBT(new NBTTagCompound());
            compound.setTag("Languages", ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.ScriptPlayerSave) {
            ScriptController.Instance.setPlayerScripts(Server.readNBT(buffer));
        } else if (type == EnumPacketServer.FactionsGet) {
            NoppesUtilServer.sendFactionDataAll(player);
        } else if (type == EnumPacketServer.FactionGet) {
            NBTTagCompound compound = new NBTTagCompound();
            Faction faction = FactionController.instance.getFaction(buffer.readInt());
            faction.writeNBT(compound);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.SaveTileEntity) {
            NoppesUtilServer.saveTileEntity(player, Server.readNBT(buffer));
        } else if (type == EnumPacketServer.GetTileEntity) {
            BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            TileEntity tile = player.world.getTileEntity(pos);
            NBTTagCompound compound = new NBTTagCompound();
            tile.writeToNBT(compound);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.ScriptBlockDataSave) {
            TileEntity tile = player.world.getTileEntity(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
            if (!(tile instanceof TileScripted))
                return;
            TileScripted script = (TileScripted) tile;
            script.setNBT(Server.readNBT(buffer));
            script.lastInited = -1;
        } else if (type == EnumPacketServer.ScriptDoorDataSave) {
            TileEntity tile = player.world.getTileEntity(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
            if (!(tile instanceof TileScriptedDoor))
                return;
            TileScriptedDoor script = (TileScriptedDoor) tile;
            script.setNBT(Server.readNBT(buffer));
            script.lastInited = -1;
        } else if (type == EnumPacketServer.ScriptDoorDataGet) {
            TileEntity tile = player.world.getTileEntity(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
            if (!(tile instanceof TileScriptedDoor))
                return;

            NBTTagCompound compound = ((TileScriptedDoor) tile).getNBT(new NBTTagCompound());
            compound.setTag("Languages", ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        } else if (type == EnumPacketServer.SchematicsTile) {
            BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            TileBuilder tile = (TileBuilder) player.world.getTileEntity(pos);
            if (tile == null)
                return;
            Server.sendData(player, EnumPacketClient.GUI_DATA, tile.writePartNBT(new NBTTagCompound()));
            Server.sendData(player, EnumPacketClient.SCROLL_LIST, SchematicController.Instance.list());

            if (tile.hasSchematic())
                Server.sendData(player, EnumPacketClient.GUI_DATA, tile.getSchematic().getNBTSmall());
        } else if (type == EnumPacketServer.SchematicsSet) {
            BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            TileBuilder tile = (TileBuilder) player.world.getTileEntity(pos);
            String name = Server.readString(buffer);
            tile.setSchematic(SchematicController.Instance.load(name));

            if (tile.hasSchematic())
                Server.sendData(player, EnumPacketClient.GUI_DATA, tile.getSchematic().getNBTSmall());
        } else if (type == EnumPacketServer.SchematicsBuild) {
            BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            TileBuilder tile = (TileBuilder) player.world.getTileEntity(pos);
            SchematicWrapper schem = tile.getSchematic();
            schem.init(pos.add(1, tile.yOffest, 1), player.world, tile.rotation);
            SchematicController.Instance.build(tile.getSchematic(), player);
            player.world.setBlockToAir(pos);
        } else if (type == EnumPacketServer.SchematicsTileSave) {
            BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            TileBuilder tile = (TileBuilder) player.world.getTileEntity(pos);
            if (tile != null) {
                tile.readPartNBT(Server.readNBT(buffer));
            }
        } else if (type == EnumPacketServer.SchematicStore) {
            String name = Server.readString(buffer);
            int t = buffer.readInt();
            TileCopy tile = (TileCopy) NoppesUtilServer.saveTileEntity(player, Server.readNBT(buffer));
            if (tile == null || name.isEmpty())
                return;
            SchematicController.Instance.save(player, name, t, tile.getPos(), tile.height, tile.width, tile.length);
        } else if (type == EnumPacketServer.NbtBookSaveBlock) {
            BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            NBTTagCompound compound = Server.readNBT(buffer);

            TileEntity tile = player.world.getTileEntity(pos);
            if (tile != null) {
                tile.readFromNBT(compound);
                tile.markDirty();
            }

        } else if (type == EnumPacketServer.NbtBookSaveEntity) {
            int entityId = buffer.readInt();
            NBTTagCompound compound = Server.readNBT(buffer);

            Entity entity = player.world.getEntityByID(entityId);
            if (entity != null) {
                entity.readFromNBT(compound);
            }
        }
    }


    private void warn(EntityPlayer player, String warning) {
        player.getServer().logWarning(player.getName() + ": " + warning);
    }
}
