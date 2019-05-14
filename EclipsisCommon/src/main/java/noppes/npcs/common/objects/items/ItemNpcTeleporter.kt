package noppes.npcs.common.objects.items

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import noppes.npcs.common.CustomNpcs
import noppes.npcs.common.objects.CreativeTabNpcs
import noppes.npcs.constants.EnumGuiType
import noppes.npcs.constants.EnumPacketServer
import noppes.npcs.common.entity.EntityNPCInterface
import noppes.npcs.util.IPermission
import ru.glassspirit.eclipsis.kotlin.setModCreativeTab


class ItemNpcTeleporter : ItemMod("teleporter"), IPermission {
    init {
        maxStackSize = 1
        this.setModCreativeTab(CreativeTabNpcs)
    }

    override fun onItemRightClick(world: World?, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val itemstack = player.getHeldItem(hand)
        if (!world!!.isRemote)
            return ActionResult(EnumActionResult.SUCCESS, itemstack)
        CustomNpcs.proxy.openGui((null as EntityNPCInterface?)!!, EnumGuiType.NpcDimensions)
        return ActionResult(EnumActionResult.SUCCESS, itemstack)
    }

    override fun onEntitySwing(entity: EntityLivingBase, stack: ItemStack): Boolean {
        if (entity.world.isRemote)
            return false
        val f = 1.0f
        val f1 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * f
        val f2 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * f
        val d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * f.toDouble()
        val d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * f.toDouble() + 1.62
        val d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * f.toDouble()
        val vec3 = Vec3d(d0, d1, d2)
        val f3 = MathHelper.cos(-f2 * 0.017453292f - Math.PI.toFloat())
        val f4 = MathHelper.sin(-f2 * 0.017453292f - Math.PI.toFloat())
        val f5 = -MathHelper.cos(-f1 * 0.017453292f)
        val f6 = MathHelper.sin(-f1 * 0.017453292f)
        val f7 = f4 * f5
        val f8 = f3 * f5
        val d3 = 80.0
        val vec31 = vec3.add(f7.toDouble() * d3, f6.toDouble() * d3, f8.toDouble() * d3)
        val movingobjectposition = entity.world.rayTraceBlocks(vec3, vec31, true) ?: return false

        val vec32 = entity.getLook(f)
        var flag = false
        val f9 = 1.0f
        val list = entity.world.getEntitiesWithinAABBExcludingEntity(entity, entity.entityBoundingBox.grow(vec32.x * d3, vec32.y * d3, vec32.z * d3).grow(f9.toDouble(), f9.toDouble(), f9.toDouble()))

        for (i in list.indices) {
            val entity1 = list[i] as Entity

            if (entity1.canBeCollidedWith()) {
                val f10 = entity1.collisionBorderSize
                val axisalignedbb = entity1.entityBoundingBox.grow(f10.toDouble(), f10.toDouble(), f10.toDouble())

                if (axisalignedbb.contains(vec3)) {
                    flag = true
                }
            }
        }

        if (flag)
            return false

        if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK) {
            var pos = movingobjectposition.blockPos

            while (entity.world.getBlockState(pos).block !== Blocks.AIR) {
                pos = pos.up()
            }
            entity.setPositionAndUpdate((pos.x + 0.5f).toDouble(), (pos.y + 1.0f).toDouble(), (pos.z + 0.5f).toDouble())
        }

        return true
    }

    override fun isAllowed(e: EnumPacketServer): Boolean {
        return e == EnumPacketServer.DimensionsGet || e == EnumPacketServer.DimensionTeleport
    }
}
