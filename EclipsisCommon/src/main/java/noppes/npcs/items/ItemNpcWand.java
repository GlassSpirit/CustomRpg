package noppes.npcs.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.IPermission;

public class ItemNpcWand extends Item implements IPermission {

    public ItemNpcWand() {
        maxStackSize = 1;
        setCreativeTab(CustomItems.tab);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!world.isRemote)
            return new ActionResult(EnumActionResult.SUCCESS, itemstack);
        CustomNpcs.proxy.openGui(0, 0, 0, EnumGuiType.NpcRemote, player);
        return new ActionResult(EnumActionResult.SUCCESS, itemstack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 72000;
    }

    @Override
    public EnumActionResult onItemUse(final EntityPlayer player, World world, BlockPos bpos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return EnumActionResult.SUCCESS;

        if (CustomNpcs.OpsOnly && !player.getServer().getPlayerList().canSendCommands(player.getGameProfile())) {
            player.sendMessage(new TextComponentTranslation("availability.permission"));
        } else if (CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.NPC_CREATE)) {
            final EntityCustomNpc npc = new EntityCustomNpc(world);
            npc.ais.setStartPos(bpos.up());
            npc.setLocationAndAngles((float) bpos.getX() + 0.5F, npc.getStartYPos(), (float) bpos.getZ() + 0.5F, player.rotationYaw, player.rotationPitch);

            world.spawnEntity(npc);
            npc.setHealth(npc.getMaxHealth());

            CustomNPCsScheduler.runTack(() -> NoppesUtilServer.sendOpenGui(player, EnumGuiType.MainMenuDisplay, npc), 100);
        } else
            player.sendMessage(new TextComponentTranslation("availability.permission"));
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase playerIn) {
        return stack;
    }

    @Override
    public Item setTranslationKey(String name) {
        setRegistryName(new ResourceLocation("customnpcs", name));
        return super.setTranslationKey(name);
    }

    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return true;
    }
}
