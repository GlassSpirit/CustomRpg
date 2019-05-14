package noppes.npcs.server.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.controllers.ScriptController;


public class CmdScript extends CommandNoppesBase {

    @SubCommand(desc = "Reload scripts and saved data from disks script folder.")
    public Boolean reload(MinecraftServer server, ICommandSender sender, String[] args) {
        ScriptController.Instance.loadCategories();

        if (ScriptController.Instance.loadPlayerScripts())
            sender.sendMessage(new TextComponentString("Reload player scripts succesfully"));
        else
            sender.sendMessage(new TextComponentString("Failed reloading player scripts"));

        if (ScriptController.Instance.loadPlayerScripts())
            sender.sendMessage(new TextComponentString("Reload forge scripts succesfully"));
        else
            sender.sendMessage(new TextComponentString("Failed reloading forge scripts"));

        if (ScriptController.Instance.loadStoredData())
            sender.sendMessage(new TextComponentString("Reload stored data succesfully"));
        else
            sender.sendMessage(new TextComponentString("Failed reloading stored data"));
        return true;
    }

    @Override
    public String getName() {
        return "script";
    }

    @Override
    public String getDescription() {
        return "Commands for scripts";
    }
}
