package noppes.npcs.objects.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.objects.CreativeTabNpcs;
import noppes.npcs.util.IPermission;

import java.util.HashMap;
import java.util.Map;

public class ItemScripted extends Item implements IPermission {
    public static Map<Integer, String> Resources = new HashMap<>();

    public ItemScripted() {
        maxStackSize = 1;
        setCreativeTab(CreativeTabNpcs.INSTANCE);
        setHasSubtypes(true);
    }

    @Override
    public Item setTranslationKey(String name) {
        super.setTranslationKey(name);
        setRegistryName(new ResourceLocation("customnpcs", name));
        return this;
    }

    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.ScriptItemDataGet || e == EnumPacketServer.ScriptItemDataSave;
    }

    public static ItemScriptedWrapper GetWrapper(ItemStack stack) {
        return (ItemScriptedWrapper) NpcAPI.Instance().getIItemStack(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        if (istack instanceof ItemScriptedWrapper)
            return ((ItemScriptedWrapper) istack).durabilityShow;
        return super.showDurabilityBar(stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        if (istack instanceof ItemScriptedWrapper)
            return 1 - ((ItemScriptedWrapper) istack).durabilityValue;
        return super.getDurabilityForDisplay(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        if (!(istack instanceof ItemScriptedWrapper))
            return super.getRGBDurabilityForDisplay(stack);
        int color = ((ItemScriptedWrapper) istack).durabilityColor;
        if (color >= 0)
            return color;
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        if (istack instanceof ItemScriptedWrapper)
            return istack.getMaxStackSize();
        return super.getItemStackLimit(stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return true;
    }
}
