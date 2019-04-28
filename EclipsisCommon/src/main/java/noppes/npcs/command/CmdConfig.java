package noppes.npcs.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsConfig;
import noppes.npcs.Server;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.ChunkController;

import java.util.Arrays;

public class CmdConfig extends CommandNoppesBase {


    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getDescription() {
        return "Some config things you can set";
    }

    @SubCommand(
            desc = "Freezes/Unfreezes npcs",
            usage = "[true/false]"
    )
    public void freezenpcs(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "Frozen NPCs: " + CustomNpcs.FreezeNPCs);
        } else {
            CustomNpcs.FreezeNPCs = Boolean.parseBoolean(args[0]);
            sendMessage(sender, "FrozenNPCs is now " + CustomNpcs.FreezeNPCs);
        }
    }

    @SubCommand(
            desc = "Add debug info to log",
            usage = "<true/false>"
    )
    public void debug(MinecraftServer server, ICommandSender sender, String[] args) {
        CustomNpcs.VerboseDebug = Boolean.parseBoolean(args[0]);
        sendMessage(sender, "Verbose debug is now " + CustomNpcs.VerboseDebug);
    }

    @SubCommand(
            desc = "Enables/Disables scripting",
            usage = "[true/false]"
    )
    public void scripting(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "Scripting: " + CustomNpcsConfig.EnableScripting);
        } else {
            CustomNpcsConfig.EnableScripting = Boolean.parseBoolean(args[0]);
            CustomNpcsConfig.INSTANCE.updateConfig();
            sendMessage(sender, "Scripting is now " + CustomNpcsConfig.EnableScripting);
        }
    }

    @SubCommand(
            desc = "Set how many active chunkloaders you can have",
            usage = "<number>"
    )
    public void chunkloaders(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sendMessage(sender, "ChunkLoaders: " + ChunkController.instance.size() + "/" + CustomNpcsConfig.ChuckLoaders);
        } else {
            try {
                CustomNpcsConfig.ChuckLoaders = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                throw new CommandException("Didnt get a number");
            }
            CustomNpcsConfig.INSTANCE.updateConfig();

            int size = ChunkController.instance.size();
            if (size > CustomNpcsConfig.ChuckLoaders) {
                ChunkController.instance.unload(size - CustomNpcsConfig.ChuckLoaders);
                sendMessage(sender, size - CustomNpcsConfig.ChuckLoaders + " chunksloaders unloaded");
            }
            sendMessage(sender, "ChunkLoaders: " + ChunkController.instance.size() + "/" + CustomNpcsConfig.ChuckLoaders);
        }
    }

    @SubCommand(
            desc = "Get/Set font",
            usage = "[type] [size]",
            permission = 2
    )
    public void font(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP))
            return;
        int size = 18;
        if (args.length > 1) {
            try {
                size = Integer.parseInt(args[args.length - 1]);
                args = Arrays.copyOfRange(args, 0, args.length - 1);
            } catch (Exception e) {
            }
        }
        StringBuilder font = new StringBuilder();
        for (String arg : args) {
            font.append(" ").append(arg);
        }
        Server.sendData((EntityPlayerMP) sender, EnumPacketClient.CONFIG, 0, font.toString().trim(), size);
    }

}
