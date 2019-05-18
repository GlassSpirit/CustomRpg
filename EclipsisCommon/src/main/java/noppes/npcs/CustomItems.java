package noppes.npcs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.blocks.*;
import noppes.npcs.blocks.tiles.*;
import noppes.npcs.client.renderer.blocks.*;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.items.*;

@ObjectHolder("customnpcs")
public class CustomItems {

    @ObjectHolder("npcwand")
    public static final Item wand = null;
    @ObjectHolder("npcmobcloner")
    public static final Item cloner = null;
    @ObjectHolder("npcscripter")
    public static final Item scripter = null;
    @ObjectHolder("npcmovingpath")
    public static final Item moving = null;
    @ObjectHolder("npcmounter")
    public static final Item mount = null;
    @ObjectHolder("npcteleporter")
    public static final Item teleporter = null;
    @ObjectHolder("npcscripteddoortool")
    public static final Item scriptedDoorTool = null;
    @ObjectHolder("scripted_item")
    public static final ItemScripted scripted_item = null;
    @ObjectHolder("nbt_book")
    public static final ItemNbtBook nbt_book = null;

    @ObjectHolder("npcsoulstoneempty")
    public static final Item soulstoneEmpty = null;
    @ObjectHolder("npcsoulstonefilled")
    public static final Item soulstoneFull = null;

    @ObjectHolder("npcredstoneblock")
    public static final Block redstoneBlock = null;
    @ObjectHolder("npcmailbox")
    public static final Block mailbox = null;
    @ObjectHolder("npcwaypoint")
    public static final Block waypoint = null;
    @ObjectHolder("npcborder")
    public static final Block border = null;
    @ObjectHolder("npcscripted")
    public static final Block scripted = null;
    @ObjectHolder("npcscripteddoor")
    public static final Block scriptedDoor = null;
    @ObjectHolder("npcbuilderblock")
    public static final Block builder = null;
    @ObjectHolder("npccopyblock")
    public static final Block copy = null;
    @ObjectHolder("npccarpentybench")
    public static final Block carpentyBench = null;

    public static CreativeTabNpcs tab = new CreativeTabNpcs("cnpcs");

    public static void load() {
        MinecraftForge.EVENT_BUS.register(new CustomItems());
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        GameRegistry.registerTileEntity(TileRedstoneBlock.class, "TileRedstoneBlock");
        GameRegistry.registerTileEntity(TileBlockAnvil.class, "TileBlockAnvil");
        GameRegistry.registerTileEntity(TileMailbox.class, "TileMailbox");
        GameRegistry.registerTileEntity(TileWaypoint.class, "TileWaypoint");
        GameRegistry.registerTileEntity(TileScripted.class, "TileNPCScripted");
        GameRegistry.registerTileEntity(TileScriptedDoor.class, "TileNPCScriptedDoor");
        GameRegistry.registerTileEntity(TileBuilder.class, "TileNPCBuilder");
        GameRegistry.registerTileEntity(TileCopy.class, "TileNPCCopy");
        GameRegistry.registerTileEntity(TileBorder.class, "TileNPCBorder");

        Block redstoneBlock = new BlockNpcRedstone().setHardness(50.0F).setResistance(2000).setTranslationKey("npcredstoneblock").setCreativeTab(tab);
        Block mailbox = new BlockMailbox().setTranslationKey("npcmailbox").setHardness(5.0F).setResistance(10.0F).setCreativeTab(tab);
        Block waypoint = new BlockWaypoint().setTranslationKey("npcwaypoint").setHardness(5.0F).setResistance(10.0F).setCreativeTab(tab);
        Block border = new BlockBorder().setTranslationKey("npcborder").setHardness(5.0F).setResistance(10.0F).setCreativeTab(tab);
        Block scripted = new BlockScripted().setTranslationKey("npcscripted").setHardness(5.0F).setResistance(10.0F).setCreativeTab(tab);
        Block scriptedDoor = new BlockScriptedDoor().setTranslationKey("npcscripteddoor").setHardness(5.0F).setResistance(10.0F);//.setCreativeTab(tab);
        Block builder = new BlockBuilder().setTranslationKey("npcbuilderblock").setHardness(5.0F).setResistance(10.0F).setCreativeTab(tab);
        Block copy = new BlockCopy().setTranslationKey("npccopyblock").setHardness(5.0F).setResistance(10.0F).setCreativeTab(tab);
        Block carpentyBench = new BlockCarpentryBench().setTranslationKey("npccarpentybench").setHardness(5.0F).setResistance(10.0F).setCreativeTab(tab);

        event.getRegistry().registerAll(redstoneBlock, carpentyBench, mailbox, waypoint, border, scripted, scriptedDoor, builder, copy);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        Item wand = new ItemNpcWand().setTranslationKey("npcwand").setFull3D();
        Item cloner = new ItemNpcCloner().setTranslationKey("npcmobcloner").setFull3D();
        Item scripter = new ItemNpcScripter().setTranslationKey("npcscripter").setFull3D();
        Item moving = new ItemNpcMovingPath().setTranslationKey("npcmovingpath").setFull3D();
        Item mount = new ItemMounter().setTranslationKey("npcmounter").setFull3D();
        Item teleporter = new ItemTeleporter().setTranslationKey("npcteleporter").setFull3D();
        Item scriptedDoorTool = new ItemScriptedDoor(scriptedDoor).setTranslationKey("npcscripteddoortool").setFull3D();
        Item soulstoneEmpty = new ItemSoulstoneEmpty().setTranslationKey("npcsoulstoneempty").setCreativeTab(tab);
        Item soulstoneFull = new ItemSoulstoneFilled().setTranslationKey("npcsoulstonefilled");
        Item scripted_item = new ItemScripted().setTranslationKey("scripted_item");
        Item nbt_book = new ItemNbtBook().setTranslationKey("nbt_book");

        event.getRegistry().registerAll(wand, cloner, scripter, moving, mount, teleporter, scriptedDoorTool, soulstoneEmpty, soulstoneFull, scripted_item, nbt_book);

        event.getRegistry().registerAll(new ItemNpcBlock(redstoneBlock), new ItemNpcBlock(carpentyBench), new ItemNpcBlock(mailbox).setHasSubtypes(true), new ItemNpcBlock(waypoint), new ItemNpcBlock(border), new ItemNpcBlock(scripted), new ItemNpcBlock(scriptedDoor), new ItemNpcBlock(builder), new ItemNpcBlock(copy));

        tab.item = wand;

        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(soulstoneFull, new BehaviorDefaultDispenseItem() {

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack item) {
                EnumFacing enumfacing = source.getBlockState().getValue(BlockDispenser.FACING);
                double x = source.getX() + enumfacing.getXOffset();
                double z = source.getZ() + enumfacing.getZOffset();
                ItemSoulstoneFilled.Spawn(null, item, source.getWorld(), new BlockPos(x, source.getY(), z));
                item.splitStack(1);
                return item;
            }
        });
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        RecipeController.Registry = event.getRegistry();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomStateMapper(mailbox, (new StateMap.Builder()).ignore(BlockMailbox.ROTATION, BlockMailbox.TYPE).build());
        ModelLoader.setCustomStateMapper(scriptedDoor, (new StateMap.Builder()).ignore(BlockDoor.POWERED).build());
        ModelLoader.setCustomStateMapper(builder, (new StateMap.Builder()).ignore(BlockBuilder.ROTATION).build());
        ModelLoader.setCustomStateMapper(carpentyBench, (new StateMap.Builder()).ignore(BlockCarpentryBench.ROTATION).build());

