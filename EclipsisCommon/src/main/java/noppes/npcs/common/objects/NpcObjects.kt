package noppes.npcs.common.objects

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.block.BlockModDoor
import net.minecraft.block.Block
import net.minecraft.block.BlockDispenser
import net.minecraft.dispenser.BehaviorDefaultDispenseItem
import net.minecraft.dispenser.IBlockSource
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import noppes.npcs.client.renderer.blocks.*
import noppes.npcs.common.objects.items.*
import noppes.npcs.common.objects.tiles.*
import noppes.npcs.controllers.RecipeController

object NpcObjects {

    @JvmField
    val redstoneBlock: Block? = null

    @JvmField
    val mailbox: Block? = null

    @JvmField
    val waypoint: Block? = null

    @JvmField
    val borderBlock: Block? = null

    @JvmField
    val scriptedBlock: Block? = null

    @JvmField
    val scriptedDoor = BlockModDoor("scripted_door", Blocks.STONE.defaultState)

    @JvmField
    val builderBlock: Block? = null

    @JvmField
    val copyBlock: Block? = null

    @JvmField
    val carpentyBench: Block? = null

    @JvmField
    val wand = ItemNpcWand()

    @JvmField
    val mobCloner = ItemNpcCloner()

    @JvmField
    val scripter = ItemNpcScripter()

    @JvmField
    val pather = ItemNpcPather()

    @JvmField
    val mounter = ItemNpcMounter()

    @JvmField
    val teleporter = ItemNpcTeleporter()

    @JvmField
    val scriptedItem = ItemScripted()

    @JvmField
    val scriptedDoorTool = ItemScriptedDoor(scriptedDoor)

    @JvmField
    val nbtBook = ItemNbtBook()

    @JvmField
    val soulstoneEmpty = ItemSoulstoneEmpty()

