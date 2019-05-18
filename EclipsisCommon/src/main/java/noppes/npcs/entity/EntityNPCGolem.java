package noppes.npcs.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.ModelData;
import noppes.npcs.api.constants.AnimationType;

public class EntityNPCGolem extends EntityNPCInterface {

    public EntityNPCGolem(World world) {
        super(world);
        display.setSkinTexture("customnpcs:textures/entity/golem/Iron Golem.png");

        width = 1.4f;
        height = 2.5f;
    }

    @Override
    public void updateHitbox() {
        currentAnimation = dataManager.get(Animation);
        if (currentAnimation == AnimationType.SLEEP) {
            width = height = 0.5f;
        } else if (currentAnimation == AnimationType.SIT) {
            width = 1.4f;
            height = 2f;
        } else {
            width = 1.4f;
            height = 2.5f;
        }
    }

    @Override
    public void onUpdate() {
        isDead = true;
        setNoAI(true);

        if (!world.isRemote) {
            NBTTagCompound compound = new NBTTagCompound();

            writeToNBT(compound);
            EntityCustomNpc npc = new EntityCustomNpc(world);
            npc.readFromNBT(compound);
            ModelData data = npc.modelData;
            data.setEntityClass(EntityNPCGolem.class);


            world.spawnEntity(npc);
        }
        super.onUpdate();
    }
}