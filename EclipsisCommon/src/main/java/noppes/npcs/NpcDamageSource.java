package noppes.npcs;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class NpcDamageSource extends EntityDamageSource {

    public NpcDamageSource(String type, Entity source) {
        super(type, source);
    }

    @Override
    public boolean isDifficultyScaled() {
        return false;
    }
}
