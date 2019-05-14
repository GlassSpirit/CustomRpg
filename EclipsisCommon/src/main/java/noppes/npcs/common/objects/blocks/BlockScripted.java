package noppes.npcs.common.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.common.objects.NpcObjects;
import noppes.npcs.common.objects.tiles.TileScripted;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.util.IPermission;

import java.util.Random;

public class BlockScripted extends BlockInterface implements IPermission {
    public static final AxisAlignedBB AABB = new AxisAlignedBB(0.001f, 0.001f, 0.001f, 0.998f, 0.998f, 0.998f);
    public static final AxisAlignedBB AABB_EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public BlockScripted() {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileScripted();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
        TileScripted tile = (TileScripted) world.getTileEntity(pos);
        if (tile != null && tile.isPassible)
            return AABB_EMPTY;
        return AABB;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return true;
        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem != null && (currentItem.getItem() == NpcObjects.wand || currentItem.getItem() == NpcObjects.scripter)) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.ScriptBlock, null, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        TileScripted tile = (TileScripted) world.getTileEntity(pos);
        return !EventHooks.onScriptBlockInteract(tile, player, side.getIndex(), hitX, hitY, hitZ);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        if (entity instanceof EntityPlayer && !world.isRemote) {
            NoppesUtilServer.sendOpenGui((EntityPlayer) entity, EnumGuiType.ScriptBlock, null, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entityIn) {
        if (world.isRemote)
            return;
        TileScripted tile = (TileScripted) world.getTileEntity(pos);
        EventHooks.onScriptBlockCollide(tile, entityIn);
    }

    @Override
    public void fillWithRain(World world, BlockPos pos) {
        if (world.isRemote)
            return;
        TileScripted tile = (TileScripted) world.getTileEntity(pos);
        EventHooks.onScriptBlockRainFill(tile);
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {
        if (world.isRemote)
            return;
        TileScripted tile = (TileScripted) world.getTileEntity(pos);
        fallDistance = EventHooks.onScriptBlockFallenUpon(tile, entity, fallDistance);
        super.onFallenUpon(world, pos, entity, fallDistance);
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
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        if (world.isRemote)
            return;
        TileScripted tile = (TileScripted) world.getTileEntity(pos);
        EventHooks.onScriptBlockClicked(tile, player);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            TileScripted tile = (TileScripted) world.getTileEntity(pos);
            EventHooks.onScriptBlockBreak(tile);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (!world.isRemote) {
            TileScripted tile = (TileScripted) world.getTileEntity(pos);
            if (EventHooks.onScriptBlockHarvest(tile, player))
                return false;
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        if (!world.isRemote) {
            TileScripted tile = (TileScripted) world.getTileEntity(pos);
            if (EventHooks.onScriptBlockExploded(tile))
                return;
        }
        super.onBlockExploded(world, pos, explosion);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos pos2) {
        if (world.isRemote)
            return;
        TileScripted tile = (TileScripted) world.getTileEntity(pos);
        EventHooks.onScriptBlockNeighborChanged(tile);

        int power = 0;
        for (EnumFacing enumfacing : EnumFacing.values()) {
            int p = world.getRedstonePower(pos.offset(enumfacing), enumfacing);
            if (p > power)
                power = p;
        }
        if (tile.prevPower != power && tile.powering <= 0) {
            tile.newPower = power;
        }
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }


    @Override
    public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return this.getStrongPower(state, worldIn, pos, side);
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return ((TileScripted) world.getTileEntity(pos)).activePowering;
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return ((TileScripted) world.getTileEntity(pos)).isLadder;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return true;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileScripted tile = (TileScripted) world.getTileEntity(pos);
        if (tile == null)
            return 0;
        return tile.lightValue;
    }

    @Override
    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        return ((TileScripted) world.getTileEntity(pos)).isPassible;
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return super.canEntityDestroy(state, world, pos, entity);
    }

    @Override
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        return super.getEnchantPowerBonus(world, pos);
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        return ((TileScripted) world.getTileEntity(pos)).blockHardness;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        return ((TileScripted) world.getTileEntity(pos)).blockResistance;
    }

    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.SaveTileEntity || e == EnumPacketServer.ScriptBlockDataSave;
    }
}
