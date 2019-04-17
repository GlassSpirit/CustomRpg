package noppes.npcs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import noppes.npcs.client.AnalyticsTracking;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.MassBlockController;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.entity.data.DataScenes.SceneContainer;
import noppes.npcs.entity.data.DataScenes.SceneState;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServerTickHandler {
    public int ticks = 0;

    @SubscribeEvent
    public void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.side != Side.SERVER || event.phase != Phase.START)
            return;
        EntityPlayer player = event.player;
        PlayerData data = PlayerData.get(player);

        if (data.updateClient) {
            Server.sendData((EntityPlayerMP) player, EnumPacketClient.SYNC_END, SyncType.PLAYER_DATA, data.getSyncNBT());
            data.updateClient = false;
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER && event.phase == Phase.START) {
            NPCSpawning.findChunksForSpawning((WorldServer) event.world);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side == Side.SERVER && event.phase == Phase.START && ticks++ >= 20) {
            SchematicController.Instance.updateBuilding();
            MassBlockController.Update();

            ticks = 0;
            for (SceneState state : DataScenes.StartedScenes.values()) {
                if (!state.paused)
                    state.ticks++;
            }
            for (SceneContainer entry : DataScenes.ScenesToRun) {
                entry.update();
            }
            DataScenes.ScenesToRun = new ArrayList<SceneContainer>();

        }
    }

    private String serverName = null;

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (serverName == null) {
            String e = "local";
            MinecraftServer server = event.player.getServer();
            if (server.isDedicatedServer()) {
                try {
                    e = InetAddress.getByName(server.getServerHostname()).getCanonicalHostName();
                } catch (UnknownHostException e1) {
                    e = server.getServerHostname();
                }
                if (server.getServerPort() != 25565)
                    e += ":" + server.getServerPort();
            }
            if (e == null || e.startsWith("192.168") || e.contains("127.0.0.1") || e.startsWith("localhost"))
                e = "local";
            serverName = e;
        }
        AnalyticsTracking.sendData(event.player, "join", serverName);

        SyncController.syncPlayer((EntityPlayerMP) event.player);
    }
}
