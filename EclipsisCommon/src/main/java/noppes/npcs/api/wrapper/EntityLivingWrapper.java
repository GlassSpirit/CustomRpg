package noppes.npcs.api.wrapper;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.IPos;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.IEntityLivingBase;

public class EntityLivingWrapper<T extends EntityLiving> extends EntityLivingBaseWrapper<T> implements IEntityLiving {

    public EntityLivingWrapper(T entity) {
        super(entity);
    }

    @Override
    public void navigateTo(double x, double y, double z, double speed) {
        entity.getNavigator().clearPath();
        entity.getNavigator().tryMoveToXYZ(x, y, z, speed * 0.7);
    }

    @Override
    public void clearNavigation() {
        entity.getNavigator().clearPath();
    }

    @Override
    public IPos getNavigationPath() {
        if (!isNavigating()) {
            return null;
        }
        PathPoint point = entity.getNavigator().getPath().getFinalPathPoint();
        if (point == null) {
            return null;
        }
        return new BlockPosWrapper(new BlockPos(point.x, point.y, point.z));
    }

    @Override
    public boolean isNavigating() {
        return !entity.getNavigator().noPath();
    }

    @Override
    public boolean isAttacking() {
        return super.isAttacking() || entity.getAttackTarget() != null;
    }

    @Override
    public void setAttackTarget(IEntityLivingBase living) {
        if (living == null)
            entity.setAttackTarget(null);
        else
            entity.setAttackTarget(living.getMCEntity());
        super.setAttackTarget(living);
    }

    @Override
    public IEntityLivingBase getAttackTarget() {
        IEntityLivingBase base = (IEntityLivingBase) NpcAPI.instance().getIEntity(entity.getAttackTarget());
        return (base != null) ? base : super.getAttackTarget();
    }

    @Override
    public boolean canSeeEntity(IEntity entity) {
        return this.entity.getEntitySenses().canSee(entity.getMCEntity());
    }

    @Override
    public void jump() {
        entity.getJumpHelper().setJumping();
    }
}
