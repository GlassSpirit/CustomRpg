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

public class EntityNPCOrcFemale extends EntityNPCInterface {
    public EntityNPCOrcFemale(World world) {
        super(world);
        scaleX = scaleY = scaleZ = 0.9375f;
        display.setSkinTexture("customnpcs:textures/entity/orcfemale/StrandedFemaleOrc.png");
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
            data.getPartConfig(EnumParts.LEG_LEFT).setScale(1.1f, 1);
            data.getPartConfig(EnumParts.ARM_LEFT).setScale(1.1f, 1);
            data.getPartConfig(EnumParts.BODY).setScale(1.1f, 1f, 1.25f);
            world.spawnEntity(npc);
        }
        super.onUpdate();
    }
}
