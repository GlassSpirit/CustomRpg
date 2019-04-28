package noppes.npcs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.MassBlockController;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.entity.data.DataScenes.SceneContainer;
import noppes.npcs.entity.data.DataScenes.SceneState;

import java.util.ArrayList;

public class ServerTickHandler {
    public int ticks = 0;
    private String serverName = null;

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
            DataScenes.ScenesToRun = new ArrayList<>();

        }
    }

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        SyncController.syncPlayer((EntityPlayerMP) event.player);
    }
}
