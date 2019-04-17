package noppes.npcs;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.util.CustomNPCsScheduler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Server {

    public static void sendData(final EntityPlayerMP player, final EnumPacketClient enu, final Object... obs) {
        sendDataDelayed(player, enu, 0, obs);
    }

    public static void sendDataDelayed(final EntityPlayerMP player, final EnumPacketClient type, int delay, final Object... obs) {
        CustomNPCsScheduler.runTack(() -> {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            try {
                if (!fillBuffer(buffer, type, obs))
                    return;
                LogWriter.debug("Send: " + type);
                CustomNpcs.Channel.sendTo(new FMLProxyPacket(buffer, "CustomNPCs"), player);
            } catch (IOException e) {
                LogWriter.error(type + " Errored", e);
            }
        }, delay);
    }

    public static boolean sendDataChecked(EntityPlayerMP player, EnumPacketClient type, Object... obs) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        try {
            if (!fillBuffer(buffer, type, obs))
                return false;
            LogWriter.debug("SendDataChecked: " + type);
            CustomNpcs.Channel.sendTo(new FMLProxyPacket(buffer, "CustomNPCs"), player);
        } catch (IOException e) {
            LogWriter.error(type + " Errored", e);
        }
        return true;
    }

    public static void sendAssociatedData(final Entity entity, final EnumPacketClient type, final Object... obs) {
        final List<EntityPlayerMP> list = entity.world.getEntitiesWithinAABB(EntityPlayerMP.class, entity.getEntityBoundingBox().grow(160, 160, 160));
        if (list.isEmpty())
            return;
        CustomNPCsScheduler.runTack(() -> {
            ByteBuf buffer = Unpooled.buffer();
            try {
                if (!fillBuffer(buffer, type, obs))
                    return;
                LogWriter.debug("SendAssociatedData: " + type);
                for (EntityPlayerMP player : list) {
                    CustomNpcs.Channel.sendTo(new FMLProxyPacket(new PacketBuffer(buffer.copy()), "CustomNPCs"), player);
                }
            } catch (IOException e) {
                LogWriter.error(type + " Errored", e);
            } finally {
                buffer.release();
            }
        });
    }

    public static void sendToAll(MinecraftServer server, final EnumPacketClient type, final Object... obs) {
        final List<EntityPlayerMP> list = new ArrayList<EntityPlayerMP>(server.getPlayerList().getPlayers());
        CustomNPCsScheduler.runTack(() -> {
            ByteBuf buffer = Unpooled.buffer();
            try {
                if (!fillBuffer(buffer, type, obs))
                    return;
                LogWriter.debug("SendToAll: " + type);
                for (EntityPlayerMP player : list) {
                    CustomNpcs.Channel.sendTo(new FMLProxyPacket(new PacketBuffer(buffer.copy()), "CustomNPCs"), player);
                }
            } catch (IOException e) {
                LogWriter.error(type + " Errored", e);
            } finally {
                buffer.release();
            }
        });
    }

    public static boolean fillBuffer(ByteBuf buffer, Enum enu, Object... obs) throws IOException {
        buffer.writeInt(enu.ordinal());
        for (Object ob : obs) {
            if (ob == null)
                continue;
            if (ob instanceof Map) {
                Map<String, Integer> map = (Map<String, Integer>) ob;

                buffer.writeInt(map.size());
                for (String key : map.keySet()) {
                    int value = map.get(key);
                    buffer.writeInt(value);
                    writeString(buffer, key);
                }
            } else if (ob instanceof MerchantRecipeList)
                ((MerchantRecipeList) ob).writeToBuf(new PacketBuffer(buffer));
            else if (ob instanceof List) {
                List<String> list = (List<String>) ob;
                buffer.writeInt(list.size());
                for (String s : list)
                    writeString(buffer, s);
            } else if (ob instanceof UUID)
                writeString(buffer, ob.toString());
            else if (ob instanceof Enum)
                buffer.writeInt(((Enum<?>) ob).ordinal());
            else if (ob instanceof Integer)
                buffer.writeInt((Integer) ob);
            else if (ob instanceof Boolean)
                buffer.writeBoolean((Boolean) ob);
            else if (ob instanceof String)
                writeString(buffer, (String) ob);
            else if (ob instanceof Float)
                buffer.writeFloat((Float) ob);
            else if (ob instanceof Long)
                buffer.writeLong((Long) ob);
            else if (ob instanceof Double)
                buffer.writeDouble((Double) ob);
            else if (ob instanceof NBTTagCompound)
                writeNBT(buffer, (NBTTagCompound) ob);
        }
        if (buffer.array().length >= Short.MAX_VALUE * 2) {
            LogWriter.error("Packet " + enu + " was too big to be send");
            return false;
        }
        return true;
    }

    public static UUID readUUID(ByteBuf buffer) {
        return UUID.fromString(readString(buffer));
    }


    public static void writeNBT(ByteBuf buffer, NBTTagCompound compound) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream));

        try {
            CompressedStreamTools.write(compound, dataoutputstream);
        } finally {
            dataoutputstream.close();
        }
        byte[] bytes = bytearrayoutputstream.toByteArray();
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
    }

    public static NBTTagCompound readNBT(ByteBuf buffer) throws IOException {
        byte[] bytes = new byte[buffer.readInt()];
        buffer.readBytes(bytes);
        DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes))));

        try {
            return CompressedStreamTools.read(datainputstream, NBTSizeTracker.INFINITE);
        } finally {
            datainputstream.close();
        }
    }

    public static void writeString(ByteBuf buffer, String s) {
        byte[] bytes = s.getBytes(Charsets.UTF_8);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
    }

    public static String readString(ByteBuf buffer) {
        try {
            byte[] bytes = new byte[buffer.readInt()];
            buffer.readBytes(bytes);
            return new String(bytes, Charsets.UTF_8);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

}
