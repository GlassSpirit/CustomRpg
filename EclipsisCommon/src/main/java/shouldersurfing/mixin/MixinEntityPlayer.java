package shouldersurfing.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import shouldersurfing.InjectionDelegation;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

    private MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Override
    public Vec3d getPositionEyes(float partialTicks) {
        return InjectionDelegation.INSTANCE.getPositionEyes(this, super.getPositionEyes(partialTicks));
    }
}
