package noppes.npcs.common.entity.ai.selector;

import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.roles.JobGuard;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.companion.CompanionGuard;

public class NPCAttackSelector implements Predicate<EntityLivingBase> {
    private EntityNPCInterface npc;

    public NPCAttackSelector(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public boolean isEntityApplicable(EntityLivingBase entity) {
        if (!entity.isEntityAlive() || entity == npc || !npc.isInRange(entity, npc.stats.aggroRange) || entity.getHealth() < 1)
            return false;
        if (this.npc.ais.directLOS && !this.npc.getEntitySenses().canSee(entity))
            return false;

        if (!npc.ais.attackInvisible && entity.isPotionActive(MobEffects.INVISIBILITY) && !npc.isInRange(entity, 3))
            return false;

        //prevent the npc from going on an endless killing spree
        if (!npc.isFollower() && npc.ais.shouldReturnHome()) {
            int allowedDistance = npc.stats.aggroRange * 2;
            if (npc.ais.getMovingType() == 1)
                allowedDistance += npc.ais.walkingRange;
            double distance = entity.getDistanceSq(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos());
            if (npc.ais.getMovingType() == 2) {
                int[] arr = npc.ais.getCurrentMovingPath();
                distance = entity.getDistanceSq(arr[0], arr[1], arr[2]);
            }

            if (distance > allowedDistance * allowedDistance)
                return false;
        }

        if (npc.advanced.job == JobType.GUARD && ((JobGuard) npc.jobInterface).isEntityApplicable(entity))
            return true;

        if (npc.advanced.role == RoleType.COMPANION) {
            RoleCompanion role = (RoleCompanion) npc.roleInterface;
            if (role.job == EnumCompanionJobs.GUARD && ((CompanionGuard) role.jobInterface).isEntityApplicable(entity))
                return true;
        }
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            return npc.faction.isAggressiveToPlayer(player) && !player.capabilities.disableDamage;
        }

        if (entity instanceof EntityNPCInterface) {
            if (((EntityNPCInterface) entity).isKilled())
                return false;
            if (npc.advanced.attackOtherFactions)
                return npc.faction.isAggressiveToNpc((EntityNPCInterface) entity);
        }

        return false;
    }

    @Override
    public boolean apply(EntityLivingBase ob) {
        return isEntityApplicable(ob);
    }
}
