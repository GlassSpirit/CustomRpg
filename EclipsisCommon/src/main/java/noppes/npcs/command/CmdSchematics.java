package noppes.npcs.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.schematics.SchematicWrapper;

import java.util.List;

public class CmdSchematics extends CommandNoppesBase {

    @Override
    public String getName() {
        return "schema";
    }


    @Override
    public String getDescription() {
        return "Schematic operation";
    }


    @SubCommand(
            desc = "Build the schematic",
            usage = "<name> [rotation] [[world:]x,y,z]]",
            permission = 4
    )
    public void build(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String name = args[0];
        SchematicWrapper schem = SchematicController.Instance.load(name);
        if (schem == null) {
            throw new CommandException("Unknown schematic: " + name);
        }
        sendMessage(sender, "width: " + schem.schema.getWidth() + ", length: " + schem.schema.getLength() + ", height: " + schem.schema.getHeight());

        BlockPos pos = sender.getPosition();
        World world = sender.getEntityWorld();
        int rotation = 0;
        if (args.length > 1) {
            try {
                rotation = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {

            }
        }
        if (args.length > 2) {
            String location = args[2];
            String[] par;
            if (location.contains(":")) {
                par = location.split(":");
                location = par[1];
                world = getWorld(server, par[0]);
                if (world == null) {
                    throw new CommandException("'%s' is an unknown world", par[0]);
                }
            }

            if (location.contains(",")) {
                par = location.split(",");
                if (par.length != 3) {
                    throw new CommandException("Location should be x,y,z");
                }
                try {
                    pos = CommandBase.parseBlockPos(sender, par, 0, false);
                } catch (NumberInvalidException e) {
                    throw new CommandException("Location should be in numbers");
                }
            }
        }

        if (pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0) {//incase it was called from the console and not pos was given
            throw new CommandException("Location needed");
        }

        schem.init(pos, world, rotation);
        SchematicController.Instance.build(schem, sender);
    }

    @SubCommand(
            desc = "Stops the current build",
            permission = 4
    )
    public void stop(MinecraftServer server, ICommandSender sender, String[] args) {
        SchematicController.Instance.stop(sender);
    }

    @SubCommand(
            desc = "Gives info about the current build",
            permission = 4
    )
    public void info(MinecraftServer server, ICommandSender sender, String[] args) {
        SchematicController.Instance.info(sender);
    }

    @SubCommand(
            desc = "Lists available schematics",
            permission = 4
    )
    public void list(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        List<String> list = SchematicController.Instance.list();
        if (list.isEmpty()) {
            throw new CommandException("No available schematics");
        }
        String s = "";
        for (String file : list) {
            s += file + ", ";
        }
        sendMessage(sender, s);
    }

    @Override
    public List getTabCompletions(MinecraftServer server, ICommandSender par1, String[] args, BlockPos pos) {
        if (args[0].equalsIgnoreCase("build") && args.length == 2) {
            List<String> list = SchematicController.Instance.list();
            return CommandBase.getListOfStringsMatchingLastWord(args, list.toArray(new String[list.size()]));
        }
        return null;
    }

    public World getWorld(MinecraftServer server, String t) {
        WorldServer[] ws = server.worlds;
        for (WorldServer w : ws) {
            if (w != null) {
                if ((w.provider.getDimension() + "").equalsIgnoreCase(t)) {
                    return w;
                }
            }
        }
        return null;
    }
}
