package noppes.npcs.common.entity.ai.selector;

import com.google.common.base.Predicate;
import noppes.npcs.common.entity.EntityNPCInterface;

public class NPCInteractSelector implements Predicate {
    private EntityNPCInterface npc;

    public NPCInteractSelector(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public boolean isEntityApplicable(EntityNPCInterface entity) {
        if (entity == npc || !npc.isEntityAlive())
            return false;
        return !entity.isAttacking() && !npc.getFaction().isAggressiveToNpc(entity) && npc.ais.stopAndInteract;
    }

    @Override
    public boolean apply(Object ob) {
        if (!(ob instanceof EntityNPCInterface))
            return false;
        return isEntityApplicable((EntityNPCInterface) ob);
    }

}
