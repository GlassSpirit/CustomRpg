package noppes.npcs.common.objects.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.common.objects.NpcObjects;
import noppes.npcs.common.objects.tiles.TileBorder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.util.IPermission;

public class BlockBorder extends BlockInterface implements IPermission {
    public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 3);

    public BlockBorder() {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);
        setBlockUnbreakable();
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public IIcon getIcon(int side, int meta)
//    {
//    	if(side == 1){
//    		return this.blockIcon;
//    	}
//    	return Blocks.iron_block.getIcon(side, meta);
//    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack currentItem = player.inventory.getCurrentItem();
        if (!world.isRemote && currentItem != null && currentItem.getItem() == NpcObjects.wand) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.Border, null, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        int l = MathHelper.floor((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        l %= 4;

        world.setBlockState(pos, state.withProperty(ROTATION, l));
        TileBorder tile = (TileBorder) world.getTileEntity(pos);

        TileBorder adjacent = getTile(world, pos.west());
        if (adjacent == null)
            adjacent = getTile(world, pos.south());
        if (adjacent == null)
            adjacent = getTile(world, pos.north());
        if (adjacent == null)
            adjacent = getTile(world, pos.east());

        if (adjacent != null) {
            NBTTagCompound compound = new NBTTagCompound();
            adjacent.writeExtraNBT(compound);
            tile.readExtraNBT(compound);
        }

        tile.rotation = l;

        if (entity instanceof EntityPlayer && !world.isRemote) {
            NoppesUtilServer.sendOpenGui((EntityPlayer) entity, EnumGuiType.Border, null, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    private TileBorder getTile(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileBorder)
            return (TileBorder) tile;
        return null;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileBorder();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ROTATION);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ROTATION);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ROTATION, Integer.valueOf(meta));
    }

    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.SaveTileEntity;
    }
}
