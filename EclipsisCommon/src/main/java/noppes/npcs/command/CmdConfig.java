package noppes.npcs.command;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockVine;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.Server;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.ChunkController;

import java.util.Arrays;
import java.util.Set;

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
            desc = "Disable/Enable the natural leaves decay",
            usage = "[true/false]"
    )
    public void leavesdecay(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "LeavesDecay: " + CustomNpcs.LeavesDecayEnabled);
        } else {
            CustomNpcs.LeavesDecayEnabled = Boolean.parseBoolean(args[0]);
            CustomNpcs.Config.updateConfig();
            Set<ResourceLocation> names = Block.REGISTRY.getKeys();
            for (ResourceLocation name : names) {
                Block block = Block.REGISTRY.getObject(name);
                if (block instanceof BlockLeaves) {
                    block.setTickRandomly(CustomNpcs.LeavesDecayEnabled);
                }
            }
            sendMessage(sender, "LeavesDecay is now " + CustomNpcs.LeavesDecayEnabled);
        }
    }

    @SubCommand(
            desc = "Disable/Enable the vines growing",
            usage = "[true/false]"
    )
    public void vinegrowth(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "VineGrowth: " + CustomNpcs.VineGrowthEnabled);
        } else {
            CustomNpcs.VineGrowthEnabled = Boolean.parseBoolean(args[0]);
            CustomNpcs.Config.updateConfig();
            Set<ResourceLocation> names = Block.REGISTRY.getKeys();
            for (ResourceLocation name : names) {
                Block block = Block.REGISTRY.getObject(name);
                if (block instanceof BlockVine) {
                    block.setTickRandomly(CustomNpcs.VineGrowthEnabled);
                }
            }
            sendMessage(sender, "VineGrowth is now " + CustomNpcs.VineGrowthEnabled);
        }
    }

    @SubCommand(
            desc = "Disable/Enable the ice melting",
            usage = "[true/false]"
    )
    public void icemelts(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "IceMelts: " + CustomNpcs.IceMeltsEnabled);
        } else {
            CustomNpcs.IceMeltsEnabled = Boolean.parseBoolean(args[0]);
            CustomNpcs.Config.updateConfig();
            Set<ResourceLocation> names = Block.REGISTRY.getKeys();
            for (ResourceLocation name : names) {
                Block block = Block.REGISTRY.getObject(name);
                if (block instanceof BlockIce) {
                    block.setTickRandomly(CustomNpcs.IceMeltsEnabled);
                }
            }
            sendMessage(sender, "IceMelts is now " + CustomNpcs.IceMeltsEnabled);
        }
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
            sendMessage(sender, "Scripting: " + CustomNpcs.EnableScripting);
        } else {
            CustomNpcs.EnableScripting = Boolean.parseBoolean(args[0]);
            CustomNpcs.Config.updateConfig();
            sendMessage(sender, "Scripting is now " + CustomNpcs.EnableScripting);
        }
    }

    @SubCommand(
            desc = "Set how many active chunkloaders you can have",
            usage = "<number>"
    )
    public void chunkloaders(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sendMessage(sender, "ChunkLoaders: " + ChunkController.instance.size() + "/" + CustomNpcs.ChuckLoaders);
        } else {
            try {
                CustomNpcs.ChuckLoaders = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                throw new CommandException("Didnt get a number");
            }
            CustomNpcs.Config.updateConfig();

            int size = ChunkController.instance.size();
            if (size > CustomNpcs.ChuckLoaders) {
                ChunkController.instance.unload(size - CustomNpcs.ChuckLoaders);
                sendMessage(sender, size - CustomNpcs.ChuckLoaders + " chunksloaders unloaded");
            }
            sendMessage(sender, "ChunkLoaders: " + ChunkController.instance.size() + "/" + CustomNpcs.ChuckLoaders);
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
        String font = "";
        for (int i = 0; i < args.length; i++) {
            font += " " + args[i];
        }
        Server.sendData((EntityPlayerMP) sender, EnumPacketClient.CONFIG, 0, font.trim(), size);
    }

}
