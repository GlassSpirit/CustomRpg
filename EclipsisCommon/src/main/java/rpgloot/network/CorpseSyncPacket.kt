package rpgloot.network

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.writeTag
import com.teamwizardry.librarianlib.features.network.PacketBase
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import rpgloot.entities.EntityCorpse

class CorpseSyncPacket() : PacketBase() {
    var corpseID: Int = 0
    var corpseTag: NBTTagCompound = NBTTagCompound()

    constructor(corpse: EntityCorpse) : this() {
        corpseID = corpse.entityId
        if (!corpse.world.isRemote) corpse.writeToNBT(corpseTag)
    }

    override fun readCustomBytes(buf: ByteBuf) {
        corpseID = buf.readInt()
        corpseTag = ByteBufUtils.readTag(buf)!!
    }

    override fun writeCustomBytes(buf: ByteBuf) {
        buf.writeInt(corpseID)
        buf.writeTag(corpseTag)
    }

    override fun handle(ctx: MessageContext) {
        if (ctx.side.isServer) {
            server(ctx.serverHandler.player)
        } else {
            client(Minecraft().player)
        }
    }

    fun client(player: EntityPlayer) {
        val world = player.world
        val entity = world.getEntityByID(this.corpseID)
        if (entity != null && entity is EntityCorpse) {
            entity.readFromNBT(this.corpseTag)
        }
    }

    fun server(player: EntityPlayerMP) {
        val world = player.world
        val entity = world.getEntityByID(this.corpseID)
        if (entity != null && entity is EntityCorpse) {
            entity.markDirty()
        }
    }
}
