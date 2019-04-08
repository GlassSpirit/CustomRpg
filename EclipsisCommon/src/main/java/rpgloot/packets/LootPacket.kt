package rpgloot.packets

import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import rpgloot.entities.EntCorpse

class LootPacket : CorpsePacket {

    constructor()

    constructor(corpse: EntCorpse) : super(corpse)

    class HANDLER : IMessageHandler<LootPacket, IMessage> {

        override fun onMessage(message: LootPacket, ctx: MessageContext): IMessage? {
            if (ctx.serverHandler.player.world.getEntityByID(message.corpseID) != null) {
                (ctx.serverHandler.player.world.getEntityByID(message.corpseID) as EntCorpse)
                        .lootToPlayer(ctx.serverHandler.player)
            }

            return null
        }
    }
}
