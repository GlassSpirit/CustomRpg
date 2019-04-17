package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.gui.player.GuiQuestLog;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.constants.EnumPlayerPacket;
import org.lwjgl.input.Keyboard;

public class ClientTickHandler {

    private World prevWorld;
    private boolean otherContainer = false;


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == Phase.END)
            return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.player.openContainer instanceof ContainerPlayer) {
            if (otherContainer) {
                NoppesUtilPlayer.sendData(EnumPlayerPacket.CheckQuestCompletion);
                otherContainer = false;
            }
        } else
            otherContainer = true;
        CustomNpcs.ticks++;
        RenderNPCInterface.LastTextureTick++;
        if (prevWorld != mc.world) {
            prevWorld = mc.world;
            MusicController.Instance.stopMusic();
        }
    }

    private int buttonPressed = -1;
    private long buttonTime = 0;

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (CustomNpcs.SceneButtonsEnabled) {
            if (ClientProxy.Scene1.isPressed()) {
                Client.sendData(EnumPacketServer.SceneStart, 1);
            }
            if (ClientProxy.Scene2.isPressed()) {
                Client.sendData(EnumPacketServer.SceneStart, 2);
            }
            if (ClientProxy.Scene3.isPressed()) {
                Client.sendData(EnumPacketServer.SceneStart, 3);
            }
            if (ClientProxy.SceneReset.isPressed()) {
                Client.sendData(EnumPacketServer.SceneReset);
            }
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (ClientProxy.QuestLog.isPressed()) {
            if (mc.currentScreen == null)
                NoppesUtil.openGUI(mc.player, new GuiQuestLog(mc.player));
            else if (mc.currentScreen instanceof GuiQuestLog)
                mc.setIngameFocus();
        }

        int key = Keyboard.getEventKey();
        long time = Keyboard.getEventNanoseconds();
        if (Keyboard.getEventKeyState()) {
            if (!isIgnoredKey(key)) {
                buttonTime = time;
                buttonPressed = key;
            }
        } else {
            if (key == buttonPressed && time - buttonTime < 500000000 && mc.currentScreen == null) {
                boolean isCtrlPressed = Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
                boolean isShiftPressed = Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
                boolean isAltPressed = Keyboard.isKeyDown(Keyboard.KEY_RMENU) || Keyboard.isKeyDown(Keyboard.KEY_LMENU);
                boolean isMetaPressed = Keyboard.isKeyDown(Keyboard.KEY_RMETA) || Keyboard.isKeyDown(Keyboard.KEY_LMETA);
                NoppesUtilPlayer.sendData(EnumPlayerPacket.KeyPressed, key, isCtrlPressed, isShiftPressed, isAltPressed, isMetaPressed);
            }
            buttonPressed = -1;
            buttonTime = 0;
        }
    }

    @SubscribeEvent
    public void invoke(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getHand() != EnumHand.MAIN_HAND)
            return;
        NoppesUtilPlayer.sendData(EnumPlayerPacket.LeftClick);
    }

    private final int[] ignoreKeys = new int[]{Keyboard.KEY_RCONTROL, Keyboard.KEY_LCONTROL, Keyboard.KEY_RSHIFT, Keyboard.KEY_LSHIFT
            , Keyboard.KEY_RMENU, Keyboard.KEY_LMENU, Keyboard.KEY_RMETA, Keyboard.KEY_LMETA};

    private boolean isIgnoredKey(int key) {
        for (int i : ignoreKeys) {
            if (i == key)
                return true;
        }
        return false;
    }
}
