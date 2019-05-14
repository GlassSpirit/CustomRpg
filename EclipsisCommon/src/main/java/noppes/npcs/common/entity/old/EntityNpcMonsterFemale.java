package noppes.npcs.common.entity.old;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.ModelData;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.common.entity.EntityCustomNpc;
import noppes.npcs.common.entity.EntityNPCInterface;

public class EntityNpcMonsterFemale extends EntityNPCInterface {
    public EntityNpcMonsterFemale(World world) {
        super(world);
        scaleX = scaleY = scaleZ = 0.9075f;
        display.setSkinTexture("customnpcs:textures/entity/monsterfemale/ZombieStephanie.png");
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
            data.getOrCreatePart(EnumParts.BREASTS).type = 2;
            data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.92f, 0.92f);
            data.getPartConfig(EnumParts.HEAD).setScale(0.95f, 0.95f);
            data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.80f, 0.92f);
            data.getPartConfig(EnumParts.BODY).setScale(0.92f, 0.92f);
            npc.ais.animationType = AnimationType.HUG;
            world.spawnEntity(npc);
        }
        super.onUpdate();
    }

}
