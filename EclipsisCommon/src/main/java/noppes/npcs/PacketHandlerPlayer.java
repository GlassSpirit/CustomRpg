package noppes.npcs;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.event.ItemEvent;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemScripted;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleTransporter;

import java.util.Iterator;

public class PacketHandlerPlayer {

    @SubscribeEvent
    public void onServerPacket(ServerCustomPacketEvent event) {
        final EntityPlayerMP player = ((NetHandlerPlayServer) event.getHandler()).player;
        final ByteBuf buffer = event.getPacket().payload();
        player.getServer().addScheduledTask(() -> {
            EnumPlayerPacket type = null;
            try {
                type = EnumPlayerPacket.values()[buffer.readInt()];
                LogWriter.debug("Received: " + type);
                player(buffer, player, type);
            } catch (Exception e) {
                LogWriter.error("Error with EnumPlayerPacket." + type, e);
            } finally {
                buffer.release();
            }
        });
    }

    private void player(ByteBuf buffer, EntityPlayerMP player, EnumPlayerPacket type) throws Exception {
        if (type == EnumPlayerPacket.MarkData) {
            Entity entity = player.getServer().getEntityFromUuid(Server.readUUID(buffer));
            if (entity == null || !(entity instanceof EntityLivingBase))
                return;
            MarkData data = MarkData.get((EntityLivingBase) entity);
        } else if (type == EnumPlayerPacket.KeyPressed) {
            if (!CustomNpcs.EnableScripting || ScriptController.Instance.languages.isEmpty())
                return;
            EventHooks.onPlayerKeyPressed(player, buffer.readInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
        } else if (type == EnumPlayerPacket.LeftClick) {
            if (!CustomNpcs.EnableScripting || ScriptController.Instance.languages.isEmpty())
                return;
            ItemStack item = player.getHeldItemMainhand();

            PlayerScriptData handler = PlayerData.get(player).scriptData;
            PlayerEvent.AttackEvent ev = new PlayerEvent.AttackEvent(handler.getPlayer(), 0, null);
            EventHooks.onPlayerAttack(handler, ev);

            if (item.getItem() == CustomItems.scripted_item) {
                ItemScriptedWrapper isw = ItemScripted.GetWrapper(item);
                ItemEvent.AttackEvent eve = new ItemEvent.AttackEvent(isw, handler.getPlayer(), 0, null);
                EventHooks.onScriptItemAttack(isw, eve);
            }
        } else if (type == EnumPlayerPacket.CompanionTalentExp) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
            if (npc == null || npc.advanced.role != RoleType.COMPANION || player != npc.getOwner())
                return;
            int id = buffer.readInt();
            int exp = buffer.readInt();
            RoleCompanion role = (RoleCompanion) npc.roleInterface;
            if (exp <= 0 || !role.canAddExp(-exp) || id < 0 || id >= EnumCompanionTalent.values().length) //should never happen unless hacking
                return;
            EnumCompanionTalent talent = EnumCompanionTalent.values()[id];
            role.addExp(-exp);
            role.addTalentExp(talent, exp);
        } else if (type == EnumPlayerPacket.CompanionOpenInv) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
            if (npc == null || npc.advanced.role != RoleType.COMPANION || player != npc.getOwner())
                return;
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.CompanionInv, npc);
        } else if (type == EnumPlayerPacket.FollowerHire) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
            if (npc == null || npc.advanced.role != RoleType.FOLLOWER)
                return;
            NoppesUtilPlayer.hireFollower(player, npc);
        } else if (type == EnumPlayerPacket.FollowerExtend) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
            if (npc == null || npc.advanced.role != RoleType.FOLLOWER)
                return;
            NoppesUtilPlayer.extendFollower(player, npc);
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.roleInterface.writeToNBT(new NBTTagCompound()));
        } else if (type == EnumPlayerPacket.FollowerState) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
            if (npc == null || npc.advanced.role != RoleType.FOLLOWER)
                return;
            NoppesUtilPlayer.changeFollowerState(player, npc);
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.roleInterface.writeToNBT(new NBTTagCompound()));
        } else if (type == EnumPlayerPacket.RoleGet) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
            if (npc == null || npc.advanced.role == RoleType.NONE)
                return;
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.roleInterface.writeToNBT(new NBTTagCompound()));
        } else if (type == EnumPlayerPacket.Transport) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
            if (npc == null || npc.advanced.role != RoleType.TRANSPORTER)
                return;
            ((RoleTransporter) npc.roleInterface).transport(player, Server.readString(buffer));
        } else if (type == EnumPlayerPacket.BankUpgrade) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
            if (npc == null || npc.advanced.role != RoleType.BANK)
                return;
            NoppesUtilPlayer.bankUpgrade(player, npc);
        } else if (type == EnumPlayerPacket.BankUnlock) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
            if (npc == null || npc.advanced.role != RoleType.BANK)
                return;
            NoppesUtilPlayer.bankUnlock(player, npc);
        } else if (type == EnumPlayerPacket.BankSlotOpen) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
            if (npc == null || npc.advanced.role != RoleType.BANK)
                return;
            int slot = buffer.readInt();
            int bankId = buffer.readInt();
            BankData data = PlayerDataController.instance.getBankData(player, bankId).getBankOrDefault(bankId);
            data.openBankGui(player, npc, bankId, slot);
        } else if (type == EnumPlayerPacket.Dialog) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
            LogWriter.debug("Dialog npc: " + npc);
            if (npc == null) {
                return;
            }
            NoppesUtilPlayer.dialogSelected(buffer.readInt(), buffer.readInt(), player, npc);
        } else if (type == EnumPlayerPacket.CheckQuestCompletion) {
            PlayerQuestData playerdata = PlayerData.get(player).questData;
            playerdata.checkQuestCompletion(player, -1);
        } else if (type == EnumPlayerPacket.QuestCompletion) {
            NoppesUtilPlayer.questCompletion(player, buffer.readInt());
        } else if (type == EnumPlayerPacket.FactionsGet) {
            PlayerFactionData data = PlayerData.get(player).factionData;
            Server.sendData(player, EnumPacketClient.GUI_DATA, data.getPlayerGuiData());
        } else if (type == EnumPlayerPacket.MailGet) {
            PlayerMailData data = PlayerData.get(player).mailData;
            Server.sendData(player, EnumPacketClient.GUI_DATA, data.saveNBTData(new NBTTagCompound()));
        } else if (type == EnumPlayerPacket.MailDelete) {
            long time = buffer.readLong();
            String username = Server.readString(buffer);
            PlayerMailData data = PlayerData.get(player).mailData;

            Iterator<PlayerMail> it = data.playermail.iterator();
            while (it.hasNext()) {
                PlayerMail mail = it.next();
                if (mail.time == time && mail.sender.equals(username)) {
                    it.remove();
                }
            }
            Server.sendData(player, EnumPacketClient.GUI_DATA, data.saveNBTData(new NBTTagCompound()));
        } else if (type == EnumPlayerPacket.MailSend) {
            String username = PlayerDataController.instance.hasPlayer(Server.readString(buffer));
            if (username.isEmpty()) {
                NoppesUtilServer.sendGuiError(player, 0);
                return;
            }

            PlayerMail mail = new PlayerMail();
            //String s = player.func_145748_c_().getFormattedText();
            String s = player.getDisplayNameString();
            if (!s.equals(player.getName()))
                s += "(" + player.getName() + ")";
            mail.readNBT(Server.readNBT(buffer));
            mail.sender = s;
            mail.items = ((ContainerMail) player.openContainer).mail.items;

            if (mail.subject.isEmpty()) {
                NoppesUtilServer.sendGuiError(player, 1);
                return;
            }

            NBTTagCompound comp = new NBTTagCompound();
            comp.setString("username", username);
            NoppesUtilServer.sendGuiClose(player, 1, comp);

            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);

            if (npc != null && EventHooks.onNPCRole(npc, new RoleEvent.MailmanEvent(player, npc.wrappedNPC, mail))) {
                return;
            }

            PlayerDataController.instance.addPlayerMessage(player.getServer(), username, mail);
        } else if (type == EnumPlayerPacket.MailboxOpenMail) {
            long time = buffer.readLong();
            String username = Server.readString(buffer);
            player.closeContainer();
            PlayerMailData data = PlayerData.get(player).mailData;

            Iterator<PlayerMail> it = data.playermail.iterator();
            while (it.hasNext()) {
                PlayerMail mail = it.next();
                if (mail.time == time && mail.sender.equals(username)) {
                    ContainerMail.staticmail = mail;
                    player.openGui(CustomNpcs.instance, EnumGuiType.PlayerMailman.ordinal(), player.world, 0, 0, 0);
                    break;
                }
            }
        } else if (type == EnumPlayerPacket.MailRead) {
            long time = buffer.readLong();
            String username = Server.readString(buffer);
            PlayerMailData data = PlayerData.get(player).mailData;

            Iterator<PlayerMail> it = data.playermail.iterator();
            while (it.hasNext()) {
                PlayerMail mail = it.next();
                if (!mail.beenRead && mail.time == time && mail.sender.equals(username)) {
                    if (mail.hasQuest())
                        PlayerQuestController.addActiveQuest(mail.getQuest(), player);
                    mail.beenRead = true;
                }
            }
        }
    }
}
