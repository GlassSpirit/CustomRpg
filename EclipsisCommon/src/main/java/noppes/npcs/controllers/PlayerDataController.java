package noppes.npcs.controllers;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.LogWriter;
import noppes.npcs.common.CustomNpcs;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.PlayerBankData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.util.NBTJsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerDataController {
    public static PlayerDataController instance;
    public Map<String, String> nameUUIDs;

    public PlayerDataController() {
        instance = this;
        File dir = CustomNpcs.INSTANCE.getWorldSaveDirectory("playerdata");
        Map<String, String> map = new HashMap<>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory() || !file.getName().endsWith(".json"))
                continue;
            try {
                NBTTagCompound compound = NBTJsonUtil.LoadFile(file);
                if (compound.hasKey("PlayerName")) {
                    map.put(compound.getString("PlayerName"), file.getName().substring(0, file.getName().length() - 5));
                }
            } catch (Exception e) {
                LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            }
        }
        nameUUIDs = map;
    }

    public PlayerBankData getBankData(EntityPlayer player, int bankId) {
        Bank bank = BankController.getInstance().getBank(bankId);
        PlayerBankData data = PlayerData.get(player).bankData;
        if (!data.hasBank(bank.id)) {
            data.loadNew(bank.id);
        }
        return data;
    }

    public String hasPlayer(String username) {
        for (String name : nameUUIDs.keySet()) {
            if (name.equalsIgnoreCase(username))
                return name;
        }

        return "";
    }

    public PlayerData getDataFromUsername(MinecraftServer server, String username) {
        EntityPlayer player = server.getPlayerList().getPlayerByUsername(username);
        PlayerData data = null;
        if (player == null) {
            for (String name : nameUUIDs.keySet()) {
                if (name.equalsIgnoreCase(username)) {
                    data = new PlayerData();
                    data.setNBT(PlayerData.loadPlayerData(nameUUIDs.get(name)));
                    break;
                }
            }
        } else
            data = PlayerData.get(player);

        return data;
    }

    public void addPlayerMessage(MinecraftServer server, String username, PlayerMail mail) {
        mail.time = System.currentTimeMillis();

        PlayerData data = getDataFromUsername(server, username);
        data.mailData.playermail.add(mail.copy());
        data.save(false);
    }

    public List<PlayerData> getPlayersData(ICommandSender sender, String username) throws CommandException {
        ArrayList<PlayerData> list = new ArrayList<>();
        List<EntityPlayerMP> players = EntitySelector.matchEntities(sender, username, EntityPlayerMP.class);
        if (players.isEmpty()) {
            PlayerData data = getDataFromUsername(sender.getServer(), username);
            if (data != null)
                list.add(data);
        } else {
            for (EntityPlayer player : players) {
                list.add(PlayerData.get(player));
            }
        }

        return list;
    }
}
