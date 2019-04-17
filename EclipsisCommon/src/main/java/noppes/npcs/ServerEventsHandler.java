package noppes.npcs;

import com.google.common.util.concurrent.ListenableFutureTask;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandGive;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.api.wrapper.WrapperEntityData;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemSoulstoneEmpty;
import noppes.npcs.quests.QuestKill;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

public class ServerEventsHandler {

    public static EntityVillager Merchant;
    public static Entity mounted;

    @SubscribeEvent
    public void invoke(PlayerInteractEvent.EntityInteract event) {
        ItemStack item = event.getEntityPlayer().getHeldItemMainhand();
        if (item == null)
            return;
        boolean isRemote = event.getEntityPlayer().world.isRemote;
        boolean npcInteracted = event.getTarget() instanceof EntityNPCInterface;

        if (!isRemote && CustomNpcs.OpsOnly && !event.getEntityPlayer().getServer().getPlayerList().canSendCommands(event.getEntityPlayer().getGameProfile())) {
            return;
        }

        if (!isRemote && item.getItem() == CustomItems.soulstoneEmpty && event.getTarget() instanceof EntityLivingBase) {
            ((ItemSoulstoneEmpty) item.getItem()).store((EntityLivingBase) event.getTarget(), item, event.getEntityPlayer());
        }

        if (item.getItem() == CustomItems.wand && npcInteracted && !isRemote) {
            if (!CustomNpcsPermissions.hasPermission(event.getEntityPlayer(), CustomNpcsPermissions.NPC_GUI)) {
                return;
            }
            event.setCanceled(true);
            NoppesUtilServer.sendOpenGui(event.getEntityPlayer(), EnumGuiType.MainMenuDisplay, (EntityNPCInterface) event.getTarget());
        } else if (item.getItem() == CustomItems.cloner && !isRemote && !(event.getTarget() instanceof EntityPlayer)) {
            NBTTagCompound compound = new NBTTagCompound();
            if (!event.getTarget().writeToNBTAtomically(compound))
                return;
            PlayerData data = PlayerData.get(event.getEntityPlayer());
            ServerCloneController.Instance.cleanTags(compound);
            if (!Server.sendDataChecked((EntityPlayerMP) event.getEntityPlayer(), EnumPacketClient.CLONE, compound))
                event.getEntityPlayer().sendMessage(new TextComponentString("Entity too big to clone"));
            data.cloned = compound;
            event.setCanceled(true);
        } else if (item.getItem() == CustomItems.scripter && !isRemote && npcInteracted) {
            if (!CustomNpcsPermissions.hasPermission(event.getEntityPlayer(), CustomNpcsPermissions.NPC_GUI))
                return;
            NoppesUtilServer.setEditingNpc(event.getEntityPlayer(), (EntityNPCInterface) event.getTarget());
            event.setCanceled(true);
            Server.sendData((EntityPlayerMP) event.getEntityPlayer(), EnumPacketClient.GUI, EnumGuiType.Script.ordinal(), 0, 0, 0);
        } else if (item.getItem() == CustomItems.mount) {
            if (!CustomNpcsPermissions.hasPermission(event.getEntityPlayer(), CustomNpcsPermissions.TOOL_MOUNTER))
                return;
            event.setCanceled(true);
            mounted = event.getTarget();
            if (isRemote)
                CustomNpcs.proxy.openGui(MathHelper.floor(mounted.posX), MathHelper.floor(mounted.posY), MathHelper.floor(mounted.posZ), EnumGuiType.MobSpawnerMounter, event.getEntityPlayer());
        } else if (item.getItem() == CustomItems.wand && event.getTarget() instanceof EntityVillager) {
            if (!CustomNpcsPermissions.hasPermission(event.getEntityPlayer(), CustomNpcsPermissions.EDIT_VILLAGER))
                return;
            event.setCanceled(true);
            Merchant = (EntityVillager) event.getTarget();

            if (!isRemote) {
                EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
                player.openGui(CustomNpcs.instance, EnumGuiType.MerchantAdd.ordinal(), player.world, 0, 0, 0);
                MerchantRecipeList merchantrecipelist = Merchant.getRecipes(player);

                if (merchantrecipelist != null) {
                    Server.sendData(player, EnumPacketClient.VILLAGER_LIST, merchantrecipelist);
                }
            }
        }
    }

