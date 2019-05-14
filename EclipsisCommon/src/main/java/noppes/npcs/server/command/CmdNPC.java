package noppes.npcs.server.command;

import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.common.entity.EntityCustomNpc;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

public class CmdNPC extends CommandNoppesBase {

    public EntityNPCInterface selectedNpc;


    @Override
    public String getName() {
        return "npc";
    }

    @Override
    public String getDescription() {
        return "NPC operation";
    }

    @Override
    public String getUsage() {
        return "<name> <command>";
    }

    @Override
    public boolean runSubCommands() {
        return false;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String npcname = args[0].replace("%", " ");
        String command = args[1];
        args = Arrays.copyOfRange(args, 2, args.length);
        if (command.equalsIgnoreCase("create")) {
            args = ArrayUtils.add(args, 0, npcname);
            executeSub(server, sender, command, args);
            return;
        }

        List<EntityNPCInterface> list = getEntities(EntityNPCInterface.class, sender.getEntityWorld(), sender.getPosition(), 80);
        for (EntityNPCInterface npc : list) {
            String name = npc.display.getName().replace(" ", "_");
            if (name.equalsIgnoreCase(npcname)) {
                if (selectedNpc == null || selectedNpc.getDistanceSq(sender.getPosition()) > npc.getDistanceSq(sender.getPosition()))
                    selectedNpc = npc;
            }
        }
        if (selectedNpc == null)
            throw new CommandException("Npc '%s' was not found", npcname);

        executeSub(server, sender, command, args);
        selectedNpc = null;

    }

    @SubCommand(
            desc = "Set Home (respawn place)",
            usage = "[x] [y] [z]",
            permission = 2
    )
    public void home(MinecraftServer server, ICommandSender sender, String[] args) {
        BlockPos pos = sender.getPosition();

        if (args.length == 3) {
            try {
                pos = CommandBase.parseBlockPos(sender, args, 0, false);
            } catch (NumberInvalidException e) {
            }
        }

        selectedNpc.ais.setStartPos(pos);
    }

    @SubCommand(
            desc = "Set NPC visibility",
            usage = "[true/false/semi]",
            permission = 2
    )
    public void visible(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length < 1)
            return;
        boolean bo = args[0].equalsIgnoreCase("true");
        boolean semi = args[0].equalsIgnoreCase("semi");

        int current = selectedNpc.display.getVisible();
        if (semi)
            selectedNpc.display.setVisible(2);
        else if (bo)
            selectedNpc.display.setVisible(0);
        else
            selectedNpc.display.setVisible(1);
    }

    @SubCommand(desc = "Delete an NPC")
    public void delete(MinecraftServer server, ICommandSender sender, String[] args) {
        selectedNpc.delete();
    }

    @SubCommand(
            desc = "Sets the owner of an follower/companion",
            usage = "[player]",
            permission = 2
    )
    public void owner(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length < 1) {
            EntityPlayer player = null;
            if (selectedNpc.roleInterface instanceof RoleFollower)
                player = ((RoleFollower) selectedNpc.roleInterface).owner;

            if (selectedNpc.roleInterface instanceof RoleCompanion)
                player = ((RoleCompanion) selectedNpc.roleInterface).owner;

            if (player == null)
                sendMessage(sender, "No owner");
            else
                sendMessage(sender, "Owner is: " + player.getName());
        } else {
            EntityPlayerMP player = null;
            try {
                player = CommandBase.getPlayer(server, sender, args[0]);
            } catch (PlayerNotFoundException e) {

            } catch (CommandException e) {
            }
            if (selectedNpc.roleInterface instanceof RoleFollower)
                ((RoleFollower) selectedNpc.roleInterface).setOwner(player);

            if (selectedNpc.roleInterface instanceof RoleCompanion)
                ((RoleCompanion) selectedNpc.roleInterface).setOwner(player);
        }
    }


    @SubCommand(
            desc = "Set NPC name",
            usage = "[name]",
            permission = 2
    )
    public void name(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length < 1)
            return;

        String name = args[0];
        for (int i = 1; i < args.length; i++) {
            name += " " + args[i];
        }

        if (!selectedNpc.display.getName().equals(name)) {
            selectedNpc.display.setName(name);
            selectedNpc.updateClient = true;
        }
    }


    @SubCommand(
            desc = "Resets the npc",
            usage = "[name]",
            permission = 2
    )
    public void reset(MinecraftServer server, ICommandSender sender, String[] args) {
        selectedNpc.reset();
    }

    @SubCommand(
            desc = "Creates an NPC",
            usage = "[name]"
    )
    public void create(MinecraftServer server, ICommandSender sender, String[] args) {
        World pw = sender.getEntityWorld();
        EntityCustomNpc npc = new EntityCustomNpc(pw);
        if (args.length > 0)
            npc.display.setName(args[0]);
        BlockPos pos = sender.getPosition();
        npc.setPositionAndRotation(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
        npc.ais.setStartPos(pos);
        pw.spawnEntity(npc);
        npc.setHealth(npc.getMaxHealth());
    }


    @Override
    public List getTabCompletions(MinecraftServer server, ICommandSender par1, String[] args, BlockPos pos) {
        if (args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "create", "home", "visible", "delete", "owner", "name");
        }
        if (args.length == 3 && args[1].equalsIgnoreCase("owner")) {
            return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    public <T extends Entity> List<T> getEntities(Class<? extends T> cls, World world, BlockPos pos, int range) {
        return world.getEntitiesWithinAABB(cls, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(range, range, range));
    }
}
