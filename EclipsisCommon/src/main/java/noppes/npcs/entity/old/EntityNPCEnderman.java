package noppes.npcs.entity.old;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.ModelData;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.entity.EntityCustomNpc;

public class EntityNPCEnderman extends EntityNpcEnderchibi {
    public EntityNPCEnderman(World world) {
        super(world);
        display.setSkinTexture("customnpcs:textures/entity/enderman/enderman.png");
        display.setOverlayTexture("customnpcs:textures/overlays/ender_eyes.png");
        this.width = 0.6F;
        this.height = 2.9F;
    }

    public void updateHitbox() {

        if (currentAnimation == AnimationType.SLEEP) {
            width = height = 0.2f;
        } else if (currentAnimation == AnimationType.SIT) {
            width = 0.6f;
            height = 2.3f;
        } else {
            width = 0.6f;
            height = 2.9f;
        }
        width = (width / 5f) * display.getSize();
        height = (height / 5f) * display.getSize();
    }

    public void onUpdate() {
        isDead = true;
        setNoAI(true);

        if (!world.isRemote) {
            NBTTagCompound compound = new NBTTagCompound();

            writeToNBT(compound);
            EntityCustomNpc npc = new EntityCustomNpc(world);
            npc.readFromNBT(compound);
            ModelData data = npc.modelData;
            data.setEntityClass(EntityEnderman.class);

            world.spawnEntity(npc);
        }
        super.onUpdate();
    }
}