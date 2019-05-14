package noppes.npcs.common.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.common.entity.EntityNPCInterface;

public class EntityAIRole extends EntityAIBase {

    private EntityNPCInterface npc;

    public EntityAIRole(EntityNPCInterface npc) {
        this.npc = npc;
    }

    @Override
    public boolean shouldExecute() {
        if (npc.isKilled() || npc.roleInterface == null)
            return false;
        return npc.roleInterface.aiShouldExecute();
    }

    @Override
    public void startExecuting() {
        npc.roleInterface.aiStartExecuting();
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (npc.isKilled() || npc.roleInterface == null)
            return false;
        return npc.roleInterface.aiContinueExecute();
    }

    @Override
    public void updateTask() {
        if (npc.roleInterface != null)
            npc.roleInterface.aiUpdateTask();
    }
}
