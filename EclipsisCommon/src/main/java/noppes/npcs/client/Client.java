package noppes.npcs.client;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.util.CustomNPCsScheduler;

import java.io.IOException;

public class Client {

    public static void sendData(final EnumPacketServer type, final Object... obs) {
        CustomNPCsScheduler.runTack(() -> {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            try {
                if (!Server.fillBuffer(buffer, type, obs))
                    return;
                LogWriter.debug("Send: " + type);
                CustomNpcs.Channel.sendToServer(new FMLProxyPacket(buffer, "CustomNPCs"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}
