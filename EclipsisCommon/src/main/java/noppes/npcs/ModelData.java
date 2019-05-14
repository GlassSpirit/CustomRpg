package noppes.npcs;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.lang.reflect.Method;

public class ModelData extends ModelDataShared {

    public EntityLivingBase getEntity(EntityNPCInterface npc) {
        if (entityClass == null)
            return null;
        if (entity == null) {
            try {
                entity = entityClass.getConstructor(new Class[]{World.class}).newInstance(npc.world);

                if (PixelmonHelper.isPixelmon(entity) && npc.world.isRemote && !extra.hasKey("Name")) {
                    extra.setString("Name", "Abra");
                }

                try {
                    entity.readEntityFromNBT(extra);
                } catch (Exception e) {
                }

                entity.setEntityInvulnerable(true);
                entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(npc.getMaxHealth());
                for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                    entity.setItemStackToSlot(slot, npc.getItemStackFromSlot(slot));
                }
            } catch (Exception e) {
            }
        }
        return entity;
    }

    public ModelData copy() {
        ModelData data = new ModelData();
        data.readFromNBT(this.writeToNBT());
        return data;
    }


    public void setExtra(EntityLivingBase entity, String key, String value) {
        key = key.toLowerCase();

        if (key.equals("breed") && EntityList.getEntityString(entity).equals("tgvstyle.Dog")) {
            try {
                Method method = entity.getClass().getMethod("getBreedID");
                Enum breed = (Enum) method.invoke(entity);
                method = entity.getClass().getMethod("setBreedID", breed.getClass());
                method.invoke(entity, breed.getClass().getEnumConstants()[Integer.parseInt(value)]);
                NBTTagCompound comp = new NBTTagCompound();
                entity.writeEntityToNBT(comp);
                extra.setString("EntityData21", comp.getString("EntityData21"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (key.equalsIgnoreCase("name") && PixelmonHelper.isPixelmon(entity)) {
            extra.setString("Name", value);
        }
        clearEntity();
    }
}
