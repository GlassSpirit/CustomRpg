package noppes.npcs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.constants.EnumParts;

import java.util.HashMap;


public class ModelDataShared {
    protected ModelPartConfig arm1 = new ModelPartConfig();
    protected ModelPartConfig arm2 = new ModelPartConfig();
    protected ModelPartConfig body = new ModelPartConfig();
    protected ModelPartConfig leg1 = new ModelPartConfig();
    protected ModelPartConfig leg2 = new ModelPartConfig();
    protected ModelPartConfig head = new ModelPartConfig();

    protected ModelPartData legParts = new ModelPartData("legs");
    public ModelEyeData eyes = new ModelEyeData();

    public Class<? extends EntityLivingBase> entityClass;
    protected EntityLivingBase entity;

    public NBTTagCompound extra = new NBTTagCompound();

    protected HashMap<EnumParts, ModelPartData> parts = new HashMap<EnumParts, ModelPartData>();

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();

        if (entityClass != null) {
            compound.setString("EntityClass", entityClass.getCanonicalName());
        }

        compound.setTag("ArmsConfig", arm1.writeToNBT());
        compound.setTag("BodyConfig", body.writeToNBT());
        compound.setTag("LegsConfig", leg1.writeToNBT());
        compound.setTag("HeadConfig", head.writeToNBT());

        compound.setTag("LegParts", legParts.writeToNBT());
        compound.setTag("Eyes", eyes.writeToNBT());

        compound.setTag("ExtraData", extra);

        NBTTagList list = new NBTTagList();
        for (EnumParts e : parts.keySet()) {
            NBTTagCompound item = parts.get(e).writeToNBT();
            item.setString("PartName", e.name);
            list.appendTag(item);
        }
        compound.setTag("Parts", list);

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        setEntityName(compound.getString("EntityClass"));

        arm1.readFromNBT(compound.getCompoundTag("ArmsConfig"));
        body.readFromNBT(compound.getCompoundTag("BodyConfig"));
        leg1.readFromNBT(compound.getCompoundTag("LegsConfig"));
        head.readFromNBT(compound.getCompoundTag("HeadConfig"));

        legParts.readFromNBT(compound.getCompoundTag("LegParts"));
        eyes.readFromNBT(compound.getCompoundTag("Eyes"));

        extra = compound.getCompoundTag("ExtraData");

        HashMap<EnumParts, ModelPartData> parts = new HashMap<EnumParts, ModelPartData>();
        NBTTagList list = compound.getTagList("Parts", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound item = list.getCompoundTagAt(i);
            String name = item.getString("PartName");
            ModelPartData part = new ModelPartData(name);
            part.readFromNBT(item);
            EnumParts e = EnumParts.FromName(name);
            if (e != null)
                parts.put(e, part);
        }
        this.parts = parts;
        updateTransate();
    }

    private void updateTransate() {
        for (EnumParts part : EnumParts.values()) {
            ModelPartConfig config = getPartConfig(part);
            if (config == null)
                continue;
            if (part == EnumParts.HEAD) {
                config.setTranslate(0, getBodyY(), 0);
            } else if (part == EnumParts.ARM_LEFT) {
                ModelPartConfig body = getPartConfig(EnumParts.BODY);
                float x = (1 - body.scaleX) * 0.25f + (1 - config.scaleX) * 0.075f;
                float y = getBodyY() + (1 - config.scaleY) * -0.1f;
                config.setTranslate(-x, y, 0);
                if (!config.notShared) {
                    ModelPartConfig arm = getPartConfig(EnumParts.ARM_RIGHT);
                    arm.copyValues(config);
                }
            } else if (part == EnumParts.ARM_RIGHT) {
                ModelPartConfig body = getPartConfig(EnumParts.BODY);
                float x = (1 - body.scaleX) * 0.25f + (1 - config.scaleX) * 0.075f;
                float y = getBodyY() + (1 - config.scaleY) * -0.1f;
                config.setTranslate(x, y, 0);
            } else if (part == EnumParts.LEG_LEFT) {
                config.setTranslate((config.scaleX) * 0.125f - 0.113f, getLegsY(), 0);
                if (!config.notShared) {
                    ModelPartConfig leg = getPartConfig(EnumParts.LEG_RIGHT);
                    leg.copyValues(config);
                }
            } else if (part == EnumParts.LEG_RIGHT) {
                config.setTranslate((1 - config.scaleX) * 0.125f, getLegsY(), 0);
            } else if (part == EnumParts.BODY) {
                config.setTranslate(0, getBodyY(), 0);
            }
        }
    }

    public void setEntityName(String string) {
        entityClass = null;
        entity = null;
        for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
            Class<? extends Entity> c = ent.getEntityClass();
            if (c.getCanonicalName().equals(string) && EntityLivingBase.class.isAssignableFrom(c)) {
                entityClass = c.asSubclass(EntityLivingBase.class);
                break;
            }
        }
    }

    public void setEntityClass(Class<? extends EntityLivingBase> entityClass) {
        this.entityClass = entityClass;
        entity = null;
        extra = new NBTTagCompound();
    }

    public Class<? extends EntityLivingBase> getEntityClass() {
        return entityClass;
    }

    public float offsetY() {
        if (entity == null)
            return -getBodyY();
        return entity.height - 1.8f;
    }

    public void clearEntity() {
        entity = null;
    }


    public ModelPartData getPartData(EnumParts type) {
        if (type == EnumParts.LEGS)
            return legParts;
        if (type == EnumParts.EYES)
            return eyes;
        return parts.get(type);
    }

    public ModelPartConfig getPartConfig(EnumParts type) {
        if (type == EnumParts.BODY)
            return body;
        if (type == EnumParts.ARM_LEFT)
            return arm1;
        if (type == EnumParts.ARM_RIGHT)
            return arm2;
        if (type == EnumParts.LEG_LEFT)
            return leg1;
        if (type == EnumParts.LEG_RIGHT)
            return leg2;

        return head;
    }

    public void removePart(EnumParts type) {
        parts.remove(type);
    }

    public ModelPartData getOrCreatePart(EnumParts type) {
        if (type == null)
            return null;
        if (type == EnumParts.EYES) {
            return eyes;
        }
        ModelPartData part = getPartData(type);
        if (part == null)
            parts.put(type, part = new ModelPartData(type.name));
        return part;
    }

    public float getBodyY() {
//		if(legParts.type == 3)
//			return (0.9f - body.scaleY) * 0.75f + getLegsY();
//		if(legParts.type == 3)
//			return (0.5f - body.scaleY) * 0.75f + getLegsY();
        return (1 - body.scaleY) * 0.75f + getLegsY();
    }

    public float getLegsY() {
        ModelPartConfig legs = leg1;
        if (leg2.notShared && leg2.scaleY > leg1.scaleY)
            legs = leg2;
//		if(legParts.type == 3)
//			return (0.87f - legs.scaleY) * 1f;
        return (1 - legs.scaleY) * 0.75f;
    }
}
