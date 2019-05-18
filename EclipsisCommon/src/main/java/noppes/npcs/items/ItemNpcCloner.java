package noppes.npcs.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.util.IPermission;

public class ItemNpcCloner extends Item implements IPermission {

    public ItemNpcCloner() {
        maxStackSize = 1;
        setCreativeTab(CustomItems.tab);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote)
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.MobSpawner, null, pos.getX(), pos.getY(), pos.getZ());
        return EnumActionResult.SUCCESS;
    }

    @Override
    public Item setTranslationKey(String name) {
        setRegistryName(new ResourceLocation("customnpcs", name));
        return super.setTranslationKey(name);
    }

    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.CloneList || e == EnumPacketServer.SpawnMob || e == EnumPacketServer.MobSpawner ||
                e == EnumPacketServer.ClonePreSave || e == EnumPacketServer.CloneRemove || e == EnumPacketServer.CloneSave;
    }
}
