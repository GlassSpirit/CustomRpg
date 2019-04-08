package ru.glassspirit.eclipsis.objects

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import net.minecraft.block.material.Material
import net.minecraft.item.ItemStack
import ru.glassspirit.eclipsis.objects.block.decorations.BlockDecorationFullNoCollision
import ru.glassspirit.eclipsis.objects.block.decorations.BlockModRail
import ru.glassspirit.eclipsis.objects.block.decorations.BlockModRope
import ru.glassspirit.eclipsis.objects.block.miscellanous.BlockBorder
import ru.glassspirit.eclipsis.objects.block.miscellanous.BlockBorderLight
import ru.glassspirit.eclipsis.objects.block.miscellanous.BlockEmpty
import ru.glassspirit.eclipsis.objects.block.miscellanous.BlockLight

object EclipsisBlocks {
    @JvmField
    val borderBlock = BlockBorder("border")
    @JvmField
    val borderLightBlock = BlockBorderLight("border_light")
    @JvmField
    val lightBlock = BlockLight("light")
    @JvmField
    val emptyBlock = BlockEmpty("empty")

    @JvmField
    val railClothBlock = BlockModRail("rail_cloth")
    @JvmField
    val railIronchainBlock = BlockModRail("rail_ironchain")

    @JvmField
    val ropeClothBlock = BlockModRope("rope_cloth", Material.CLOTH)
    @JvmField
    val ropeIronchainBlock = BlockModRope("rope_ironchain", Material.IRON)
    @JvmField
    val ropeSmallchainBlock = BlockModRope("rope_smallchain", Material.IRON)
    @JvmField
    val ropeRustychainBlock = BlockModRope("rope_rustychain", Material.IRON)
    @JvmField
    val ropeGoldchainBlock = BlockModRope("rope_goldchain", Material.IRON)

    @JvmField
    val hookClothBlock = BlockDecorationFullNoCollision("hook_rope", Material.CLOTH)
    @JvmField
    val hookIronBlock = BlockDecorationFullNoCollision("hook_iron", Material.IRON)

}

object EclipsisItems {
    /*val testItem = object : ItemMod("test_item") {
        override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
            if (worldIn.isRemote) {
                val builder = ParticleBuilder(500)
                builder.setDeceleration(vec(1, 0, 1))
                builder.disableRandom()
                //builder.setColorFunction(InterpColorHSV(Color.WHITE, Color.YELLOW))
                //builder.setAcceleration(vec(0, -0.05, 0))
                //builder.setCollision(true)
                val loc = ResourceLocation("items/snowball")
                if (playerIn.isSneaking) {
                    builder.setRender(loc)
                } else {
                    builder.setRenderNormalLayer(loc)
                }
                builder.setRotation(InterpLinearFloat(0f, 3 * Math.PI.toFloat()))

                ParticleSpawner.spawn(builder, worldIn,
                        InterpHelix(playerIn.getPositionEyes(0f).add(playerIn.getLook(Minecraft().renderPartialTicks)),
                                playerIn.getPositionEyes(0f).add(playerIn.getLook(Minecraft().renderPartialTicks) * 0.1),
                                1f, 2f, 5f, 0f), 300, 200)
            }

            return super.onItemRightClick(worldIn, playerIn, handIn)
        }
    }*/


}

object CustomRpgTab : ModCreativeTab() {
    init {
        registerDefaultTab()
    }

    override val iconStack: ItemStack
        get() = ItemStack(EclipsisBlocks.lightBlock)
}