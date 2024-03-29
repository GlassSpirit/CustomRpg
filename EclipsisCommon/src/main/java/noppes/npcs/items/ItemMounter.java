package noppes.npcs.items;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomItems;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.util.IPermission;


public class ItemMounter extends Item implements IPermission {

    public ItemMounter() {
        maxStackSize = 1;
        setCreativeTab(CustomItems.tab);
    }

    @Override
    public Item setTranslationKey(String name) {
        setRegistryName(new ResourceLocation("customnpcs", name));
        return super.setTranslationKey(name);
    }

    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.SpawnRider || e == EnumPacketServer.PlayerRider || e == EnumPacketServer.CloneList;
    }
}
