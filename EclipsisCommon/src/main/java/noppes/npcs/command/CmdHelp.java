package noppes.npcs.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.api.CommandNoppesBase;

import java.lang.reflect.Method;
import java.util.Map.Entry;

public class CmdHelp extends CommandNoppesBase {
    private CommandNoppes parent;

    public CmdHelp(CommandNoppes parent) {
        this.parent = parent;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "help [command]";
    }


    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sendMessage(sender, "------Noppes Commands------");
            for (Entry<String, CommandNoppesBase> entry : parent.map.entrySet()) {
                sendMessage(sender, entry.getKey() + ": " + entry.getValue().getUsage(sender));
            }
            return;
        }

        CommandNoppesBase command = parent.getCommand(args);
        if (command == null)
            throw new CommandException("Unknown command " + args[0]);

        if (command.subcommands.isEmpty()) {
            sender.sendMessage(new TextComponentTranslation(command.getUsage(sender)));
            return;
        }

        Method m = null;
        if (args.length > 1) {
            m = command.subcommands.get(args[1].toLowerCase());
        }
        if (m == null) {
            sendMessage(sender, "------" + command.getName() + " SubCommands------");
            for (Entry<String, Method> entry : command.subcommands.entrySet()) {
                sender.sendMessage(new TextComponentTranslation(entry.getKey() + ": " + entry.getValue().getAnnotation(SubCommand.class).desc()));
            }
        } else {
            sendMessage(sender, "------" + command.getName() + "." + args[1].toLowerCase() + " Command------");
            SubCommand sc = m.getAnnotation(SubCommand.class);
            sender.sendMessage(new TextComponentTranslation(sc.desc()));
            if (!sc.usage().isEmpty())
                sender.sendMessage(new TextComponentTranslation("Usage: " + sc.usage()));
        }
    }
}
