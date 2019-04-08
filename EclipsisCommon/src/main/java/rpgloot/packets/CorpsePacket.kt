package rpgloot.packets

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import rpgloot.entities.EntCorpse

open class CorpsePacket : IMessage {

    var corpseID: Int = 0

    constructor()

    constructor(corpse: EntCorpse) {
        corpseID = corpse.entityId
    }

    override fun fromBytes(buf: ByteBuf) {
        corpseID = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(corpseID)
    }
}
