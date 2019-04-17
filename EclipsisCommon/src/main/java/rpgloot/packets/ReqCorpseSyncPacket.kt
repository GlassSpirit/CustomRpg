package rpgloot.packets

import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import rpgloot.entities.EntCorpse

class ReqCorpseSyncPacket : CorpsePacket {

    constructor()

    constructor(corpse: EntCorpse) : super(corpse)

    class HANDLER : IMessageHandler<ReqCorpseSyncPacket, IMessage> {

        override fun onMessage(message: ReqCorpseSyncPacket, ctx: MessageContext): IMessage? {
            val world = ctx.serverHandler.player.world
            val entity = world.getEntityByID(message.corpseID)
            if (entity != null && entity is EntCorpse) {
                entity.markDirty()
            }

            return null
        }
    }
}
