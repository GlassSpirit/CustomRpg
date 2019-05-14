// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst

package noppes.npcs.common.entity.old;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.ModelData;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.common.entity.EntityCustomNpc;
import noppes.npcs.common.entity.EntityNPCInterface;

// Referenced classes of package net.minecraft.src:
//            EntityAnimal, Item, EntityPlayer, InventoryPlayer,
//            ItemStack, World, NBTTagCompound

public class EntityNPCDwarfMale extends EntityNPCInterface {
    public EntityNPCDwarfMale(World world) {
        super(world);
        scaleX = scaleZ = 0.85f;
        scaleY = 0.6875f;
        display.setSkinTexture("customnpcs:textures/entity/dwarfmale/Simon.png");
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
            data.getPartConfig(EnumParts.LEG_LEFT).setScale(1.1f, 0.7f, 0.9f);
            data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.9f, 0.7f);
            data.getPartConfig(EnumParts.BODY).setScale(1.2f, 0.7f, 1.5f);
            data.getPartConfig(EnumParts.HEAD).setScale(0.85f, 0.85f);
            world.spawnEntity(npc);
        }
        super.onUpdate();
    }
}
