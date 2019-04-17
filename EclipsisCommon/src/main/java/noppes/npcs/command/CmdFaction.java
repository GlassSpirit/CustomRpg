package noppes.npcs.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerFactionData;

import java.util.Arrays;
import java.util.List;

public class CmdFaction extends CommandNoppesBase {

    public Faction selectedFaction;
    public List<PlayerData> data;


    @Override
    public String getName() {
        return "faction";
    }

    @Override
    public String getDescription() {
        return "Faction operations";
    }

    @Override
    public String getUsage() {
        return "<player> <faction> <command>";
    }

    @Override
    public boolean runSubCommands() {
        return false;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        String factionname = args[1];

        data = PlayerDataController.instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            throw new CommandException("Unknow player '%s'", playername);
        }

        try {
            selectedFaction = FactionController.instance.getFaction(Integer.parseInt(factionname));
        } catch (NumberFormatException e) {
            selectedFaction = FactionController.instance.getFactionFromName(factionname);
        }

        if (selectedFaction == null) {
            throw new CommandException("Unknow facion '%s", factionname);
        }

        executeSub(server, sender, args[2], Arrays.copyOfRange(args, 3, args.length));
    }

    @SubCommand(
            desc = "Add points",
            usage = "<points>"
    )
    public void add(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int points;
        try {
            points = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            throw new CommandException("Must be an integer");
        }
        int factionid = this.selectedFaction.id;

        for (PlayerData playerdata : data) {
            PlayerFactionData playerfactiondata = playerdata.factionData;
            playerfactiondata.increasePoints(playerdata.player, factionid, points);
            playerdata.save(true);
        }
    }

    @SubCommand(
            desc = "Substract points",
            usage = "<points>"
    )
    public void subtract(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int points;
        try {
            points = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            throw new CommandException("Must be an integer");
        }
        int factionid = this.selectedFaction.id;
        for (PlayerData playerdata : data) {
            PlayerFactionData playerfactiondata = playerdata.factionData;
            playerfactiondata.increasePoints(playerdata.player, factionid, -points);
            playerdata.save(true);
        }
    }

    @SubCommand(desc = "Reset points to default")
    public void reset(MinecraftServer server, ICommandSender sender, String[] args) {
        for (PlayerData playerdata : data) {
            playerdata.factionData.factionData.put(this.selectedFaction.id, this.selectedFaction.defaultPoints);
            playerdata.save(true);
        }
    }

    @SubCommand(
            desc = "Set points",
            usage = "<points>"
    )
    public void set(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int points;
        try {
            points = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            throw new CommandException("Must be an integer");
        }
        for (PlayerData playerdata : data) {
            PlayerFactionData playerfactiondata = playerdata.factionData;
            playerfactiondata.factionData.put(this.selectedFaction.id, points);
            playerdata.save(true);
        }
    }

    @SubCommand(desc = "Drop relationship")
    public void drop(MinecraftServer server, ICommandSender sender, String[] args) {
        for (PlayerData playerdata : data) {
            playerdata.factionData.factionData.remove(this.selectedFaction.id);
            playerdata.save(true);
        }
    }

    @Override
    public List getTabCompletions(MinecraftServer server, ICommandSender par1, String[] args, BlockPos pos) {
        if (args.length == 3) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "add", "subtract", "set", "reset", "drop", "create");
        }
        return null;
    }
}