        ModelLoader.setCustomModelResourceLocation(wand, 0, new ModelResourceLocation("customnpcs:npcwand", "inventory"));
        ModelLoader.setCustomModelResourceLocation(cloner, 0, new ModelResourceLocation("customnpcs:npcmobcloner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(scripter, 0, new ModelResourceLocation("customnpcs:npcscripter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(moving, 0, new ModelResourceLocation("customnpcs:npcmovingpath", "inventory"));
        ModelLoader.setCustomModelResourceLocation(mount, 0, new ModelResourceLocation("customnpcs:npcmounter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(teleporter, 0, new ModelResourceLocation("customnpcs:npcteleporter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(scriptedDoorTool, 0, new ModelResourceLocation("customnpcs:npcscripteddoortool", "inventory"));
        ModelLoader.setCustomModelResourceLocation(soulstoneEmpty, 0, new ModelResourceLocation("customnpcs:npcsoulstoneempty", "inventory"));
        ModelLoader.setCustomModelResourceLocation(soulstoneFull, 0, new ModelResourceLocation("customnpcs:npcsoulstonefilled", "inventory"));
        ModelLoader.setCustomModelResourceLocation(scripted_item, 0, new ModelResourceLocation("customnpcs:scripted_item", "inventory"));
        ModelLoader.setCustomModelResourceLocation(nbt_book, 0, new ModelResourceLocation("customnpcs:nbt_book", "inventory"));

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(redstoneBlock), 0, new ModelResourceLocation(redstoneBlock.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mailbox), 0, new ModelResourceLocation(mailbox.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mailbox), 1, new ModelResourceLocation(mailbox.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mailbox), 2, new ModelResourceLocation(mailbox.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(waypoint), 0, new ModelResourceLocation(waypoint.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(border), 0, new ModelResourceLocation(border.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(scripted), 0, new ModelResourceLocation(scripted.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(scriptedDoor), 0, new ModelResourceLocation(scriptedDoor.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(builder), 0, new ModelResourceLocation(builder.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(copy), 0, new ModelResourceLocation(copy.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(carpentyBench), 0, new ModelResourceLocation(carpentyBench.getRegistryName(), "inventory"));

        ClientRegistry.bindTileEntitySpecialRenderer(TileBlockAnvil.class, new BlockCarpentryBenchRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMailbox.class, new BlockMailboxRenderer(0));
        ClientRegistry.bindTileEntitySpecialRenderer(TileMailbox2.class, new BlockMailboxRenderer(1));
        ClientRegistry.bindTileEntitySpecialRenderer(TileMailbox3.class, new BlockMailboxRenderer(2));
        ClientRegistry.bindTileEntitySpecialRenderer(TileScripted.class, new BlockScriptedRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileDoor.class, new BlockDoorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCopy.class, new BlockCopyRenderer());

        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(carpentyBench), 0, TileBlockAnvil.class);
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(mailbox), 0, TileMailbox.class);
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(mailbox), 1, TileMailbox2.class);
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(mailbox), 2, TileMailbox3.class);

    }
}
