package noppes.npcs.common.objects.items

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.math.MathHelper
import noppes.npcs.api.NpcAPI
import noppes.npcs.api.wrapper.ItemScriptedWrapper
import noppes.npcs.common.objects.CreativeTabNpcs
import noppes.npcs.constants.EnumPacketServer
import noppes.npcs.util.IPermission
import ru.glassspirit.eclipsis.kotlin.setModCreativeTab
import java.util.*

class ItemScripted : ItemMod("scripted_item"), IPermission {
    init {
        maxStackSize = 1
        setHasSubtypes(true)
        setFull3D()
        this.setModCreativeTab(CreativeTabNpcs)
    }

    override fun isAllowed(e: EnumPacketServer): Boolean {
        return e == EnumPacketServer.ScriptItemDataGet || e == EnumPacketServer.ScriptItemDataSave
    }

    override fun showDurabilityBar(stack: ItemStack): Boolean {
        val istack = NpcAPI.instance()!!.getIItemStack(stack)
        return (istack as? ItemScriptedWrapper)?.durabilityShow ?: super.showDurabilityBar(stack)
    }

    override fun getDurabilityForDisplay(stack: ItemStack): Double {
        val istack = NpcAPI.instance()!!.getIItemStack(stack)
        return if (istack is ItemScriptedWrapper) 1 - istack.durabilityValue else super.getDurabilityForDisplay(stack)
    }

    override fun getRGBDurabilityForDisplay(stack: ItemStack): Int {
        val istack = NpcAPI.instance()!!.getIItemStack(stack) as? ItemScriptedWrapper ?: return super.getRGBDurabilityForDisplay(stack)
        val color = istack.durabilityColor
        return if (color >= 0) color else MathHelper.hsvToRGB(Math.max(0.0f, (1.0f - getDurabilityForDisplay(stack)).toFloat()) / 3.0f, 1.0f, 1.0f)
    }

    override fun getItemStackLimit(stack: ItemStack): Int {
        val istack = NpcAPI.instance()!!.getIItemStack(stack)
        return (istack as? ItemScriptedWrapper)?.maxStackSize ?: super.getItemStackLimit(stack)
    }

    override fun hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase): Boolean {
        return true
    }

    companion object {
        var Resources: Map<Int, String> = HashMap()

        fun getWrapper(stack: ItemStack): ItemScriptedWrapper {
            return NpcAPI.instance()!!.getIItemStack(stack) as ItemScriptedWrapper
        }
    }
}
