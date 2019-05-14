package noppes.npcs.roles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.util.NBTTags;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.List;

public class JobGuard extends JobInterface {

    public List<String> targets = new ArrayList<String>();

    public JobGuard(EntityNPCInterface npc) {
        super(npc);
    }

    public boolean isEntityApplicable(Entity entity) {
        if (entity instanceof EntityPlayer || entity instanceof EntityNPCInterface) return false;
        return targets.contains("entity." + EntityList.getEntityString(entity) + ".name");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setTag("GuardTargets", NBTTags.nbtStringList(targets));
        return nbttagcompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {

        targets = NBTTags.getStringList(nbttagcompound.getTagList("GuardTargets", 10));
        //Backwards Compatibility
        if (nbttagcompound.getBoolean("GuardAttackAnimals")) {
            for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
                Class<? extends Entity> cl = ent.getEntityClass();
                String name = "entity." + ent.getName() + ".name";
                if (EntityAnimal.class.isAssignableFrom(cl))
                    if (!targets.contains(name)) targets.add(name);
            }
        }
        if (nbttagcompound.getBoolean("GuardAttackMobs")) {
            for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
                Class<? extends Entity> cl = ent.getEntityClass();
                String name = "entity." + ent.getName() + ".name";
                if (EntityMob.class.isAssignableFrom(cl) && !EntityCreeper.class.isAssignableFrom(cl))
                    if (!targets.contains(name)) targets.add(name);
            }
        }
        if (nbttagcompound.getBoolean("GuardAttackCreepers")) {
            for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
                Class<? extends Entity> cl = ent.getEntityClass();
                String name = "entity." + ent.getName() + ".name";
                if (EntityCreeper.class.isAssignableFrom(cl))
                    if (!targets.contains(name)) targets.add(name);
            }
        }
        //End BC
    }
}
