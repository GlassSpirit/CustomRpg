package noppes.npcs.api.wrapper;

import net.minecraft.entity.passive.EntityVillager;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.entity.IVillager;

public class EntityVillagerWrapper<T extends EntityVillager> extends EntityLivingWrapper<T> implements IVillager {

    public EntityVillagerWrapper(T entity) {
        super(entity);
    }

    public int getProfession() {
        return entity.getProfession();
    }

    public String getCareer() {
        return entity.getProfessionForge().getCareer(entity.careerId).getName();
    }

    @Override
    public int getType() {
        return EntityType.VILLAGER;
    }
}
