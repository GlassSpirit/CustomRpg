// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst

package noppes.npcs.entity.old;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.ModelData;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

// Referenced classes of package net.minecraft.src:
//            EntityAnimal, Item, EntityPlayer, InventoryPlayer,
//            ItemStack, World, NBTTagCompound

public class EntityNPCOrcMale extends EntityNPCInterface {
    public EntityNPCOrcMale(World world) {
        super(world);
        scaleY = 1f;
        scaleX = scaleZ = 1.2f;
        display.setSkinTexture("customnpcs:textures/entity/orcmale/StrandedOrc.png");
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
            data.getPartConfig(EnumParts.LEG_LEFT).setScale(1.2f, 1.05f);
            data.getPartConfig(EnumParts.ARM_LEFT).setScale(1.2f, 1.05f);
            data.getPartConfig(EnumParts.BODY).setScale(1.4f, 1.1f, 1.5f);
            data.getPartConfig(EnumParts.HEAD).setScale(1.2f, 1.1f);
            world.spawnEntity(npc);
        }
        super.onUpdate();
    }
}
