package rpgloot.packets

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import rpgloot.RPGLoot
import rpgloot.entities.EntCorpse

class CorpseSyncPacket : CorpsePacket {

    var corpseTag: NBTTagCompound? = null

    constructor()

    constructor(corpse: EntCorpse) : super(corpse) {
        corpseTag = NBTTagCompound()
        corpse.writeToNBT(corpseTag!!)
    }

    override fun fromBytes(buf: ByteBuf) {
        super.fromBytes(buf)
        corpseTag = ByteBufUtils.readTag(buf)
    }

    override fun toBytes(buf: ByteBuf) {
        super.toBytes(buf)
        ByteBufUtils.writeTag(buf, corpseTag)
    }

    class HANDLER : IMessageHandler<CorpseSyncPacket, IMessage> {
        override fun onMessage(message: CorpseSyncPacket, ctx: MessageContext): IMessage? {
            RPGLoot.proxy.handleCorpseSyncPacket(message)
            return null
        }
    }
}
