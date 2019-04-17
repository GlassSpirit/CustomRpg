package noppes.npcs.api.wrapper;

import net.minecraft.entity.monster.EntityMob;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.entity.IMonster;

public class MonsterWrapper<T extends EntityMob> extends EntityLivingWrapper<T> implements IMonster {
    public MonsterWrapper(T entity) {
        super(entity);
    }

    @Override
    public int getType() {
        return EntityType.MONSTER;
    }

    @Override
    public boolean typeOf(int type) {
        return type == EntityType.MONSTER || super.typeOf(type);
    }
}
