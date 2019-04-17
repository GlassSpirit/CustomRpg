package noppes.npcs.entity.old;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityNpcMonsterMale extends EntityNPCInterface {
    public EntityNpcMonsterMale(World world) {
        super(world);
        display.setSkinTexture("customnpcs:textures/entity/monstermale/ZombieSteve.png");
    }

    public void onUpdate() {
        isDead = true;
        setNoAI(true);

        if (!world.isRemote) {
            NBTTagCompound compound = new NBTTagCompound();

            writeToNBT(compound);
            EntityCustomNpc npc = new EntityCustomNpc(world);
            npc.readFromNBT(compound);
            npc.ais.animationType = AnimationType.HUG;
            world.spawnEntity(npc);
        }
        super.onUpdate();
    }

}
