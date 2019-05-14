package noppes.npcs.common.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.common.entity.EntityNPCInterface;

public class EntityAITransform extends EntityAIBase {

    private EntityNPCInterface npc;

    public EntityAITransform(EntityNPCInterface npc) {
        this.npc = npc;
        setMutexBits(AiMutex.PASSIVE);
    }

    @Override
    public boolean shouldExecute() {
        if (npc.isKilled() || npc.isAttacking() || npc.transform.editingModus)
            return false;

        return (npc.world.getWorldTime() % 24000 < 12000) == npc.transform.isActive;
    }

    @Override
    public void startExecuting() {
        npc.transform.transform(!npc.transform.isActive);
    }
}
