// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst

package noppes.npcs.entity.old;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartData;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

// Referenced classes of package net.minecraft.src:
//            EntityAnimal, Item, EntityPlayer, InventoryPlayer,
//            ItemStack, World, NBTTagCompound

public class EntityNpcNagaMale extends EntityNPCInterface {
    public EntityNpcNagaMale(World world) {
        super(world);
        display.setSkinTexture("customnpcs:textures/entity/nagamale/Cobra.png");
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
            ModelPartData legs = data.getOrCreatePart(EnumParts.LEGS);
            legs.playerTexture = true;
            legs.type = 1;

            world.spawnEntity(npc);
        }
        super.onUpdate();
    }
}
