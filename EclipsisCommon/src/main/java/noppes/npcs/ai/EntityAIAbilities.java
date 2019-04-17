package noppes.npcs.ai;

import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.ability.AbstractAbility;
import noppes.npcs.ability.IAbilityUpdate;
import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIAbilities extends EntityAIBase {
    private EntityNPCInterface npc;
    private IAbilityUpdate ability;

    public EntityAIAbilities(EntityNPCInterface npc) {
        this.npc = npc;
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.isAttacking())
            return false;
        ability = (IAbilityUpdate) npc.abilities.getAbility(EnumAbilityType.UPDATE);
        return ability != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return npc.isAttacking() && ability.isActive();
    }

    @Override
    public void updateTask() {
        ability.update();
    }

    @Override
    public void resetTask() {
        ((AbstractAbility) ability).endAbility();
        ability = null;
    }

}
