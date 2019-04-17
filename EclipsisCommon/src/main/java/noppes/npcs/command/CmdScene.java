package noppes.npcs.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.entity.data.DataScenes.SceneState;

import java.util.Map.Entry;

public class CmdScene extends CommandNoppesBase {

    @Override
    public String getName() {
        return "scene";
    }

    @Override
    public String getDescription() {
        return "Scene operations";
    }

    @SubCommand(
            desc = "Get/Set scene time",
            usage = "[time] [name]",
            permission = 2
    )
    public void time(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sendMessage(sender, "Active scenes:");
            for (Entry<String, SceneState> entry : DataScenes.StartedScenes.entrySet())
                sendMessage(sender, "Scene %s time is %s", entry.getKey(), entry.getValue().ticks);
        } else if (args.length == 1) {
            int ticks = Integer.parseInt(args[0]);
            for (SceneState state : DataScenes.StartedScenes.values())
                state.ticks = ticks;
            sendMessage(sender, "All Scene times are set to " + ticks);
        } else {
            SceneState state = DataScenes.StartedScenes.get(args[1].toLowerCase());
            if (state == null)
                throw new CommandException("Unknown scene name %s", args[1]);
            state.ticks = Integer.parseInt(args[0]);
            sendMessage(sender, "Scene %s set to %s", args[1], state.ticks);
        }
    }

    @SubCommand(
            desc = "Reset scene",
            usage = "[name]",
            permission = 2
    )
    public void reset(MinecraftServer server, ICommandSender sender, String[] args) {
        DataScenes.Reset(sender, args.length == 0 ? null : args[0]);
    }

    @SubCommand(
            desc = "Start scene",
            usage = "<name>",
            permission = 2
    )
    public void start(MinecraftServer server, ICommandSender sender, String[] args) {
        DataScenes.Start(sender, args[0]);
    }

    @SubCommand(
            desc = "Pause scene",
            usage = "[name]",
            permission = 2
    )
    public void pause(MinecraftServer server, ICommandSender sender, String[] args) {
        DataScenes.Pause(sender, args.length == 0 ? null : args[0]);
    }
}
