package noppes.npcs.objects.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcsConfig;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.objects.CreativeTabNpcs;
import noppes.npcs.util.IPermission;

import java.util.List;


public class ItemNpcMovingPath extends Item implements IPermission {
    public ItemNpcMovingPath() {
        maxStackSize = 1;
        setCreativeTab(CreativeTabNpcs.INSTANCE);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (world.isRemote || !CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.TOOL_MOUNTER))
            return new ActionResult(EnumActionResult.PASS, itemstack);
        EntityNPCInterface npc = getNpc(itemstack, world);
        if (npc != null)
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.MovingPath, npc);
        return new ActionResult(EnumActionResult.SUCCESS, itemstack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos bpos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote || !CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.TOOL_MOUNTER))
            return EnumActionResult.FAIL;
        ItemStack stack = player.getHeldItem(hand);
        EntityNPCInterface npc = getNpc(stack, world);
        if (npc == null)
            return EnumActionResult.PASS;
        List<int[]> list = npc.ais.getMovingPath();
        int[] pos = list.get(list.size() - 1);

        int x = bpos.getX(), y = bpos.getY(), z = bpos.getZ();
        list.add(new int[]{x, y, z});

        double d3 = x - pos[0];
        double d4 = y - pos[1];
        double d5 = z - pos[2];
        double distance = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

        player.sendMessage(new TextComponentString("Added point x:" + x + " y:" + y + " z:" + z + " to npc " + npc.getName()));
        if (distance > CustomNpcsConfig.NpcNavRange)
            player.sendMessage(new TextComponentString("Warning: point is too far away from previous point. Max block walk distance = " + CustomNpcsConfig.NpcNavRange));

        return EnumActionResult.SUCCESS;
    }

    private EntityNPCInterface getNpc(ItemStack item, World world) {
        if (world.isRemote || item.getTagCompound() == null)
            return null;

        Entity entity = world.getEntityByID(item.getTagCompound().getInteger("NPCID"));
        if (entity == null || !(entity instanceof EntityNPCInterface))
            return null;

        return (EntityNPCInterface) entity;
    }


    @Override
    public Item setTranslationKey(String name) {
        setRegistryName(new ResourceLocation("customnpcs", name));
        return super.setTranslationKey(name);
    }

    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.MovingPathGet || e == EnumPacketServer.MovingPathSave;
    }
}
