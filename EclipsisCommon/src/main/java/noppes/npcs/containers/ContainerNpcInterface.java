package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.MathHelper;

public class ContainerNpcInterface extends Container {
    private int posX, posZ;

    public ContainerNpcInterface(EntityPlayer player) {
        posX = MathHelper.floor(player.posX);
        posZ = MathHelper.floor(player.posZ);
        player.motionX = 0;
        player.motionZ = 0;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return !player.isDead && posX == MathHelper.floor(player.posX) && posZ == MathHelper.floor(player.posZ);
    }

}