    @SubscribeEvent
    public void invoke(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote)
            return;
        Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());
        if (source != null) {
            if (source instanceof EntityNPCInterface && event.getEntityLiving() != null) {
                EntityNPCInterface npc = (EntityNPCInterface) source;
                Line line = npc.advanced.getKillLine();
                if (line != null)
                    npc.saySurrounding(line.formatTarget(event.getEntityLiving()));
                EventHooks.onNPCKills(npc, event.getEntityLiving());
            }

            EntityPlayer player = null;
            if (source instanceof EntityPlayer)
                player = (EntityPlayer) source;
            else if (source instanceof EntityNPCInterface && ((EntityNPCInterface) source).getOwner() instanceof EntityPlayer)
                player = (EntityPlayer) ((EntityNPCInterface) source).getOwner();
            if (player != null) {
                doQuest(player, event.getEntityLiving(), true);

                if (event.getEntityLiving() instanceof EntityNPCInterface)
                    doFactionPoints(player, (EntityNPCInterface) event.getEntityLiving());
            }
        }
        if (event.getEntityLiving() instanceof EntityPlayer) {
            PlayerData data = PlayerData.get((EntityPlayer) event.getEntityLiving());
            data.save(false);
        }
    }

    private void doFactionPoints(EntityPlayer player, EntityNPCInterface npc) {
        npc.advanced.factions.addPoints(player);
    }

    private void doQuest(EntityPlayer player, EntityLivingBase entity, boolean all) {
        PlayerData pdata = PlayerData.get(player);
        PlayerQuestData playerdata = pdata.questData;
        String entityName = EntityList.getEntityString(entity);
        if (entity instanceof EntityPlayer)
            entityName = "Player";

        for (QuestData data : playerdata.activeQuests.values()) {
            if (data.quest.type != QuestType.KILL && data.quest.type != QuestType.AREA_KILL)
                continue;
            if (data.quest.type == QuestType.AREA_KILL && all) {
                List<EntityPlayer> list = player.world.getEntitiesWithinAABB(EntityPlayer.class, entity.getEntityBoundingBox().grow(10, 10, 10));
                for (EntityPlayer pl : list)
                    if (pl != player)
                        doQuest(pl, entity, false);

            }
            String name = entityName;
            QuestKill quest = (QuestKill) data.quest.questInterface;
            if (quest.targets.containsKey(entity.getName()))
                name = entity.getName();
            else if (!quest.targets.containsKey(name))
                continue;
            HashMap<String, Integer> killed = quest.getKilled(data);
            if (killed.containsKey(name) && killed.get(name) >= quest.targets.get(name))
                continue;
            int amount = 0;
            if (killed.containsKey(name))
                amount = killed.get(name);
            killed.put(name, amount + 1);
            quest.setKilled(data, killed);
            pdata.updateClient = true;
        }
        playerdata.checkQuestCompletion(player, QuestType.KILL);
        playerdata.checkQuestCompletion(player, QuestType.AREA_KILL);
    }

    @SubscribeEvent
    public void pickUp(EntityItemPickupEvent event) {
        if (event.getEntityPlayer().world.isRemote)
            return;
        PlayerQuestData playerdata = PlayerData.get(event.getEntityPlayer()).questData;
        playerdata.checkQuestCompletion(event.getEntityPlayer(), QuestType.ITEM);
    }

    @SubscribeEvent
    public void commandGive(CommandEvent event) {
        if (!(event.getSender().getEntityWorld() instanceof WorldServer) || !(event.getCommand() instanceof CommandGive))
            return;
        try {
            EntityPlayer player = CommandBase.getPlayer(event.getSender().getServer(), event.getSender(), event.getParameters()[0]);

            player.getServer().futureTaskQueue.add(ListenableFutureTask.create(Executors.callable(() -> {
                PlayerQuestData playerdata = PlayerData.get(player).questData;
                playerdata.checkQuestCompletion(player, QuestType.ITEM);
            })));
        } catch (Throwable e) {

        }
    }

    @SubscribeEvent
    public void world(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityPlayer))
            return;
        PlayerData data = PlayerData.get((EntityPlayer) event.getEntity());
        data.updateCompanion(event.getWorld());
    }

    @SubscribeEvent
    public void populateChunk(PopulateChunkEvent.Post event) {
        NPCSpawning.performWorldGenSpawning(event.getWorld(), event.getChunkX(), event.getChunkZ(), event.getRand());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void attachEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer)
            PlayerData.register(event);
        if (event.getObject() instanceof EntityLivingBase)
            MarkData.register(event);
        if (event.getObject().world != null && !event.getObject().world.isRemote && event.getObject().world instanceof WorldServer)
            WrapperEntityData.register(event);
    }

    @SubscribeEvent
    public void attachItem(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStackWrapper.register(event);
    }

    @SubscribeEvent
    public void savePlayer(PlayerEvent.SaveToFile event) {
        PlayerData.get(event.getEntityPlayer()).save(false);
    }

    @SubscribeEvent
    public void saveChunk(ChunkDataEvent.Save event) {
        for (ClassInheritanceMultiMap<Entity> map : event.getChunk().getEntityLists()) {
            for (Entity e : map) {
                if (e instanceof EntityLivingBase) {
                    MarkData.get((EntityLivingBase) e).save();
                }
            }
        }
    }

    @SubscribeEvent
    public void playerTracking(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof EntityLivingBase) || event.getTarget().world.isRemote)
            return;
        MarkData data = MarkData.get((EntityLivingBase) event.getTarget());
        if (data.marks.isEmpty())
            return;
        Server.sendData((EntityPlayerMP) event.getEntityPlayer(), EnumPacketClient.MARK_DATA, event.getTarget().getEntityId(), data.getNBT());
    }
}
