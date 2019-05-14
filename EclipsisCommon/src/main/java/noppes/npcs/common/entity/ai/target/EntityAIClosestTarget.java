package noppes.npcs.common.entity.ai.target;

import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.Collections;
import java.util.List;

public class EntityAIClosestTarget extends EntityAITarget {
    private final Class targetClass;
    private final int targetChance;

    private final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
    private final Predicate field_82643_g;
    private EntityLivingBase targetEntity;
    private EntityNPCInterface npc;


    public EntityAIClosestTarget(EntityNPCInterface npc, Class par2Class, int par3, boolean par4, boolean par5, Predicate par6IEntitySelector) {
        super(npc, par4, par5);
        this.targetClass = par2Class;
        this.targetChance = par3;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(npc);
        this.setMutexBits(1);
        this.field_82643_g = par6IEntitySelector;
        this.npc = npc;
    }

    @Override
    public boolean shouldExecute() {
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
            return false;
        } else {
            double d0 = this.getTargetDistance();
            List list = this.taskOwner.world.getEntitiesWithinAABB(this.targetClass, this.taskOwner.getEntityBoundingBox().grow(d0, MathHelper.ceil(d0 / 2.0D), d0), this.field_82643_g);

            Collections.sort(list, this.theNearestAttackableTargetSorter);
            if (list.isEmpty()) {
                return false;
            } else {
                this.targetEntity = (EntityLivingBase) list.get(0);
                return true;
            }
        }
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.targetEntity);
        if (this.targetEntity instanceof EntityMob) {
            if (((EntityMob) this.targetEntity).getAttackTarget() == null) {
                ((EntityMob) this.targetEntity).setAttackTarget(this.taskOwner);
            }
        }
        super.startExecuting();
    }
}
