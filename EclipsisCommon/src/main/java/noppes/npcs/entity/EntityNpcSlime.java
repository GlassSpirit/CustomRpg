package noppes.npcs.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.ModelData;

public class EntityNpcSlime extends EntityNPCInterface {
    public EntityNpcSlime(World world) {
        super(world);
        scaleX = 2f;
        scaleY = 2f;
        scaleZ = 2f;
        display.setSkinTexture("customnpcs:textures/entity/slime/Slime.png");
        width = 0.8f;
        height = 0.8f;
    }

    @Override
    public void updateHitbox() {
        width = 0.8f;
        height = 0.8f;
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
            data.setEntityClass(EntityNpcSlime.class);


            world.spawnEntity(npc);
        }
        super.onUpdate();
    }
}
