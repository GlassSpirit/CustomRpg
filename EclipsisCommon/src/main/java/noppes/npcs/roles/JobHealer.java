package noppes.npcs.roles;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import noppes.npcs.NBTTags;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JobHealer extends JobInterface {
    private int healTicks = 0;
    public int range = 8;
    public byte type = 2; //0:friendly, 1:enemy, 2:all
    public int speed = 20;

    public HashMap<Integer, Integer> effects = new HashMap<Integer, Integer>();

    public JobHealer(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("HealerRange", range);
        nbttagcompound.setByte("HealerType", type);
        nbttagcompound.setTag("BeaconEffects", NBTTags.nbtIntegerIntegerMap(effects));
        nbttagcompound.setInteger("HealerSpeed", speed);
        return nbttagcompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        range = nbttagcompound.getInteger("HealerRange");
        type = nbttagcompound.getByte("HealerType");
        effects = NBTTags.getIntegerIntegerMap(nbttagcompound.getTagList("BeaconEffects", 10));
        speed = ValueUtil.CorrectInt(nbttagcompound.getInteger("HealerSpeed"), 10, Integer.MAX_VALUE);
    }

    private List<EntityLivingBase> affected = new ArrayList<EntityLivingBase>();

    //TODO heal food, heal potion effects, heal more types of entities besides just the player and npcs
    public boolean aiShouldExecute() {
        healTicks++;
        if (healTicks < speed)
            return false;
        healTicks = 0;
        affected = npc.world.getEntitiesWithinAABB(EntityLivingBase.class, npc.getEntityBoundingBox().grow((double) range, (double) range / 2.0D, (double) range));
        return !affected.isEmpty();
    }

    @Override
    public boolean aiContinueExecute() {
        return false;
    }

    public void aiStartExecuting() {
        for (EntityLivingBase entity : affected) {
            boolean isEnemy = false;
            if (entity instanceof EntityPlayer) {
                isEnemy = npc.faction.isAggressiveToPlayer((EntityPlayer) entity);
            } else if (entity instanceof EntityNPCInterface) {
                isEnemy = npc.faction.isAggressiveToNpc((EntityNPCInterface) entity);
            } else {
                isEnemy = entity instanceof EntityMob;
            }

            if (entity == npc || type == 0 && isEnemy || type == 1 && !isEnemy)
                continue;

            for (Integer potionEffect : effects.keySet()) {
                Potion p = Potion.getPotionById(potionEffect);
                if (p != null)
                    entity.addPotionEffect(new PotionEffect(p, 100, effects.get(potionEffect)));
            }
        }
        affected.clear();
    }
}
