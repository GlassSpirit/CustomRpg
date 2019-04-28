package noppes.npcs.objects.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import noppes.npcs.objects.CreativeTabNpcs;

public class ItemNpcInterface extends Item {
    private boolean damageAble = true;

    public ItemNpcInterface(int par1) {
        this();
    }

    public ItemNpcInterface() {
        setCreativeTab(CreativeTabNpcs.INSTANCE);
    }

    public void setUnDamageable() {
        damageAble = false;
    }

    public void playSound(EntityLivingBase entity, SoundEvent sound, float volume, float pitch) {
        entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, sound, SoundCategory.NEUTRAL, volume, pitch);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        subItems.add(new ItemStack(this, 1, 0));
    }

    @Override
    public int getItemEnchantability() {
        //return this.toolMaterial.getEnchantability();
        return super.getItemEnchantability();
    }

    @Override
    public Item setTranslationKey(String name) {
        super.setTranslationKey(name);
        setRegistryName(new ResourceLocation("customnpcs", name));
        return this;
    }

    @Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLiving, EntityLivingBase par3EntityLiving) {
        if (par2EntityLiving.getHealth() <= 0)
            return false;
        if (damageAble)
            par1ItemStack.damageItem(1, par3EntityLiving);
        return true;
    }

    public boolean hasItem(EntityPlayer player, Item item) {
        return getItemStack(player, item) != null;
    }

    public boolean consumeItem(EntityPlayer player, Item item) {
        ItemStack itemstack = getItemStack(player, item);
        if (itemstack == null)
            return false;

        itemstack.shrink(1);

        if (itemstack.getCount() == 0) {
            player.inventory.deleteStack(itemstack);
        }
        return true;
    }


    private ItemStack getItemStack(EntityPlayer player, Item item) {
        if (player.getHeldItem(EnumHand.OFF_HAND) != null && player.getHeldItem(EnumHand.OFF_HAND).getItem() == item) {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        if (player.getHeldItem(EnumHand.MAIN_HAND) != null && player.getHeldItem(EnumHand.MAIN_HAND).getItem() == item) {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);

            if (itemstack != null && itemstack.getItem() == item) {
                return itemstack;
            }
        }

        return null;

    }
}
