package noppes.npcs.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import noppes.npcs.*;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.client.ClientProxy.FontContainer;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.gui.GuiAchievement;
import noppes.npcs.client.gui.GuiNpcMobSpawnerAdd;
import noppes.npcs.client.gui.player.GuiQuestCompletion;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.common.CustomNpcs;
import noppes.npcs.common.CustomNpcsConfig;
import noppes.npcs.common.entity.EntityCustomNpc;
import noppes.npcs.common.entity.EntityDialogNpc;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.common.objects.NpcObjects;
import noppes.npcs.common.objects.items.ItemScripted;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.util.NBTTags;
import noppes.npcs.util.NoppesStringUtils;

import java.util.Map.Entry;

public class PacketHandlerClient extends PacketHandlerServer {

    @SubscribeEvent
    public void onPacketData(ClientCustomPacketEvent event) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null)
            return;
        final ByteBuf buffer = event.getPacket().payload();
        Minecraft.getMinecraft().addScheduledTask(() -> {
            EnumPacketClient type = null;
            try {
                type = EnumPacketClient.values()[buffer.readInt()];
                LogWriter.debug("Received: " + type);
                client(buffer, player, type);
            } catch (Exception e) {
                LogWriter.error("Error with EnumPacketClient." + type, e);
            } finally {
                buffer.release();
            }
        });
    }

    private void client(ByteBuf buffer, final EntityPlayer player, EnumPacketClient type) throws Exception {
        if (type == EnumPacketClient.CHATBUBBLE) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface))
                return;
            EntityNPCInterface npc = (EntityNPCInterface) entity;
            if (npc.messages == null)
                npc.messages = new RenderChatMessages();
            String text = NoppesStringUtils.formatText(Server.readString(buffer), player, npc);
            npc.messages.addMessage(text, npc);

            if (buffer.readBoolean())
                player.sendMessage(new TextComponentTranslation(npc.getName() + ": " + text));
        } else if (type == EnumPacketClient.CHAT) {
            String message = "";
            String str;
            while ((str = Server.readString(buffer)) != null && !str.isEmpty())
                message += I18n.translateToLocal(str);

            player.sendMessage(new TextComponentTranslation(message));
        } else if (type == EnumPacketClient.EYE_BLINK) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface))
                return;
            ModelData data = ((EntityCustomNpc) entity).modelData;
            data.eyes.blinkStart = System.currentTimeMillis();
        } else if (type == EnumPacketClient.MESSAGE) {
            TextComponentTranslation title = new TextComponentTranslation(Server.readString(buffer));
            TextComponentTranslation message = new TextComponentTranslation(Server.readString(buffer));
            int btype = buffer.readInt();

            Minecraft.getMinecraft().getToastGui().add(new GuiAchievement(title, message, btype));
        } else if (type == EnumPacketClient.UPDATE_ITEM) {
            int id = buffer.readInt();
            NBTTagCompound compound = Server.readNBT(buffer);
            ItemStack stack = player.inventory.getStackInSlot(id);
            if (!stack.isEmpty()) {
                ((ItemStackWrapper) NpcAPI.instance().getIItemStack(stack)).setMCNbt(compound);
            }
        } else if (type == EnumPacketClient.SYNC_ADD || type == EnumPacketClient.SYNC_END) {
            int synctype = buffer.readInt();
            NBTTagCompound compound = Server.readNBT(buffer);

            SyncController.clientSync(synctype, compound, type == EnumPacketClient.SYNC_END);

            if (synctype == SyncType.PLAYER_DATA) {
                ClientProxy.Companion.getPlayerData().setNBT(compound);
            } else if (synctype == SyncType.SCRIPTED_ITEM_RESOURCES) {
                if (player.getServer() == null) {
                    ItemScripted.Companion.setResources(NBTTags.getIntegerStringMap(compound.getTagList("List", 10)));
                }
                for (Entry<Integer, String> entry : ItemScripted.Companion.getResources().entrySet()) {
                    ModelResourceLocation mrl = new ModelResourceLocation(entry.getValue(), "inventory");
                    Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(NpcObjects.scriptedItem, entry.getKey(), mrl);
                    ModelLoader.setCustomModelResourceLocation(NpcObjects.scriptedItem, entry.getKey(), mrl);
                }
            }
        } else if (type == EnumPacketClient.SYNC_UPDATE) {
            int synctype = buffer.readInt();
            NBTTagCompound compound = Server.readNBT(buffer);
            SyncController.clientSyncUpdate(synctype, compound, buffer);
        } else if (type == EnumPacketClient.SYNC_REMOVE) {
            int synctype = buffer.readInt();
            int id = buffer.readInt();

            SyncController.clientSyncRemove(synctype, id);
        } else if (type == EnumPacketClient.MARK_DATA) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(buffer.readInt());
            if (!(entity instanceof EntityLivingBase))
                return;
            MarkData data = MarkData.get((EntityLivingBase) entity);
            data.setNBT(Server.readNBT(buffer));
        } else if (type == EnumPacketClient.DIALOG) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(buffer.readInt());

            if (!(entity instanceof EntityNPCInterface))
                return;
            Dialog dialog = DialogController.instance.dialogs.get(buffer.readInt());
            NoppesUtil.openDialog(dialog, (EntityNPCInterface) entity, player);
        } else if (type == EnumPacketClient.DIALOG_DUMMY) {
            EntityDialogNpc npc = new EntityDialogNpc(player.world);
            npc.display.setName(Server.readString(buffer));
            EntityUtil.Copy(player, npc);
            Dialog dialog = new Dialog(null);
            dialog.readNBT(Server.readNBT(buffer));
            NoppesUtil.openDialog(dialog, npc, player);
        } else if (type == EnumPacketClient.QUEST_COMPLETION) {
            int id = buffer.readInt();
            IQuest quest = QuestController.instance.get(id);
            if (!quest.getCompleteText().isEmpty())
                NoppesUtil.openGUI(player, new GuiQuestCompletion(quest));
            else
                NoppesUtilPlayer.sendData(EnumPlayerPacket.QuestCompletion, id);
        } else if (type == EnumPacketClient.EDIT_NPC) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface))
                NoppesUtil.setLastNpc(null);
            else
                NoppesUtil.setLastNpc((EntityNPCInterface) entity);
        } else if (type == EnumPacketClient.PLAY_MUSIC) {
            MusicController.Instance.playMusic(Server.readString(buffer), player);
        } else if (type == EnumPacketClient.PLAY_SOUND) {
            MusicController.Instance.playSound(SoundCategory.VOICE, Server.readString(buffer), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        } else if (type == EnumPacketClient.UPDATE_NPC) {
            NBTTagCompound compound = Server.readNBT(buffer);
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(compound.getInteger("EntityId"));
            if (entity == null || !(entity instanceof EntityNPCInterface))
                return;
            ((EntityNPCInterface) entity).readSpawnData(compound);
        } else if (type == EnumPacketClient.ROLE) {
            NBTTagCompound compound = Server.readNBT(buffer);
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(compound.getInteger("EntityId"));
            if (entity == null || !(entity instanceof EntityNPCInterface))
                return;
            ((EntityNPCInterface) entity).advanced.setRole(compound.getInteger("Role"));
            ((EntityNPCInterface) entity).roleInterface.readFromNBT(compound);
            NoppesUtil.setLastNpc((EntityNPCInterface) entity);
        } else if (type == EnumPacketClient.GUI) {
            EnumGuiType gui = EnumGuiType.values()[buffer.readInt()];
            CustomNpcs.proxy.openGui(NoppesUtil.getLastNpc(), gui, buffer.readInt(), buffer.readInt(), buffer.readInt());
        } else if (type == EnumPacketClient.PARTICLE) {
            NoppesUtil.spawnParticle(buffer);
        } else if (type == EnumPacketClient.DELETE_NPC) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface))
                return;
            ((EntityNPCInterface) entity).delete();
        } else if (type == EnumPacketClient.SCROLL_LIST) {
            NoppesUtil.setScrollList(buffer);
        } else if (type == EnumPacketClient.SCROLL_DATA) {
            NoppesUtil.setScrollData(buffer);
        } else if (type == EnumPacketClient.SCROLL_DATA_PART) {
            NoppesUtil.addScrollData(buffer);
        } else if (type == EnumPacketClient.SCROLL_SELECTED) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (gui == null || !(gui instanceof IScrollData))
                return;
            String selected = Server.readString(buffer);

            ((IScrollData) gui).setSelected(selected);
        } else if (type == EnumPacketClient.CLONE) {
            NBTTagCompound compound = Server.readNBT(buffer);
            NoppesUtil.openGUI(player, new GuiNpcMobSpawnerAdd(compound));
        } else if (type == EnumPacketClient.GUI_DATA) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (gui == null)
                return;

            if (gui instanceof GuiNPCInterface && ((GuiNPCInterface) gui).hasSubGui()) {
                gui = ((GuiNPCInterface) gui).getSubGui();
            } else if (gui instanceof GuiContainerNPCInterface && ((GuiContainerNPCInterface) gui).hasSubGui()) {
                gui = ((GuiContainerNPCInterface) gui).getSubGui();
            }
            if (gui instanceof IGuiData)
                ((IGuiData) gui).setGuiData(Server.readNBT(buffer));
        } else if (type == EnumPacketClient.GUI_UPDATE) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (gui == null)
                return;
            gui.initGui();
        } else if (type == EnumPacketClient.GUI_ERROR) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (gui == null || !(gui instanceof IGuiError))
                return;

            int i = buffer.readInt();
            NBTTagCompound compound = Server.readNBT(buffer);

            ((IGuiError) gui).setError(i, compound);
        } else if (type == EnumPacketClient.GUI_CLOSE) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (gui == null)
                return;

            if (gui instanceof IGuiClose) {
                int i = buffer.readInt();
                NBTTagCompound compound = Server.readNBT(buffer);
                ((IGuiClose) gui).setClose(i, compound);
            }

            Minecraft mc = Minecraft.getMinecraft();
            mc.displayGuiScreen(null);
            mc.setIngameFocus();
        } else if (type == EnumPacketClient.VILLAGER_LIST) {
            MerchantRecipeList merchantrecipelist = MerchantRecipeList.readFromBuf(new PacketBuffer(buffer));
            ServerEventsHandler.Merchant.setRecipes(merchantrecipelist);
        } else if (type == EnumPacketClient.CONFIG) {
            int config = buffer.readInt();
            if (config == 0) {//Font
                final String font = Server.readString(buffer);
                final int size = buffer.readInt();
                Runnable run = () -> {
                    if (!font.isEmpty()) {
                        CustomNpcsConfig.FontType = font;
                        CustomNpcsConfig.FontSize = size;
                        ClientProxy.Companion.getFont().clear();
                        ClientProxy.Companion.setFont(new FontContainer(CustomNpcsConfig.FontType, CustomNpcsConfig.FontSize));
                        CustomNpcsConfig.INSTANCE.updateConfig();
                        player.sendMessage(new TextComponentTranslation("Font set to %s", ClientProxy.Companion.getFont().getName()));
                    } else
                        player.sendMessage(new TextComponentTranslation("Current font is %s", ClientProxy.Companion.getFont().getName()));
                };
                Minecraft.getMinecraft().addScheduledTask(run);
            }
        }
    }


}
