package noppes.npcs.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.api.CommandNoppesBase.SubCommand;
import noppes.npcs.api.CustomNPCsException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandNoppes extends CommandBase {

    public Map<String, CommandNoppesBase> map = new HashMap<String, CommandNoppesBase>();
    public CmdHelp help = new CmdHelp(this);

    public CommandNoppes() {
        registerCommand(help);
        registerCommand(new CmdScript());
        registerCommand(new CmdScene());
        registerCommand(new CmdSlay());
        registerCommand(new CmdQuest());
        registerCommand(new CmdDialog());
        registerCommand(new CmdSchematics());
        registerCommand(new CmdFaction());
        registerCommand(new CmdNPC());
        registerCommand(new CmdClone());
        registerCommand(new CmdConfig());
        registerCommand(new CmdMark());
    }

    public void registerCommand(CommandNoppesBase command) {
        String name = command.getName().toLowerCase();
        if (map.containsKey(name))
            throw new CustomNPCsException("Already a subcommand with the name: " + name);
        map.put(name, command);
    }

    @Override
    public String getName() {
        return "noppes";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Use as /noppes subcommand";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            help.execute(server, sender, args);
            return;
        }

        CommandNoppesBase command = getCommand(args);
        if (command == null)
            throw new CommandException("Unknown command " + args[0]);

        args = Arrays.copyOfRange(args, 1, args.length);
        if (command.subcommands.isEmpty() || !command.runSubCommands()) {
            if (!sender.canUseCommand(command.getRequiredPermissionLevel(), "commands.noppes." + command.getName().toLowerCase()))
                throw new CommandException("You are not allowed to use this command");
            command.canRun(server, sender, command.getUsage(), args);
            command.execute(server, sender, args);
            return;
        }

        if (args.length == 0) {
            help.execute(server, sender, new String[]{command.getName()});
            return;
        }

        command.executeSub(server, sender, args[0], Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, map.keySet());
        CommandNoppesBase command = getCommand(args);
        if (command == null)
            return null;
        if (args.length == 2 && command.runSubCommands())
            return CommandBase.getListOfStringsMatchingLastWord(args, command.subcommands.keySet());
        String[] useArgs = command.getUsage().split(" ");
        if (command.runSubCommands()) {
            Method m = command.subcommands.get(args[1].toLowerCase());
            if (m != null) {
                useArgs = m.getAnnotation(SubCommand.class).usage().split(" ");
            }
        }
        if (args.length <= useArgs.length + 2) {
            String usage = useArgs[args.length - 3];
            if (usage.equals("<player>") || usage.equals("[player]")) {
                return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            }
        }
        return command.getTabCompletions(server, sender, Arrays.copyOfRange(args, 1, args.length), pos);
    }

    public CommandNoppesBase getCommand(String[] args) {
        if (args.length == 0)
            return null;
        return map.get(args[0].toLowerCase());
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
}
