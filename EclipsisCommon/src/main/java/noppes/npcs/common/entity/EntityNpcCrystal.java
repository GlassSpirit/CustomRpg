// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst

package noppes.npcs.common.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.ModelData;


public class EntityNpcCrystal extends EntityNPCInterface {
    public EntityNpcCrystal(World world) {
        super(world);
        scaleX = 0.7f;
        scaleY = 0.7f;
        scaleZ = 0.7f;
        display.setSkinTexture("customnpcs:textures/entity/crystal/EnderCrystal.png");
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
            data.setEntityClass(EntityNpcCrystal.class);


            world.spawnEntity(npc);
        }
        super.onUpdate();
    }

}
