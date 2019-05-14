package noppes.npcs.common.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigateGround;
import noppes.npcs.common.entity.EntityNPCInterface;

public class EntityAIWaterNav extends EntityAIBase {
    private EntityNPCInterface entity;

    public EntityAIWaterNav(EntityNPCInterface iNpc) {
        this.entity = iNpc;
        ((PathNavigateGround) iNpc.getNavigator()).setCanSwim(true);
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.isInWater() || this.entity.isInLava()) {
            if (this.entity.ais.canSwim) {
                return true;
            } else return this.entity.collidedHorizontally;

        }
        return false;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        if (this.entity.getRNG().nextFloat() < 0.8F) {
            this.entity.getJumpHelper().setJumping();
        }
    }
}
