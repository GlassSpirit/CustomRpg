package noppes.npcs.entity.old;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.ModelData;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityNPCElfMale extends EntityNPCInterface {
    public EntityNPCElfMale(World world) {
        super(world);
        scaleX = 0.85f;
        scaleY = 1.07f;
        scaleZ = 0.85f;
        display.setSkinTexture("customnpcs:textures/entity/elfmale/ElfMale.png");
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
            data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.85f, 1.15f);
            data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.85f, 1.15f);
            data.getPartConfig(EnumParts.BODY).setScale(0.85f, 1.15f);
            data.getPartConfig(EnumParts.HEAD).setScale(0.85f, 0.95f);
            world.spawnEntity(npc);
        }
        super.onUpdate();
    }
}
