package noppes.npcs.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsConfig;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartData;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.constants.EnumParts;

public class EntityCustomNpc extends EntityNPCFlying {
    public ModelData modelData = new ModelData();

    public EntityCustomNpc(World world) {
        super(world);
        if (!CustomNpcsConfig.EnableDefaultEyes) {
            modelData.eyes.type = -1;
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("NpcModelData"))
            modelData.readFromNBT(compound.getCompoundTag("NpcModelData"));
        super.readEntityFromNBT(compound);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setTag("NpcModelData", modelData.writeToNBT());
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound compound) {
        boolean bo = super.writeToNBTAtomically(compound);
        if (bo) {
            String s = getEntityString();
            if (s.equals("minecraft:customnpcs.customnpc")) {
                compound.setString("id", "customnpcs:customnpc");
            }
        }
        return bo;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (isRemote()) {
            ModelPartData particles = modelData.getPartData(EnumParts.PARTICLES);
            if (particles != null && !isKilled()) {
                CustomNpcs.proxy.spawnParticle(this, "ModelData", modelData, particles);
            }
            EntityLivingBase entity = modelData.getEntity(this);
            if (entity != null) {
                try {
                    entity.onUpdate();
                } catch (Exception e) {
                }
                EntityUtil.Copy(this, entity);
            }
        }
        modelData.eyes.update(this);
    }

    @Override
    public boolean startRiding(Entity par1Entity, boolean force) {
        boolean b = super.startRiding(par1Entity, force);
        updateHitbox();
        return b;
    }

    @Override
    public void updateHitbox() {
        Entity entity = modelData.getEntity(this);
        if (modelData == null || entity == null) {
            baseHeight = 1.9f - modelData.getBodyY() + (modelData.getPartConfig(EnumParts.HEAD).scaleY - 1) / 2;
            super.updateHitbox();
        } else {
            if (entity instanceof EntityNPCInterface)
                ((EntityNPCInterface) entity).updateHitbox();
            width = (entity.width / 5f) * display.getSize();
            height = (entity.height / 5f) * display.getSize();

            if (width < 0.1f)
                width = 0.1f;
            if (height < 0.1f)
                height = 0.1f;
            this.setPosition(posX, posY, posZ);
        }
    }
}
