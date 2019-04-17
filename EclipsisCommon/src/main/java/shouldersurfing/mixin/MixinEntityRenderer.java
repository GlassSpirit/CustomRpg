package shouldersurfing.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shouldersurfing.InjectionDelegation;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Final
    @Shadow
    private Minecraft mc;

    @Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"))
    private RayTraceResult getRayTraceResult(WorldClient worldClient, Vec3d start, Vec3d end) {
        return InjectionDelegation.INSTANCE.getRayTraceResult(worldClient, start, end);
    }

    @Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;distanceTo(Lnet/minecraft/util/math/Vec3d;)D"))
    private double verifyReverseBlockDist(Vec3d hitVec, Vec3d vec) {
        double distance = hitVec.distanceTo(vec);
        InjectionDelegation.INSTANCE.verifyReverseBlockDist(distance);
        return distance;
    }

    @ModifyVariable(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", opcode = Opcodes.GETFIELD, ordinal = 1), name = "f1")
    private float applyShoulderRotationYaw(float f1) {
        return f1 + InjectionDelegation.INSTANCE.getShoulderRotationYaw();
    }

    @ModifyVariable(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", opcode = Opcodes.GETFIELD, ordinal = 1), name = "f2")
    private float applyShoulderRotationPitch(float f2) {
        return f2 + InjectionDelegation.INSTANCE.getShoulderRotationPitch();
    }

    @ModifyVariable(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", opcode = Opcodes.GETFIELD, ordinal = 1), name = "d3")
    private double applyShoulderZoomMod(double d3) {
        return d3 * InjectionDelegation.INSTANCE.getShoulderZoomMod();
    }

    @ModifyVariable(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", opcode = Opcodes.GETFIELD, ordinal = 2), name = "d3")
    private double checkDistance(double d3) {
        Entity entity = mc.getRenderViewEntity();
        float partialTicks = mc.getRenderPartialTicks();
        float f = entity.getEyeHeight();
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
        float f1 = entity.rotationYaw + InjectionDelegation.INSTANCE.getShoulderRotationYaw();
        float f2 = entity.rotationPitch + InjectionDelegation.INSTANCE.getShoulderRotationPitch();
        if (mc.gameSettings.thirdPersonView == 2) {
            f2 += 180.0F;
        }
        double d4 = (double) (-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
        double d5 = (double) (MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
        double d6 = (double) (-MathHelper.sin(f2 * 0.017453292F)) * d3;
        return InjectionDelegation.INSTANCE.checkDistance(d3, f1, d0, d1, d2, d4, d5, d6);
    }

    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/culling/ClippingHelperImpl;getInstance()Lnet/minecraft/client/renderer/culling/ClippingHelper;", shift = At.Shift.AFTER))
    private void calculateRayTraceProjection(CallbackInfo ci) {
        InjectionDelegation.INSTANCE.calculateRayTraceProjection();
    }
}
