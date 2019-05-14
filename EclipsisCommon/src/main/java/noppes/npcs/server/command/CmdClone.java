package noppes.npcs.server.command;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.List;

public class CmdClone extends CommandNoppesBase {

    @Override
    public String getName() {
        return "clone";
    }

    @Override
    public String getDescription() {
        return "Clone operation (server side)";
    }

    @SubCommand(
            desc = "Add NPC(s) to clone storage",
            usage = "<npc> <tab> [clonedname]",
            permission = 4
    )
    public void add(MinecraftServer server, ICommandSender sender, String[] args) {
        int tab = 0;
        try {
            tab = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {

        }
        List<EntityNPCInterface> list = getEntities(EntityNPCInterface.class, sender.getEntityWorld(), sender.getPosition(), 80);
        for (EntityNPCInterface npc : list) {
            if (npc.display.getName().equalsIgnoreCase(args[0])) {
                String name = npc.display.getName();
                if (args.length > 2) {
                    name = args[2];
                }
                NBTTagCompound compound = new NBTTagCompound();
                if (!npc.writeToNBTAtomically(compound))
                    return;
                ServerCloneController.Instance.addClone(compound, name, tab);
            }
        }
    }

    @SubCommand(
            desc = "List NPC from clone storage",
            usage = "<tab>",
            permission = 2
    )
    public void list(MinecraftServer server, ICommandSender sender, String[] args) {
        sendMessage(sender, "--- Stored NPCs --- (server side)");
        int tab = 0;
        try {
            tab = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {

        }
        for (String name : ServerCloneController.Instance.getClones(tab)) {
            sendMessage(sender, name);
        }
        sendMessage(sender, "------------------------------------");
    }

    @SubCommand(
            desc = "Remove NPC from clone storage",
            usage = "<name> <tab>",
            permission = 4
    )
    public void del(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String nametodel = args[0];
        int tab = 0;
        try {
            tab = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {

        }
        boolean deleted = false;
        for (String name : ServerCloneController.Instance.getClones(tab)) {
            if (nametodel.equalsIgnoreCase(name)) {
                ServerCloneController.Instance.removeClone(name, tab);
                deleted = true;
                break;
            }
        }
        if (!ServerCloneController.Instance.removeClone(nametodel, tab)) {
            throw new CommandException("Npc '%s' wasn't found", nametodel);
        }
    }

    @SubCommand(
            desc = "spawn cloned NPC",
            usage = "<name> <tab> [[world:]x,y,z]] [newname]",
            permission = 2
    )
    public void spawn(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String name = args[0].replaceAll("%", " "); // if name of npc separed by space, user must use % in place of space
        int tab = 0;
        try {
            tab = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {

        }
        String newname = null;
        NBTTagCompound compound = ServerCloneController.Instance.getCloneData(sender, name, tab);
        if (compound == null) {
            throw new CommandException("Unknown npc");
        }
        World world = sender.getEntityWorld();
        BlockPos pos = sender.getPosition();

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
                    throw new CommandException("Location need be x,y,z");
                }
                try {
                    pos = CommandBase.parseBlockPos(sender, par, 0, false);
                } catch (NumberInvalidException e) {
                    throw new CommandException("Location should be in numbers");
                }
                if (args.length > 3) {
                    newname = args[3];
                }
            } else {
                newname = location;
            }
        }

        if (pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0) {//incase it was called from the console and not pos was given
            throw new CommandException("Location needed");
        }

        Entity entity = EntityList.createEntityFromNBT(compound, world);
        entity.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface) entity;
            npc.ais.setStartPos(pos);
            if (newname != null && !newname.isEmpty())
                npc.display.setName(newname.replaceAll("%", " ")); // like name, newname must use % in place of space to keep a logical way
        }
        world.spawnEntity(entity);
    }

    @SubCommand(
            desc = "spawn multiple cloned NPC in a grid",
            usage = "<name> <tab> <lenght> <width> [[world:]x,y,z]] [newname]",
            permission = 2)
    public boolean grid(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String name = args[0].replaceAll("%", " ");
        int tab = 0;
        try {
            tab = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {

        }
        int width, height;
        try {
            width = Integer.parseInt(args[2]);
            height = Integer.parseInt(args[3]);
        } catch (NumberFormatException ex) {
            throw new CommandException("lenght or width wasnt a number");
        }

        String newname = null;
        NBTTagCompound compound = ServerCloneController.Instance.getCloneData(sender, name, tab);
        if (compound == null) {
            throw new CommandException("Unknown npc");
        }
        World world = sender.getEntityWorld();
        BlockPos curpos = sender.getPosition();

        if (args.length > 4) {
            String location = args[4];
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
                    throw new CommandException("Location need be x,y,z");
                }
                try {
                    curpos = CommandBase.parseBlockPos(sender, par, 0, false);
                } catch (NumberInvalidException e) {
                    throw new CommandException("Location should be in numbers");
                }
                if (args.length > 5) {
                    newname = args[5];
                }
            } else {
                newname = location;
            }
        }

        if (curpos.getX() == 0 && curpos.getY() == 0 && curpos.getZ() == 0) {//incase it was called from the console and not pos was given
            throw new CommandException("Location needed");
        }

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {

                BlockPos npcpos = curpos.add(x, -2, z);

                for (int y = 0; y < 10; y++) {
                    BlockPos pos = npcpos.up(y);
                    BlockPos pos2 = pos.up();
                    IBlockState b = world.getBlockState(pos);
                    IBlockState b2 = world.getBlockState(pos2);
                    if (b.causesSuffocation() && !b2.causesSuffocation()) {
                        npcpos = pos;
                        break;
                    }
                }


                Entity entity = EntityList.createEntityFromNBT(compound, world);
                entity.setPosition(npcpos.getX() + 0.5, npcpos.getY() + 1, npcpos.getZ() + 0.5);
                if (entity instanceof EntityNPCInterface) {
                    EntityNPCInterface npc = (EntityNPCInterface) entity;
                    npc.ais.setStartPos(npcpos);
                    if (newname != null && !newname.isEmpty())
                        npc.display.setName(newname.replaceAll("%", " ")); // like name, newname must use % in place of space to keep a logical way
                }
                world.spawnEntity(entity);
            }
        }
        return true;
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

    public <T extends Entity> List<T> getEntities(Class<? extends T> cls, World world, BlockPos pos, int range) {
        return world.getEntitiesWithinAABB(cls, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(range, range, range));
    }
}
