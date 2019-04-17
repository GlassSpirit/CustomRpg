// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst

package noppes.npcs.entity.old;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.ModelData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

// Referenced classes of package net.minecraft.src:
//            EntityAnimal, Item, EntityPlayer, InventoryPlayer,
//            ItemStack, World, NBTTagCompound

public class EntityNpcSkeleton extends EntityNPCInterface {
    public EntityNpcSkeleton(World world) {
        super(world);
        display.setSkinTexture("customnpcs:textures/entity/skeleton/Skeleton.png");
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
            data.setEntityClass(EntitySkeleton.class);


            world.spawnEntity(npc);
        }
        super.onUpdate();
    }
}
