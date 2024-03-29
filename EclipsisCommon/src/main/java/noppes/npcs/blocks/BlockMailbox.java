package noppes.npcs.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.npcs.Server;
import noppes.npcs.blocks.tiles.TileMailbox;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;

import java.util.ArrayList;

public class BlockMailbox extends BlockInterface {
    public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 3);
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 2);

    public BlockMailbox() {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
    }

    @Override
    public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
        par3List.add(new ItemStack(this, 1, 0));
        par3List.add(new ItemStack(this, 1, 1));
        par3List.add(new ItemStack(this, 1, 2));
    }

    @Override
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!par1World.isRemote) {
            Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI, EnumGuiType.PlayerMailbox, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        int damage = state.getValue(TYPE);
        ret.add(new ItemStack(this, 1, damage));
        return ret;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ROTATION) | state.getValue(TYPE) << 2;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, (Integer.valueOf(meta) >> 2) % 3).withProperty(ROTATION, Integer.valueOf((meta | 4) % 4));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, ROTATION);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        int l = MathHelper.floor((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockState(pos, state.withProperty(TYPE, stack.getItemDamage()).withProperty(ROTATION, l % 4), 2);
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
        return new TileMailbox();
    }
}
