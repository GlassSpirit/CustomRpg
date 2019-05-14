package noppes.npcs.common.entity.old;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.ModelData;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.common.entity.EntityCustomNpc;
import noppes.npcs.common.entity.EntityNPCInterface;

public class EntityNPCElfFemale extends EntityNPCInterface {
    public EntityNPCElfFemale(World world) {
        super(world);
        display.setSkinTexture("customnpcs:textures/entity/elffemale/ElfFemale.png");
        scaleX = 0.8f;
        scaleY = 1f;
        scaleZ = 0.8f;
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
            data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.8f, 1.05f);
            data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8f, 1.05f);
            data.getPartConfig(EnumParts.BODY).setScale(0.8f, 1.05f);
            data.getPartConfig(EnumParts.HEAD).setScale(0.8f, 0.85f);
            world.spawnEntity(npc);
        }
        super.onUpdate();
    }
}
