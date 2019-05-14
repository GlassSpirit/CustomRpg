package noppes.npcs.common.objects.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.common.objects.NpcObjects;
import noppes.npcs.common.objects.tiles.TileRedstoneBlock;
import noppes.npcs.util.IPermission;

public class BlockNpcRedstone extends BlockInterface implements IPermission {
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockNpcRedstone() {
        super(Material.ROCK);
    }

    @Override
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (par1World.isRemote)
            return false;
        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() == NpcObjects.wand && CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.EDIT_BLOCKS)) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.RedstoneBlock, null, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }

    @Override
    public void onBlockAdded(World par1World, BlockPos pos, IBlockState state) {
        par1World.notifyNeighborsOfStateChange(pos, this, false);
        par1World.notifyNeighborsOfStateChange(pos.down(), this, false);
        par1World.notifyNeighborsOfStateChange(pos.up(), this, false);
        par1World.notifyNeighborsOfStateChange(pos.west(), this, false);
        par1World.notifyNeighborsOfStateChange(pos.east(), this, false);
        par1World.notifyNeighborsOfStateChange(pos.south(), this, false);
        par1World.notifyNeighborsOfStateChange(pos.north(), this, false);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityliving, ItemStack item) {
        if (entityliving instanceof EntityPlayer && !world.isRemote) {
            NoppesUtilServer.sendOpenGui((EntityPlayer) entityliving, EnumGuiType.RedstoneBlock, null, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public void onPlayerDestroy(World par1World, BlockPos pos, IBlockState state) {
        onBlockAdded(par1World, pos, state);
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return isActivated(state);
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return isActivated(state);
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(ACTIVE) ? 1 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ACTIVE, false);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }

    public int isActivated(IBlockState state) {
        return state.getValue(ACTIVE) ? 15 : 0;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileRedstoneBlock();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.SaveTileEntity;
    }
}
