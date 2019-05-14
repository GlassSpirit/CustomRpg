package noppes.npcs.server.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.common.entity.EntityDialogNpc;

import java.util.List;

public class CmdDialog extends CommandNoppesBase {

    @Override
    public String getName() {
        return "dialog";
    }

    @Override
    public String getDescription() {
        return "Dialog operations";
    }

    @SubCommand(
            desc = "force read",
            usage = "<player> <dialog>",
            permission = 2
    )
    public void read(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        int diagid;
        try {
            diagid = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            throw new CommandException("DialogID must be an integer");
        }
        List<PlayerData> data = PlayerDataController.instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            throw new CommandException("Unknow player '%s'", playername);
        }
        for (PlayerData playerdata : data) {
            playerdata.dialogData.dialogsRead.add(diagid);
            playerdata.save(true);
        }
    }

    @SubCommand(
            desc = "force unread dialog",
            usage = "<player> <dialog>",
            permission = 2
    )
    public void unread(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        int diagid;
        try {
            diagid = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            throw new CommandException("DialogID must be an integer");
        }
        List<PlayerData> data = PlayerDataController.instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            throw new CommandException("Unknow player '%s'", playername);
        }
        for (PlayerData playerdata : data) {
            playerdata.dialogData.dialogsRead.remove(diagid);
            playerdata.save(true);
        }
    }

    @SubCommand(
            desc = "reload dialogs from disk",
            permission = 4
    )
    public void reload(MinecraftServer server, ICommandSender sender, String[] args) {
        new DialogController().load();
        SyncController.syncAllDialogs(server);
    }

    @SubCommand(
            desc = "show dialog",
            usage = "<player> <dialog> <name>",
            permission = 2
    )
    public void show(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        List<EntityPlayerMP> players = CommandBase.getPlayers(server, sender, args[0]);
        if (players == null) {
            throw new CommandException("Unknow player '%s'", args[0]);
        }

        int diagid;
        try {
            diagid = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            throw new CommandException("DialogID must be an integer: " + args[1]);
        }
        Dialog dialog = DialogController.instance.dialogs.get(diagid);
        if (dialog == null) {
            throw new CommandException("Unknown dialog id: " + args[1]);
        }

        EntityDialogNpc npc = new EntityDialogNpc(sender.getEntityWorld());
        DialogOption option = new DialogOption();
        option.dialogId = diagid;
        option.title = dialog.title;
        npc.dialogs.put(0, option);
        npc.display.setName(args[2]);

        for (EntityPlayer player : players) {
            EntityUtil.Copy(player, npc);
            NoppesUtilServer.openDialog(player, npc, dialog);
        }
    }
}
