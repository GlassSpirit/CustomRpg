package noppes.npcs.api.wrapper;

import net.minecraft.entity.passive.EntityAnimal;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.entity.IAnimal;

public class AnimalWrapper<T extends EntityAnimal> extends EntityLivingWrapper<T> implements IAnimal {

    public AnimalWrapper(T entity) {
        super(entity);
    }

    @Override
    public int getType() {
        return EntityType.ANIMAL;
    }

    @Override
    public boolean typeOf(int type) {
        return type == EntityType.ANIMAL || super.typeOf(type);
    }
}
