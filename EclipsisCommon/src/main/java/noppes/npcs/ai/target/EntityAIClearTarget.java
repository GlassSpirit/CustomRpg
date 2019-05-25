package noppes.npcs.ai.target;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIClearTarget extends EntityAIBase {
    private EntityNPCInterface npc;
    private EntityLivingBase target;

    public EntityAIClearTarget(EntityNPCInterface npc) {
        this.npc = npc;
    }

    @Override
    public boolean shouldExecute() {
        target = npc.getAttackTarget();
        if (target == null)
            return false;

        if (npc.getOwner() != null && !npc.isInRange(npc.getOwner(), npc.stats.getAggroRange() * 2)) {
            return true;
        }

        return npc.combatHandler.checkTarget();
    }

    @Override
    public void startExecuting() {
        this.npc.setAttackTarget(null);
        if (target == npc.getRevengeTarget())
            this.npc.setRevengeTarget(null);
        super.startExecuting();
    }

    @Override
    public void resetTask() {
        npc.getNavigator().clearPath();
    }
}
