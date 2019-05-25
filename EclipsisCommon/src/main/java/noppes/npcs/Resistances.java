package noppes.npcs;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class Resistances {

    public float knockback = 1f;
    public float arrow = 1f;
    public float melee = 1f;
    public float explosion = 1f;

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setFloat("Knockback", knockback);
        compound.setFloat("Arrow", arrow);
        compound.setFloat("Melee", melee);
        compound.setFloat("Explosion", explosion);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        knockback = compound.getFloat("Knockback");
        arrow = compound.getFloat("Arrow");
        melee = compound.getFloat("Melee");
        explosion = compound.getFloat("Explosion");
    }

    public float applyResistance(DamageSource source, float damage) {
        if (source.damageType.equals("arrow") || source.damageType.equals("thrown") || source.isProjectile()) {
            damage *= (2 - arrow);
        } else if (source.damageType.equals("player") || source.damageType.equals("mob")) {
            damage *= (2 - melee);
        } else if (source.damageType.equals("explosion") || source.damageType.equals("explosion.player")) {
            damage *= (2 - explosion);
        }

        return damage;
    }

}