    @JvmField
    val soulstoneFull = ItemSoulstoneFilled()

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun registerBlocks(event: RegistryEvent.Register<Block>) {
        /*GameRegistry.registerTileEntity(TileRedstoneBlock::class.java, "customnpcs:tile_redstone_block".toRl())
        GameRegistry.registerTileEntity(TileBlockAnvil::class.java, "customnpcs:tile_anvil".toRl())
        GameRegistry.registerTileEntity(TileMailbox::class.java, "customnpcs:tile_mailbox".toRl())
        GameRegistry.registerTileEntity(TileWaypoint::class.java, "customnpcs:tile_waypoint".toRl())
        GameRegistry.registerTileEntity(TileScripted::class.java, "customnpcs:tile_scripted".toRl())
        GameRegistry.registerTileEntity(TileScriptedDoor::class.java, "customnpcs:tile_scripted_door".toRl())
        GameRegistry.registerTileEntity(TileBuilder::class.java, "customnpcs:tile_builder".toRl())
        GameRegistry.registerTileEntity(TileCopy::class.java, "customnpcs:tile_copy".toRl())
        GameRegistry.registerTileEntity(TileBorder::class.java, "customnpcs:tile_border".toRl())

        val redstoneBlock = BlockNpcRedstone().setHardness(50.0f).setResistance(2000f).setTranslationKey("npcredstoneblock").setCreativeTab(CreativeTabNpcs)
        val mailbox = BlockMailbox().setTranslationKey("npcmailbox").setHardness(5.0f).setResistance(10.0f).setCreativeTab(CreativeTabNpcs)
        val waypoint = BlockWaypoint().setTranslationKey("npcwaypoint").setHardness(5.0f).setResistance(10.0f).setCreativeTab(CreativeTabNpcs)
        val border = BlockBorder().setTranslationKey("npcborder").setHardness(5.0f).setResistance(10.0f).setCreativeTab(CreativeTabNpcs)
        val scripted = BlockScripted().setTranslationKey("npcscripted").setHardness(5.0f).setResistance(10.0f).setCreativeTab(CreativeTabNpcs)
        val scriptedDoor = BlockScriptedDoor().setTranslationKey("npcscripteddoor").setHardness(5.0f).setResistance(10.0f).setCreativeTab(CreativeTabNpcs);
        val builder = BlockBuilder().setTranslationKey("npcbuilderblock").setHardness(5.0f).setResistance(10.0f).setCreativeTab(CreativeTabNpcs)
        val copy = BlockCopy().setTranslationKey("npccopyblock").setHardness(5.0f).setResistance(10.0f).setCreativeTab(CreativeTabNpcs)
        val carpentyBench = BlockCarpentryBench().setTranslationKey("npccarpentybench").setHardness(5.0f).setResistance(10.0f).setCreativeTab(CreativeTabNpcs)

        event.registry.registerAll(redstoneBlock, carpentyBench, mailbox, waypoint, border, scripted, scriptedDoor, builder, copy)*/
    }

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(soulstoneFull, object : BehaviorDefaultDispenseItem() {
            public override fun dispenseStack(source: IBlockSource, item: ItemStack): ItemStack {
                val enumfacing = source.blockState.getValue(BlockDispenser.FACING)
                val x = source.x + enumfacing.xOffset
                val z = source.z + enumfacing.zOffset
                ItemSoulstoneFilled.spawn(null, item, source.world, BlockPos(x, source.y, z))
                item.splitStack(1)
                return item
            }
        })
    }

    @SubscribeEvent
    fun registerRecipes(event: RegistryEvent.Register<IRecipe>) {
        RecipeController.Registry = event.registry
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        /*ModelLoader.setCustomStateMapper(mailbox!!, StateMap.Builder().ignore(BlockMailbox.ROTATION, BlockMailbox.TYPE).build())
        ModelLoader.setCustomStateMapper(scriptedDoor!!, StateMap.Builder().ignore(BlockDoor.POWERED).build())
        ModelLoader.setCustomStateMapper(builderBlock!!, StateMap.Builder().ignore(BlockBuilder.ROTATION).build())
        ModelLoader.setCustomStateMapper(carpentyBench!!, StateMap.Builder().ignore(BlockCarpentryBench.ROTATION).build())

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(redstoneBlock!!), 0, ModelResourceLocation(redstoneBlock.registryName!!, "inventory"))
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mailbox), 0, ModelResourceLocation(mailbox.registryName!!, "inventory"))
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mailbox), 1, ModelResourceLocation(mailbox.registryName!!, "inventory"))
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mailbox), 2, ModelResourceLocation(mailbox.registryName!!, "inventory"))
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(waypoint!!), 0, ModelResourceLocation(waypoint.registryName!!, "inventory"))
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(borderBlock!!), 0, ModelResourceLocation(borderBlock.registryName!!, "inventory"))
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(scriptedBlock!!), 0, ModelResourceLocation(scriptedBlock.registryName!!, "inventory"))
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(scriptedDoor), 0, ModelResourceLocation(scriptedDoor.registryName!!, "inventory"))
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(builderBlock), 0, ModelResourceLocation(builderBlock.registryName!!, "inventory"))
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(copyBlock!!), 0, ModelResourceLocation(copyBlock.registryName!!, "inventory"))
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(carpentyBench), 0, ModelResourceLocation(carpentyBench.registryName!!, "inventory"))*/

        ClientRegistry.bindTileEntitySpecialRenderer(TileBlockAnvil::class.java, BlockCarpentryBenchRenderer())
        ClientRegistry.bindTileEntitySpecialRenderer(TileMailbox::class.java, BlockMailboxRenderer(0))
        ClientRegistry.bindTileEntitySpecialRenderer(TileMailbox2::class.java, BlockMailboxRenderer(1))
        ClientRegistry.bindTileEntitySpecialRenderer(TileMailbox3::class.java, BlockMailboxRenderer(2))
        ClientRegistry.bindTileEntitySpecialRenderer(TileScripted::class.java, BlockScriptedRenderer())
        ClientRegistry.bindTileEntitySpecialRenderer(TileDoor::class.java, BlockDoorRenderer())
        ClientRegistry.bindTileEntitySpecialRenderer(TileCopy::class.java, BlockCopyRenderer())

        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(carpentyBench), 0, TileBlockAnvil::class.java)
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(mailbox), 0, TileMailbox::class.java)
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(mailbox), 1, TileMailbox2::class.java)
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(mailbox), 2, TileMailbox3::class.java)

    }
}

object CreativeTabNpcs : ModCreativeTab() {
    override val iconStack: ItemStack
        get() = ItemStack(NpcObjects.wand)
}
