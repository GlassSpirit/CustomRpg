package noppes.npcs.ai;

import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class EntityAIMovingPath extends EntityAIBase {
    private EntityNPCInterface npc;
    private int[] pos;
    private int retries = 0;

    public EntityAIMovingPath(EntityNPCInterface iNpc) {
        this.npc = iNpc;
        this.setMutexBits(AiMutex.PASSIVE);
    }

    @Override
    public boolean shouldExecute() {
        if (npc.isAttacking() || npc.isInteracting() || npc.getRNG().nextInt(40) != 0 && npc.ais.movingPause || !npc.getNavigator().noPath())
            return false;

        List<int[]> list = npc.ais.getMovingPath();
        if (list.size() < 2)
            return false;

        npc.ais.incrementMovingPath();
        pos = npc.ais.getCurrentMovingPath();
        retries = 0;

        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (npc.isAttacking() || npc.isInteracting()) {
            npc.ais.decreaseMovingPath();
            return false;
        }
        if (this.npc.getNavigator().noPath()) {
            this.npc.getNavigator().clearPath();
            if (retries++ < 3) {
                startExecuting();
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public void startExecuting() {
        this.npc.getNavigator().tryMoveToXYZ(pos[0] + 0.5, pos[1], pos[2] + 0.5, 1.0D);
    }
}
