package noppes.npcs.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.util.IPermission;

public class BlockScriptedDoor extends BlockNpcDoorInterface implements IPermission {
    //public static final PropertyInteger MODEL = PropertyInteger.create("model", 0, 6);

    public BlockScriptedDoor() {
        super();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileScriptedDoor();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return true;

        BlockPos blockpos1 = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
        IBlockState iblockstate1 = pos.equals(blockpos1) ? state : world.getBlockState(blockpos1);

        if (iblockstate1.getBlock() != this) {
            return false;
        }
        ItemStack currentItem = player.inventory.getCurrentItem();

        if (currentItem != null && (currentItem.getItem() == CustomItems.wand || currentItem.getItem() == CustomItems.scripter || currentItem.getItem() == CustomItems.scriptedDoorTool)) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.ScriptDoor, null, blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
            return true;
        }

        TileScriptedDoor tile = (TileScriptedDoor) world.getTileEntity(blockpos1);
        if (EventHooks.onScriptBlockInteract(tile, player, side.getIndex(), hitX, hitY, hitZ))
            return false;

        toggleDoor(world, blockpos1, iblockstate1.getValue(BlockDoor.OPEN).equals(false));
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos pos2) {
        if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
            BlockPos blockpos1 = pos.down();
            IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

            if (iblockstate1.getBlock() != this) {
                worldIn.setBlockToAir(pos);
            } else if (neighborBlock != this) {
                this.neighborChanged(iblockstate1, worldIn, blockpos1, neighborBlock, blockpos1);
            }
        } else {
            BlockPos blockpos2 = pos.up();
            IBlockState iblockstate2 = worldIn.getBlockState(blockpos2);

            if (iblockstate2.getBlock() != this) {
                worldIn.setBlockToAir(pos);
            } else {
                TileScriptedDoor tile = (TileScriptedDoor) worldIn.getTileEntity(pos);
                if (!worldIn.isRemote)
                    EventHooks.onScriptBlockNeighborChanged(tile);

                boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockpos2);

                if ((flag || neighborBlock.getDefaultState().canProvidePower()) && neighborBlock != this && flag != iblockstate2.getValue(POWERED).booleanValue()) {
                    worldIn.setBlockState(blockpos2, iblockstate2.withProperty(POWERED, Boolean.valueOf(flag)), 2);

                    if (flag != state.getValue(OPEN).booleanValue()) {
                        toggleDoor(worldIn, pos, flag);
                    }
                }

                int power = 0;
                for (EnumFacing enumfacing : EnumFacing.values()) {
                    int p = worldIn.getRedstonePower(pos.offset(enumfacing), enumfacing);
                    if (p > power)
                        power = p;
                }
                tile.newPower = power;
            }
        }
    }

    @Override
    public void toggleDoor(World worldIn, BlockPos pos, boolean open) {
        TileScriptedDoor tile = (TileScriptedDoor) worldIn.getTileEntity(pos);

        if (EventHooks.onScriptBlockDoorToggle(tile))
            return;

        super.toggleDoor(worldIn, pos, open);
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer playerIn) {
        if (world.isRemote)
            return;

        IBlockState state = world.getBlockState(pos);
        BlockPos blockpos1 = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
        IBlockState iblockstate1 = pos.equals(blockpos1) ? state : world.getBlockState(blockpos1);

        if (iblockstate1.getBlock() != this) {
            return;
        } else {
            TileScriptedDoor tile = (TileScriptedDoor) world.getTileEntity(blockpos1);
            EventHooks.onScriptBlockClicked(tile, playerIn);
        }

    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        BlockPos blockpos1 = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
        IBlockState iblockstate1 = pos.equals(blockpos1) ? state : world.getBlockState(blockpos1);

        if (!world.isRemote && iblockstate1.getBlock() == this) {
            TileScriptedDoor tile = (TileScriptedDoor) world.getTileEntity(pos);
            EventHooks.onScriptBlockBreak(tile);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (!world.isRemote) {
            TileScriptedDoor tile = (TileScriptedDoor) world.getTileEntity(pos);
            if (EventHooks.onScriptBlockHarvest(tile, player))
                return false;
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entityIn) {
        if (world.isRemote)
            return;
        TileScriptedDoor tile = (TileScriptedDoor) world.getTileEntity(pos);
        EventHooks.onScriptBlockCollide(tile, entityIn);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        BlockPos blockpos1 = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
        IBlockState iblockstate1 = pos.equals(blockpos1) ? state : world.getBlockState(blockpos1);
        if (player.capabilities.isCreativeMode && iblockstate1.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER && iblockstate1.getBlock() == this) {
            world.setBlockToAir(blockpos1);
        }
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        return ((TileScriptedDoor) world.getTileEntity(pos)).blockHardness;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        return ((TileScriptedDoor) world.getTileEntity(pos)).blockResistance;
    }

    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.ScriptDoorDataSave;
    }
}
