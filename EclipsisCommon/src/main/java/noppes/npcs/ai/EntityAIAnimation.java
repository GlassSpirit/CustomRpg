package noppes.npcs.ai;

import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIAnimation extends EntityAIBase {
    private EntityNPCInterface npc;

    private boolean isAttacking = false;
    private boolean isDead = false;
    private boolean isAtStartpoint = false;
    private boolean hasPath = false;
    private int tick = 4;

    public int temp = AnimationType.NORMAL;

    public EntityAIAnimation(EntityNPCInterface npc) {
        this.npc = npc;
    }

    @Override
    public boolean shouldExecute() {
        isDead = !npc.isEntityAlive();
        if (isDead)
            return npc.currentAnimation != AnimationType.SLEEP;
        if (npc.stats.ranged.getHasAimAnimation() && npc.isAttacking())
            return npc.currentAnimation != AnimationType.AIM;

        hasPath = !npc.getNavigator().noPath();
        isAttacking = npc.isAttacking();
        isAtStartpoint = npc.ais.shouldReturnHome() && npc.isVeryNearAssignedPlace();

        if (temp != AnimationType.NORMAL) {
            if (!hasNavigation())
                return npc.currentAnimation != temp;
            temp = AnimationType.NORMAL;
        }

        if (hasNavigation() && !isWalkingAnimation(npc.currentAnimation)) {
            return npc.currentAnimation != AnimationType.NORMAL;
        }

        return npc.currentAnimation != npc.ais.animationType;
    }

    @Override
    public void updateTask() {
        if (npc.stats.ranged.getHasAimAnimation() && npc.isAttacking()) {
            setAnimation(AnimationType.AIM);
            return;
        }
        int type = npc.ais.animationType;

        if (isDead)
            type = AnimationType.SLEEP;
        else if (!isWalkingAnimation(npc.ais.animationType) && hasNavigation())
            type = AnimationType.NORMAL;
        else if (temp != AnimationType.NORMAL) {
            if (hasNavigation())
                temp = AnimationType.NORMAL;
            else
                type = temp;
        }

        setAnimation(type);
    }

    @Override
    public void resetTask() {

    }

    public static int getWalkingAnimationGuiIndex(int animation) {
        if (animation == AnimationType.SNEAK)
            return 1;
        if (animation == AnimationType.AIM)
            return 2;
        if (animation == AnimationType.DANCE)
            return 3;
        if (animation == AnimationType.CRAWL)
            return 4;
        if (animation == AnimationType.HUG)
            return 5;
        return 0;
    }

    public static boolean isWalkingAnimation(int animation) {
        return getWalkingAnimationGuiIndex(animation) != 0;
    }

    private void setAnimation(int animation) {
        npc.setCurrentAnimation(animation);
        npc.updateHitbox();
        npc.setPosition(npc.posX, npc.posY, npc.posZ);
    }

    private boolean hasNavigation() {
        return (isAttacking || npc.ais.shouldReturnHome() && !isAtStartpoint && !npc.isFollower() || hasPath);
    }
}
