package ru.glassspirit.eclipsis.network

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.readTag
import com.teamwizardry.librarianlib.features.kotlin.writeTag
import com.teamwizardry.librarianlib.features.network.PacketBase
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

open class NBTPacket : PacketBase() {
    protected val data: NBTTagCompound = NBTTagCompound()

    override fun handle(ctx: MessageContext) {
        if (ctx.side.isServer) {
            ctx.serverHandler.player.serverWorld.addScheduledTask { server(ctx.serverHandler.player) }
        } else {
            Minecraft().addScheduledTask { client(Minecraft().player) }
        }
    }

    /**
     * Override this to add custom write-to-bytes.
     * Make sure to have the same order for writing and reading.
     */
    override fun writeCustomBytes(buf: ByteBuf) {
        buf.writeTag(data)
    }

    /**
     * Override this to add custom read-from-bytes.
     * Make sure to have the same order for writing and reading.
     */
    override fun readCustomBytes(buf: ByteBuf) {
        data.merge(buf.readTag())
    }

    /**
     * Обработка пакета, полученного с сервера
     */
    open fun client(player: EntityPlayer) {}

    /**
     * Обработка пакета, полученного с клиента
     */
    open fun server(player: EntityPlayerMP) {}

}