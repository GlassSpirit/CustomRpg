package noppes.npcs.api.wrapper;

import net.minecraft.util.DamageSource;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;

public class DamageSourceWrapper implements IDamageSource {
    private DamageSource source;

    public DamageSourceWrapper(DamageSource source) {
        this.source = source;
    }

    @Override
    public String getType() {
        return source.getDamageType();
    }

    @Override
    public boolean isUnblockable() {
        return source.isUnblockable();
    }

    @Override
    public boolean isProjectile() {
        return source.isProjectile();
    }

    @Override
    public DamageSource getMCDamageSource() {
        return source;
    }

    @Override
    public IEntity getTrueSource() {
        return NpcAPI.instance().getIEntity(source.getTrueSource());
    }

    @Override
    public IEntity getImmediateSource() {
        return NpcAPI.instance().getIEntity(source.getImmediateSource());
    }

}
