package rpgloot.packets

import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import rpgloot.entities.EntCorpse

class DisposePacket : CorpsePacket {

    constructor()

    constructor(corpse: EntCorpse) : super(corpse)

    class HANDLER : IMessageHandler<DisposePacket, IMessage> {

        override fun onMessage(message: DisposePacket, ctx: MessageContext): IMessage? {
            val corpse = ctx.serverHandler.player.world.getEntityByID(message.corpseID) as EntCorpse?
            if (corpse != null && corpse.lootToPlayer(ctx.serverHandler.player)) {
                corpse.dispose()
            }

            return null
        }
    }
}
