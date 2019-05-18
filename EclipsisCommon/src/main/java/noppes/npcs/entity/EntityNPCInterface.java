package noppes.npcs.entity;

import com.google.common.base.Predicate;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import noppes.npcs.*;
import noppes.npcs.ai.EntityAIFollow;
import noppes.npcs.ai.EntityAIMoveIndoors;
import noppes.npcs.ai.EntityAIPanic;
import noppes.npcs.ai.EntityAIWander;
import noppes.npcs.ai.EntityAIWatchClosest;
import noppes.npcs.ai.*;
import noppes.npcs.ai.selector.NPCAttackSelector;
import noppes.npcs.ai.target.EntityAIClearTarget;
import noppes.npcs.ai.target.EntityAIClosestTarget;
import noppes.npcs.ai.target.EntityAIOwnerHurtByTarget;
import noppes.npcs.ai.target.EntityAIOwnerHurtTarget;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.*;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.controllers.LinkedNpcController.LinkedData;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.data.*;
import noppes.npcs.items.ItemSoulstoneFilled;
import noppes.npcs.roles.*;
import noppes.npcs.util.GameProfileAlt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class EntityNPCInterface extends EntityCreature implements IEntityAdditionalSpawnData, ICommandSender, IRangedAttackMob, IAnimals {
    public static final DataParameter<Boolean> Attacking = EntityDataManager.createKey(EntityNPCInterface.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Integer> Animation = EntityDataManager.createKey(EntityNPCInterface.class, DataSerializers.VARINT);
    private static final DataParameter<String> RoleData = EntityDataManager.createKey(EntityNPCInterface.class, DataSerializers.STRING);
    private static final DataParameter<String> JobData = EntityDataManager.createKey(EntityNPCInterface.class, DataSerializers.STRING);
    private static final DataParameter<Integer> FactionData = EntityDataManager.createKey(EntityNPCInterface.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> Walking = EntityDataManager.createKey(EntityNPCInterface.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Interacting = EntityDataManager.createKey(EntityNPCInterface.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Killed = EntityDataManager.createKey(EntityNPCInterface.class, DataSerializers.BOOLEAN);

    public static final GameProfileAlt CommandProfile = new GameProfileAlt();
    public static final GameProfileAlt ChatEventProfile = new GameProfileAlt();
    public static FakePlayer ChatEventPlayer;
    public static FakePlayer CommandPlayer;

    public ICustomNpc wrappedNPC;

    public DataAbilities abilities;
    public DataDisplay display;
    public DataStats stats;
    public DataAI ais;
    public DataAdvanced advanced;
    public DataInventory inventory;
    public DataScript script;
    public DataTransform transform;
    public DataTimers timers;

    public CombatHandler combatHandler = new CombatHandler(this);

    public String linkedName = "";
    public long linkedLast = 0;
    public LinkedData linkedData;

    public float baseHeight = 1.8f;
    public float scaleX, scaleY, scaleZ;
    private boolean wasKilled = false;
    public RoleInterface roleInterface;
    public JobInterface jobInterface;
    public HashMap<Integer, DialogOption> dialogs;
    public boolean hasDied = false;
    public long killedtime = 0;
    public long totalTicksAlive = 0;
    private int taskCount = 1;
    public int lastInteract = 0;
    public Faction faction; //should only be used server side

    private EntityAIRangedAttack aiRange;
    private EntityAIBase aiAttackTarget;
    public EntityAILook lookAi;
    public EntityAIAnimation animateAi;

    public List<EntityLivingBase> interactingEntities = new ArrayList<>();

    public ResourceLocation textureLocation = null;
    public ResourceLocation textureGlowLocation = null;
    public ResourceLocation textureCloakLocation = null;

    public int currentAnimation = AnimationType.NORMAL;
    public int animationStart = 0;

    public int npcVersion = VersionCompatibility.ModRev;
    public IChatMessages messages;

    public boolean updateClient = false;
    public boolean updateAI = false;

    public final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);

    public EntityNPCInterface(World world) {
        super(world);
        if (!isRemote())
            wrappedNPC = new NPCWrapper(this);
        dialogs = new HashMap<>();
        if (!CustomNpcs.DefaultInteractLine.isEmpty())
            advanced.interactLines.lines.put(0, new Line(CustomNpcs.DefaultInteractLine));

        experienceValue = 0;
        scaleX = scaleY = scaleZ = 0.9375f;

        faction = getFaction();
        setFaction(faction.id);
        setSize(1, 1);
        updateAI = true;
        bossInfo.setVisible(false);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return ais.movementType == 2;
    }

    @Override
    public boolean isPushedByWater() {
        return ais.movementType != 2;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        abilities = new DataAbilities(this);
        display = new DataDisplay(this);
        stats = new DataStats(this);
        ais = new DataAI(this);
        advanced = new DataAdvanced(this);
        inventory = new DataInventory(this);
        transform = new DataTransform(this);
        script = new DataScript(this);
        timers = new DataTimers(this);

        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(stats.maxHealth);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(CustomNpcs.NpcNavRange);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getSpeed());
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(stats.melee.getStrength());
        this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(this.getSpeed() * 10);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(RoleData, "");
        this.dataManager.register(JobData, "");
        this.dataManager.register(FactionData, 0);
        this.dataManager.register(Animation, Integer.valueOf(0));

        this.dataManager.register(Walking, false);
        this.dataManager.register(Interacting, false);
        this.dataManager.register(Killed, false);
        this.dataManager.register(Attacking, false);
    }

    @Override
    public boolean isEntityAlive() {
        return super.isEntityAlive() && !isKilled();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted % 10 == 0) {
            this.startYPos = calculateStartYPos(ais.startPos()) + 1;
            if (startYPos < 0 && !isRemote())
                setDead();
            EventHooks.onNPCTick(this);
        }
        timers.update();
    }

    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        //float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        float f = stats.melee.getStrength();
        if (stats.melee.getDelay() < 10) {
            par1Entity.hurtResistantTime = 0;
        }
        if (par1Entity instanceof EntityLivingBase) {
            NpcEvent.MeleeAttackEvent event = new NpcEvent.MeleeAttackEvent(wrappedNPC, (EntityLivingBase) par1Entity, f);
            if (EventHooks.onNPCAttacksMelee(this, event))
                return false;
            f = event.damage;
        }

        boolean var4 = par1Entity.attackEntityFrom(new NpcDamageSource("mob", this), f);

        if (var4) {
            if (getOwner() instanceof EntityPlayer)
                EntityUtil.setRecentlyHit((EntityLivingBase) par1Entity);
            if (stats.melee.getKnockback() > 0) {
                par1Entity.addVelocity((-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * stats.melee.getKnockback() * 0.5F), 0.1D, (MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * stats.melee.getKnockback() * 0.5F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }
            if (advanced.role == RoleType.COMPANION) {
                ((RoleCompanion) roleInterface).attackedEntity(par1Entity);
            }
        }

        if (stats.melee.getEffectType() != PotionEffectType.NONE) {
            if (stats.melee.getEffectType() != PotionEffectType.FIRE)
                ((EntityLivingBase) par1Entity).addPotionEffect(new PotionEffect(PotionEffectType.getMCType(stats.melee.getEffectType()), stats.melee.getEffectTime() * 20, stats.melee.getEffectStrength()));
            else
                par1Entity.setFire(stats.melee.getEffectTime());
        }
        return var4;
    }

    @Override
    public void onLivingUpdate() {
        if (CustomNpcs.FreezeNPCs)
            return;
        if (this.isAIDisabled()) {
            super.onLivingUpdate();
            return;
        }
        totalTicksAlive++;
        this.updateArmSwingProgress();
        if (this.ticksExisted % 20 == 0)
            faction = getFaction();
        if (!world.isRemote) {
            if (!isKilled() && this.ticksExisted % 20 == 0) {
                advanced.scenes.update();
                if (this.getHealth() < this.getMaxHealth()) {
                    if (stats.healthRegen > 0 && !isAttacking())
                        heal(stats.healthRegen);
                    if (stats.combatRegen > 0 && isAttacking())
                        heal(stats.combatRegen);
                }
                if (faction.getsAttacked && !isAttacking()) {
                    List<EntityMob> list = this.world.getEntitiesWithinAABB(EntityMob.class, this.getEntityBoundingBox().grow(16, 16, 16));
                    for (EntityMob mob : list) {
                        if (mob.getAttackTarget() == null && this.canSee(mob)) {
                            mob.setAttackTarget(this);
                        }
                    }
                }
                if (linkedData != null && linkedData.time > linkedLast) {
                    LinkedNpcController.Instance.loadNpcData(this);
                }
                if (updateClient) {
                    updateClient();
                }
                if (updateAI) {
                    updateTasks();
                    updateAI = false;
                }
            }
            if (getHealth() <= 0 && !isKilled()) {
                clearActivePotions();
                dataManager.set(Killed, true);
                updateTasks();
            }
            if (display.getBossbar() == 2)
                bossInfo.setVisible(this.getAttackTarget() != null);
            dataManager.set(Walking, !getNavigator().noPath());
            dataManager.set(Interacting, isInteracting());

            combatHandler.update();
            onCollide();
        }

        if (wasKilled != isKilled() && wasKilled) {
            reset();
        }

        wasKilled = isKilled();

        if (this.world.isDaytime() && !this.world.isRemote && this.stats.burnInSun) {
            float f = this.getBrightness();

            if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.canBlockSeeSky(new BlockPos(this))) {
                this.setFire(8);
            }
        }

        super.onLivingUpdate();

        if (world.isRemote) {
            if (roleInterface != null) {
                roleInterface.clientUpdate();
            }

            if (textureCloakLocation != null)
                cloakUpdate();
            if (currentAnimation != dataManager.get(Animation)) {
                currentAnimation = dataManager.get(Animation);
                animationStart = this.ticksExisted;
                updateHitbox();
            }
            if (advanced.job == JobType.BARD)
                ((JobBard) jobInterface).onLivingUpdate();
        }

        if (display.getBossbar() > 0)
            this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    public void updateClient() {
        NBTTagCompound compound = writeSpawnData();
        compound.setInteger("EntityId", getEntityId());
        Server.sendAssociatedData(this, EnumPacketClient.UPDATE_NPC, compound);
        updateClient = false;
    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        super.damageEntity(damageSrc, damageAmount);
        combatHandler.damage(damageSrc, damageAmount);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (world.isRemote)
            return !isAttacking();
        if (hand != EnumHand.MAIN_HAND)
            return true;
        ItemStack stack = player.getHeldItem(hand);
        if (stack != null) {
            Item item = stack.getItem();
            if (item == CustomItems.cloner || item == CustomItems.wand || item == CustomItems.mount || item == CustomItems.scripter) {
                setAttackTarget(null);
                setRevengeTarget(null);
                return true;
            }
            if (item == CustomItems.moving) {
                setAttackTarget(null);
                stack.setTagInfo("NPCID", new NBTTagInt(getEntityId()));
                player.sendMessage(new TextComponentTranslation("Registered " + this.getName() + " to your NPC Pather"));
                return true;
            }
        }
        if (EventHooks.onNPCInteract(this, player))
            return false;

        if (getFaction().isAggressiveToPlayer(player))
            return !isAttacking();

        addInteract(player);

        Dialog dialog = getDialog(player);
        QuestData data = PlayerData.get(player).questData.getQuestCompletion(player, this);
        if (data != null) {
            Server.sendData((EntityPlayerMP) player, EnumPacketClient.QUEST_COMPLETION, data.quest.id);
        } else if (dialog != null) {
            NoppesUtilServer.openDialog(player, this, dialog);
        } else if (roleInterface != null)
            roleInterface.interact(player);
        else
            say(player, advanced.getInteractLine());

        return true;
    }

    public void addInteract(EntityLivingBase entity) {
        if (!ais.stopAndInteract || isAttacking() || !entity.isEntityAlive() || this.isAIDisabled())
            return;
        if ((ticksExisted - lastInteract) < 180)
            interactingEntities.clear();
        getNavigator().clearPath();
        lastInteract = ticksExisted;
        if (!interactingEntities.contains(entity))
            interactingEntities.add(entity);
    }

    public boolean isInteracting() {
        if ((ticksExisted - lastInteract) < 40 || isRemote() && dataManager.get(Interacting))
            return true;
        return ais.stopAndInteract && !interactingEntities.isEmpty() && (ticksExisted - lastInteract) < 180;
    }

    private Dialog getDialog(EntityPlayer player) {
        for (DialogOption option : dialogs.values()) {
            if (option == null)
                continue;
            if (!option.hasDialog())
                continue;
            Dialog dialog = option.getDialog();
            if (dialog.availability.isAvailable(player)) {
                return dialog;
            }
        }
        return null;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i) {
        if (this.world.isRemote || CustomNpcs.FreezeNPCs || damagesource.damageType.equals("inWall")) {
            return false;
        }
        if (damagesource.damageType.equals("outOfWorld") && isKilled()) {
            reset();
        }
        i = stats.resistances.applyResistance(damagesource, i);

        if ((float) this.hurtResistantTime > (float) this.maxHurtResistantTime / 2.0F && i <= this.lastDamage)
            return false;

        Entity entity = NoppesUtilServer.GetDamageSourcee(damagesource);
        EntityLivingBase attackingEntity = null;

        if (entity instanceof EntityLivingBase)
            attackingEntity = (EntityLivingBase) entity;

        if (attackingEntity != null && attackingEntity == getOwner())
            return false;
        else if (attackingEntity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface) attackingEntity;
            if (npc.faction.id == faction.id)
                return false;
            if (npc.getOwner() instanceof EntityPlayer)
                this.recentlyHit = 100;
        } else if (attackingEntity instanceof EntityPlayer && faction.isFriendlyToPlayer((EntityPlayer) attackingEntity)) {
            net.minecraftforge.common.ForgeHooks.onLivingAttack(this, damagesource, i);
            return false;
        }

        NpcEvent.DamagedEvent event = new NpcEvent.DamagedEvent(wrappedNPC, entity, i, damagesource);
        if (EventHooks.onNPCDamaged(this, event)) {
            net.minecraftforge.common.ForgeHooks.onLivingAttack(this, damagesource, i);
            return false;
        }
        i = event.damage;

        if (isKilled())
            return false;

        if (attackingEntity == null)
            return super.attackEntityFrom(damagesource, i);

        try {
            if (isAttacking()) {
                if (getAttackTarget() != null && attackingEntity != null && this.getDistanceSq(getAttackTarget()) > this.getDistanceSq(attackingEntity)) {
                    setAttackTarget(attackingEntity);
                }
                return super.attackEntityFrom(damagesource, i);
            }

            if (i > 0) {
                List<EntityNPCInterface> inRange = world.getEntitiesWithinAABB(EntityNPCInterface.class, this.getEntityBoundingBox().grow(32D, 16D, 32D));
                for (EntityNPCInterface npc : inRange) {
                    if (npc.isKilled() || !npc.advanced.defendFaction || npc.faction.id != faction.id)
                        continue;

                    if (npc.canSee(this) || npc.ais.directLOS || npc.canSee(attackingEntity))
                        npc.onAttack(attackingEntity);
                }
                setAttackTarget(attackingEntity);
            }
            return super.attackEntityFrom(damagesource, i);
        } finally {
            if (event.clearTarget) {
                setAttackTarget(null);
                setRevengeTarget(null);
            }
        }
    }

    public void onAttack(EntityLivingBase entity) {
        if (entity == null || entity == this || isAttacking() || ais.onAttack == 3 || entity == getOwner())
            return;
        super.setAttackTarget(entity);
    }

    @Override
    public void setAttackTarget(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.disableDamage ||
                entity != null && entity == getOwner() || getAttackTarget() == entity)
            return;
        if (entity != null) {
            NpcEvent.TargetEvent event = new NpcEvent.TargetEvent(wrappedNPC, entity);
            if (EventHooks.onNPCTarget(this, event))
                return;
            if (event.entity == null)
                entity = null;
            else
                entity = event.entity.getMCEntity();
        } else {
            for (EntityAITaskEntry en : targetTasks.taskEntries) {
                if (en.using) {
                    en.using = false;
                    en.action.resetTask();
                }
            }
            if (EventHooks.onNPCTargetLost(this, getAttackTarget()))
                return;
        }

        if (entity != null && entity != this && ais.onAttack != 3 && !isAttacking() && !isRemote()) {
            Line line = advanced.getAttackLine();
            if (line != null)
                saySurrounding(line.formatTarget(entity));
        }

        super.setAttackTarget(entity);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase entity, float f) {
        final ItemStack proj = ItemStackWrapper.MCItem(inventory.getProjectile());
        if (proj == null) {
            updateAI = true;
            return;
        }

        NpcEvent.RangedLaunchedEvent event = new NpcEvent.RangedLaunchedEvent(wrappedNPC, entity, stats.ranged.getStrength());
        if (EventHooks.onNPCRangedLaunched(this, event))
            return;

        for (int i = 0; i < this.stats.ranged.getShotCount(); i++) {
            EntityProjectile projectile = shoot(entity, stats.ranged.getAccuracy(), proj, f == 1);
            projectile.damage = event.damage;
            projectile.callback = (projectile1, pos, entity1) -> {
                if (proj.getItem() == CustomItems.soulstoneFull) {
                    Entity e = ItemSoulstoneFilled.Spawn(null, proj, EntityNPCInterface.this.world, pos);
                    if (e instanceof EntityLivingBase && entity1 instanceof EntityLivingBase) {
                        if (e instanceof EntityLiving)
                            ((EntityLiving) e).setAttackTarget((EntityLivingBase) entity1);
                        else
                            ((EntityLivingBase) e).setRevengeTarget((EntityLivingBase) entity1);
                    }
                }
                projectile1.playSound(stats.ranged.getSoundEvent(entity1 != null ? 1 : 2), 1.0F, 1.2F / (getRNG().nextFloat() * 0.2F + 0.9F));
                return false;
            };
        }
        this.playSound(this.stats.ranged.getSoundEvent(0), 2.0F, 1.0f);

    }

    public EntityProjectile shoot(EntityLivingBase entity, int accuracy, ItemStack proj, boolean indirect) {
        return shoot(entity.posX, entity.getEntityBoundingBox().minY + (double) (entity.height / 2.0F), entity.posZ, accuracy, proj, indirect);
    }

    public EntityProjectile shoot(double x, double y, double z, int accuracy, ItemStack proj, boolean indirect) {
        EntityProjectile projectile = new EntityProjectile(this.world, this, proj.copy(), true);
        double varX = x - this.posX;
        double varY = y - (this.posY + this.getEyeHeight());
        double varZ = z - this.posZ;
        float varF = projectile.hasGravity() ? MathHelper.sqrt(varX * varX + varZ * varZ) : 0.0F;
        float angle = projectile.getAngleForXYZ(varX, varY, varZ, varF, indirect);
        float acc = 20.0F - MathHelper.floor(accuracy / 5.0F);
        projectile.shoot(varX, varY, varZ, angle, acc);
        world.spawnEntity(projectile);
        return projectile;
    }

    private void clearTasks(EntityAITasks tasks) {
        Iterator iterator = tasks.taskEntries.iterator();
        List<EntityAITaskEntry> list = new ArrayList(tasks.taskEntries);
        for (EntityAITaskEntry entityaitaskentry : list) {
            tasks.removeTask(entityaitaskentry.action);
        }
        tasks.taskEntries.clear();
    }

    private void updateTasks() {
        if (world == null || world.isRemote)
            return;

        clearTasks(tasks);
        clearTasks(targetTasks);
        if (isKilled())
            return;

        Predicate attackEntitySelector = new NPCAttackSelector(this);
        this.targetTasks.addTask(0, new EntityAIClearTarget(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAIClosestTarget(this, EntityLivingBase.class, 4, this.ais.directLOS, false, attackEntitySelector));
        this.targetTasks.addTask(3, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(4, new EntityAIOwnerHurtTarget(this));

        world.pathListener.onEntityRemoved(this);
        if (ais.movementType == 1) {
            this.moveHelper = new EntityFlyHelper(this);
            this.navigator = new PathNavigateFlying(this, world);
        } else if (ais.movementType == 2) {
            this.moveHelper = new EntityFlyHelper(this);
            this.navigator = new PathNavigateSwimmer(this, world);
        } else {
            this.moveHelper = new EntityMoveHelper(this);
            this.navigator = new PathNavigateGround(this, world);
            this.tasks.addTask(0, new EntityAIWaterNav(this));
        }
        world.pathListener.onEntityAdded(this);

        this.taskCount = 1;
        this.addRegularEntries();
        this.doorInteractType();
        this.seekShelter();
        this.setResponse();
        this.setMoveType();
    }

    private void setResponse() {
        aiAttackTarget = aiRange = null;
        if (this.ais.canSprint)
            this.tasks.addTask(this.taskCount++, new EntityAISprintToTarget(this));

        if (this.ais.onAttack == 1) {
            this.tasks.addTask(this.taskCount++, new EntityAIPanic(this, 1.2F));
        } else if (this.ais.onAttack == 2) {
            this.tasks.addTask(this.taskCount++, new EntityAIAvoidTarget(this));
        } else if (this.ais.onAttack == 0) {
            if (this.ais.canLeap)
                this.tasks.addTask(this.taskCount++, new EntityAIPounceTarget(this));

            if (this.inventory.getProjectile() == null) {
                switch (this.ais.tacticalVariant) {
                    case TacticalType.DODGE:
                        this.tasks.addTask(this.taskCount++, new EntityAIZigZagTarget(this, 1.3D));
                        break;
                    case TacticalType.SURROUND:
                        this.tasks.addTask(this.taskCount++, new EntityAIOrbitTarget(this, 1.3D, true));
                        break;
                    case TacticalType.HITNRUN:
                        this.tasks.addTask(this.taskCount++, new EntityAIAvoidTarget(this));
                        break;
                    case TacticalType.AMBUSH:
                        this.tasks.addTask(this.taskCount++, new EntityAIAmbushTarget(this, 1.2D));
                        break;
                    case TacticalType.STALK:
                        this.tasks.addTask(this.taskCount++, new EntityAIStalkTarget(this));
                        break;
                    default:
                }
            } else {
                switch (this.ais.tacticalVariant) {
                    case TacticalType.DODGE:
                        this.tasks.addTask(this.taskCount++, new EntityAIDodgeShoot(this));
                        break;
                    case TacticalType.SURROUND:
                        this.tasks.addTask(this.taskCount++, new EntityAIOrbitTarget(this, 1.3D, false));
                        break;
                    case TacticalType.HITNRUN:
                        this.tasks.addTask(this.taskCount++, new EntityAIAvoidTarget(this));
                        break;
                    case TacticalType.AMBUSH:
                        this.tasks.addTask(this.taskCount++, new EntityAIAmbushTarget(this, 1.3D));
                        break;
                    case TacticalType.STALK:
                        this.tasks.addTask(this.taskCount++, new EntityAIStalkTarget(this));
                        break;
                    default:
                }
            }
            this.tasks.addTask(this.taskCount, aiAttackTarget = new EntityAIAttackTarget(this));
            ((EntityAIAttackTarget) aiAttackTarget).navOverride(ais.tacticalVariant == TacticalType.NONE);

            if (this.inventory.getProjectile() != null) {
                this.tasks.addTask(this.taskCount++, aiRange = new EntityAIRangedAttack(this));
                aiRange.navOverride(ais.tacticalVariant == TacticalType.NONE);
            }
        } else if (this.ais.onAttack == 3) {
            //do nothing
        }
    }

    public boolean canFly() {
        return false;
    }

    /*
     * Branch task function for setting if an NPC wanders or not
     */
    public void setMoveType() {
        if (ais.getMovingType() == 1) {
            this.tasks.addTask(this.taskCount++, new EntityAIWander(this));
        }
        if (ais.getMovingType() == 2) {
            this.tasks.addTask(this.taskCount++, new EntityAIMovingPath(this));
        }
    }

    public void doorInteractType() {
        if (canFly()) //currently flying does not support opening doors
            return;
        EntityAIBase aiDoor = null;
        if (this.ais.doorInteract == 1) {
            this.tasks.addTask(this.taskCount++, aiDoor = new EntityAIOpenDoor(this, true));
        } else if (this.ais.doorInteract == 0) {
            this.tasks.addTask(this.taskCount++, aiDoor = new EntityAIBustDoor(this));
        }
        if (getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround) getNavigator()).setBreakDoors(aiDoor != null);
        }
    }

    /*
     * Branch task function for finding shelter under the appropriate conditions
     */
    public void seekShelter() {
        if (this.ais.findShelter == 0) {
            this.tasks.addTask(this.taskCount++, new EntityAIMoveIndoors(this));
        } else if (this.ais.findShelter == 1) {
            if (!canFly())//doesnt work when flying
                this.tasks.addTask(this.taskCount++, new EntityAIRestrictSun(this));
            this.tasks.addTask(this.taskCount++, new EntityAIFindShade(this));
        }
    }

    /*
     * Add immutable task entries.
     */
    public void addRegularEntries() {
        this.tasks.addTask(this.taskCount++, new EntityAIReturn(this));
        this.tasks.addTask(this.taskCount++, new EntityAIFollow(this));
        if (this.ais.getStandingType() != 1 && this.ais.getStandingType() != 3)
            this.tasks.addTask(this.taskCount++, new EntityAIWatchClosest(this, EntityLivingBase.class, 5.0F));
        this.tasks.addTask(this.taskCount++, lookAi = new EntityAILook(this));
        this.tasks.addTask(this.taskCount++, new EntityAIWorldLines(this));
        this.tasks.addTask(this.taskCount++, new EntityAIJob(this));
        this.tasks.addTask(this.taskCount++, new EntityAIRole(this));
        this.tasks.addTask(this.taskCount++, animateAi = new EntityAIAnimation(this));
        if (transform.isValid())
            this.tasks.addTask(this.taskCount++, new EntityAITransform(this));
    }

    public float getSpeed() {
        return (float) ais.getWalkingSpeed() / 20.0F;
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        if (ais.movementType == 2) {
            return this.world.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F : 0;
        }
        float weight = this.world.getLightBrightness(pos) - 0.5F;
        if (world.getBlockState(pos).isOpaqueCube())
            weight += 10;
        return weight;
    }

    @Override
    protected int decreaseAirSupply(int par1) {
        if (!this.stats.canDrown)
            return par1;
        return super.decreaseAirSupply(par1);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return this.stats == null ? null : this.stats.creatureType;
    }

    @Override
    public int getTalkInterval() {
        return 160;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (!this.isEntityAlive())
            return null;
        return this.advanced.getSoundEvent(this.getAttackTarget() != null ? 1 : 0);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return this.advanced.getSoundEvent(2);
    }

    @Override
    public SoundEvent getDeathSound() {
        return this.advanced.getSoundEvent(3);
    }

    @Override
    protected float getSoundPitch() {
        if (this.advanced.disablePitch)
            return 1;
        return super.getSoundPitch();
    }

    @Override
    protected void playStepSound(BlockPos pos, Block block) {
        SoundEvent sound = this.advanced.getSoundEvent(4);
        if (sound != null)
            this.playSound(sound, 0.15F, 1.0F);
        else
            super.playStepSound(pos, block);

    }

    public EntityPlayerMP getFakeChatPlayer() {
        if (world.isRemote)
            return null;
        EntityUtil.Copy(this, ChatEventPlayer);
        ChatEventProfile.npc = this;
        ChatEventPlayer.refreshDisplayName();
        ChatEventPlayer.setWorld(world);
        ChatEventPlayer.setPosition(posX, posY, posZ);
        return ChatEventPlayer;
    }

    public void saySurrounding(Line line) {
        if (line == null || line.text == null)
            return;
        if (!line.hideText && !line.text.isEmpty()) {
            ServerChatEvent event = new ServerChatEvent(getFakeChatPlayer(), line.text, new TextComponentTranslation(line.text.replace("%", "%%")));
            if (MinecraftForge.EVENT_BUS.post(event) || event.getComponent() == null) {
                return;
            }
            line.text = event.getComponent().getUnformattedText().replace("%%", "%");
        }

        List<EntityPlayer> inRange = world.getEntitiesWithinAABB(
                EntityPlayer.class, this.getEntityBoundingBox().grow(20D, 20D, 20D));
        for (EntityPlayer player : inRange)
            say(player, line);
    }

    public void say(EntityPlayer player, Line line) {
        if (line == null || !this.canSee(player))
            return;

        if (!line.sound.isEmpty()) {
            Server.sendData((EntityPlayerMP) player, EnumPacketClient.PLAY_SOUND, line.sound, (float) posX, (float) posY, (float) posZ);
        }
        if (line.text != null && !line.text.isEmpty()) {
            Server.sendData((EntityPlayerMP) player, EnumPacketClient.CHATBUBBLE, this.getEntityId(), line.text, !line.hideText);
        }
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    public void addVelocity(double d, double d1, double d2) {
        if (isWalking() && !isKilled())
            super.addVelocity(d, d1, d2);
    }


    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        npcVersion = compound.getInteger("ModRev");
        VersionCompatibility.CheckNpcCompatibility(this, compound);

        display.readToNBT(compound);
        stats.readToNBT(compound);
        ais.readToNBT(compound);
        script.readFromNBT(compound);
        timers.readFromNBT(compound);

        advanced.readToNBT(compound);
        if (advanced.role != RoleType.NONE && roleInterface != null)
            roleInterface.readFromNBT(compound);
        if (advanced.job != JobType.NONE && jobInterface != null)
            jobInterface.readFromNBT(compound);

        inventory.readEntityFromNBT(compound);
        transform.readToNBT(compound);

        killedtime = compound.getLong("KilledTime");
        totalTicksAlive = compound.getLong("TotalTicksAlive");

        linkedName = compound.getString("LinkedNpcName");
        if (!isRemote())
            LinkedNpcController.Instance.loadNpcData(this);

        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(CustomNpcs.NpcNavRange);

        updateAI = true;
    }


    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        display.writeToNBT(compound);
        stats.writeToNBT(compound);
        ais.writeToNBT(compound);
        script.writeToNBT(compound);
        timers.writeToNBT(compound);

        advanced.writeToNBT(compound);
        if (advanced.role != RoleType.NONE && roleInterface != null)
            roleInterface.writeToNBT(compound);
        if (advanced.job != JobType.NONE && jobInterface != null)
            jobInterface.writeToNBT(compound);

        inventory.writeEntityToNBT(compound);
        transform.writeToNBT(compound);

        compound.setLong("KilledTime", killedtime);
        compound.setLong("TotalTicksAlive", totalTicksAlive);
        compound.setInteger("ModRev", npcVersion);
        compound.setString("LinkedNpcName", linkedName);
    }

    public void updateHitbox() {
        if (currentAnimation == AnimationType.SLEEP || currentAnimation == AnimationType.CRAWL) {
            width = 0.8f;
            height = 0.4f;
        } else if (isRiding()) {
            width = 0.6f;
            height = baseHeight * 0.77f;
        } else {
            width = 0.6f;
            height = baseHeight;
        }
        width = (width / 5f) * display.getSize();
        height = (height / 5f) * display.getSize();

        if (!display.getHasHitbox() || isKilled() && stats.hideKilledBody) {
            width = 0.0f;
        }

        this.setPosition(posX, posY, posZ);
    }

    @Override
    public void onDeathUpdate() {
        if (stats.spawnCycle == 3 || stats.spawnCycle == 4) {
            super.onDeathUpdate();
            return;
        }

        ++this.deathTime;
        if (world.isRemote)
            return;
        if (!hasDied) {
            setDead();
        }
        if (killedtime < System.currentTimeMillis()) {
            if (stats.spawnCycle == 0 || (this.world.isDaytime() && stats.spawnCycle == 1) || (!this.world.isDaytime() && stats.spawnCycle == 2)) {
                reset();
            }
        }
    }

    public void reset() {
        hasDied = false;
        isDead = false;
        wasKilled = false;
        setSprinting(false);
        setHealth(getMaxHealth());
        dataManager.set(Animation, 0);
        dataManager.set(Walking, false);
        dataManager.set(Killed, false);
        dataManager.set(Interacting, false);
        interactingEntities.clear();

        combatHandler.reset();

        this.setAttackTarget(null);
        this.setRevengeTarget(null);
        this.deathTime = 0;
        //fleeingTick = 0;
        if (ais.returnToStart && !hasOwner() && !isRemote())
            setLocationAndAngles(getStartXPos(), getStartYPos(), getStartZPos(), rotationYaw, rotationPitch);
        killedtime = 0;
        extinguish();
        this.clearActivePotions();
        travel(0, 0, 0);
        distanceWalkedModified = 0;
        getNavigator().clearPath();
        currentAnimation = AnimationType.NORMAL;
        updateHitbox();
        updateAI = true;
        ais.movingPos = 0;
        if (getOwner() != null) {
            getOwner().setLastAttackedEntity(null);
        }
        bossInfo.setVisible(display.getBossbar() == 1);

        if (jobInterface != null)
            jobInterface.reset();

        EventHooks.onNPCInit(this);
    }

    public void onCollide() {
        if (!isEntityAlive() || ticksExisted % 4 != 0 || world.isRemote)
            return;

        AxisAlignedBB axisalignedbb = null;

        if (this.getRidingEntity() != null && this.getRidingEntity().isEntityAlive()) {
            axisalignedbb = this.getEntityBoundingBox().union(this.getRidingEntity().getEntityBoundingBox()).grow(1.0D, 0.0D, 1.0D);
        } else {
            axisalignedbb = this.getEntityBoundingBox().grow(1.0D, 0.5D, 1.0D);
        }

        List list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
        if (list == null)
            return;

        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity) list.get(i);
            if (entity != this && entity.isEntityAlive())
                EventHooks.onNPCCollide(this, entity);
        }

    }

    @Override
    public void setPortal(BlockPos pos) {
        //prevent npcs from walking into portals
    }

    public double field_20066_r;
    public double field_20065_s;
    public double field_20064_t;
    public double field_20063_u;
    public double field_20062_v;
    public double field_20061_w;

    public void cloakUpdate() {
        field_20066_r = field_20063_u;
        field_20065_s = field_20062_v;
        field_20064_t = field_20061_w;
        double d = posX - field_20063_u;
        double d1 = posY - field_20062_v;
        double d2 = posZ - field_20061_w;
        double d3 = 10D;
        if (d > d3) {
            field_20066_r = field_20063_u = posX;
        }
        if (d2 > d3) {
            field_20064_t = field_20061_w = posZ;
        }
        if (d1 > d3) {
            field_20065_s = field_20062_v = posY;
        }
        if (d < -d3) {
            field_20066_r = field_20063_u = posX;
        }
        if (d2 < -d3) {
            field_20064_t = field_20061_w = posZ;
        }
        if (d1 < -d3) {
            field_20065_s = field_20062_v = posY;
        }
        field_20063_u += d * 0.25D;
        field_20061_w += d2 * 0.25D;
        field_20062_v += d1 * 0.25D;
    }

    @Override
    protected boolean canDespawn() {
        return stats.spawnCycle == 4;
    }

    @Override
    public ItemStack getHeldItemMainhand() {
        IItemStack item = null;
        if (isAttacking())
            item = inventory.getRightHand();
        else if (advanced.role == RoleType.COMPANION)
            item = ((RoleCompanion) roleInterface).getHeldItem();
        else if (jobInterface != null && jobInterface.overrideMainHand)
            item = jobInterface.getMainhand();
        else
            item = inventory.getRightHand();

        return ItemStackWrapper.MCItem(item);
    }

    @Override
    public ItemStack getHeldItemOffhand() {
        IItemStack item = null;
        if (isAttacking())
            item = inventory.getLeftHand();
        else if (jobInterface != null && jobInterface.overrideOffHand)
            item = jobInterface.getOffhand();
        else
            item = inventory.getLeftHand();
        return ItemStackWrapper.MCItem(item);
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slot) {
        if (slot == EntityEquipmentSlot.MAINHAND)
            return getHeldItemMainhand();
        if (slot == EntityEquipmentSlot.OFFHAND)
            return getHeldItemOffhand();
        return ItemStackWrapper.MCItem(inventory.getArmor(3 - slot.getIndex()));
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slot, ItemStack item) {
        if (slot == EntityEquipmentSlot.MAINHAND)
            inventory.weapons.put(0, NpcAPI.Instance().getIItemStack(item));
        else if (slot == EntityEquipmentSlot.OFFHAND)
            inventory.weapons.put(2, NpcAPI.Instance().getIItemStack(item));
        else {
            inventory.armor.put(3 - slot.getIndex(), NpcAPI.Instance().getIItemStack(item));
        }
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(ItemStackWrapper.MCItem(inventory.armor.get(3 - i)));
        }
        return list;
    }

    @Override
    public Iterable<ItemStack> getHeldEquipment() {
        ArrayList list = new ArrayList();
        list.add(ItemStackWrapper.MCItem(inventory.weapons.get(0)));
        list.add(ItemStackWrapper.MCItem(inventory.weapons.get(2)));
        return list;
    }

    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
    }

    @Override
    public void onDeath(DamageSource damagesource) {
        setSprinting(false);
        getNavigator().clearPath();
        extinguish();
        clearActivePotions();

        if (!isRemote()) {
            Entity attackingEntity = NoppesUtilServer.GetDamageSourcee(damagesource);
            EventHooks.onNPCDied(this, attackingEntity, damagesource);

            bossInfo.setVisible(false);
            inventory.dropStuff(attackingEntity, damagesource);
            Line line = advanced.getKilledLine();
            if (line != null)
                saySurrounding(line.formatTarget(attackingEntity instanceof EntityLivingBase ? (EntityLivingBase) attackingEntity : null));
        }
        super.onDeath(damagesource);
    }

    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void setDead() {
        hasDied = true;

        removePassengers();
        dismountRidingEntity();

        if (world.isRemote || stats.spawnCycle == 3 || stats.spawnCycle == 4) {
            //this.spawnExplosionParticle();
            delete();
        } else {
            setHealth(-1);
            setSprinting(false);
            getNavigator().clearPath();

            setCurrentAnimation(AnimationType.SLEEP);
            updateHitbox();

            if (killedtime <= 0)
                killedtime = stats.respawnTime * 1000 + System.currentTimeMillis();

            if (advanced.role != RoleType.NONE && roleInterface != null)
                roleInterface.killed();
            if (advanced.job != JobType.NONE && jobInterface != null)
                jobInterface.killed();
        }
    }

    public void delete() {
        if (advanced.role != RoleType.NONE && roleInterface != null)
            roleInterface.delete();
        if (advanced.job != JobType.NONE && jobInterface != null)
            jobInterface.delete();
        super.setDead();
    }

    public float getStartXPos() {
        return ais.startPos().getX() + ais.bodyOffsetX / 10;
    }

    public float getStartZPos() {
        return ais.startPos().getZ() + ais.bodyOffsetZ / 10;
    }

    public boolean isVeryNearAssignedPlace() {
        double xx = posX - getStartXPos();
        double zz = posZ - getStartZPos();
        if (xx < -0.2 || xx > 0.2)
            return false;
        return !(zz < -0.2) && !(zz > 0.2);
    }

    //	@Override
//	public IIcon getItemIcon(ItemStack par1ItemStack, int par2){
//        if (par1ItemStack.getItem() == Items.bow){
//            return Items.bow.getIcon(par1ItemStack, par2);
//        }
//		EntityPlayer player = CustomNpcs.proxy.getPlayer();
//		if(player == null)
//			return super.getItemIcon(par1ItemStack, par2);
//		return player.getItemIcon(par1ItemStack, par2);
//    }
    private double startYPos = -1;

    public double getStartYPos() {
        if (startYPos < 0)
            return calculateStartYPos(ais.startPos());
        return startYPos;
    }

    private double calculateStartYPos(BlockPos pos) {
        BlockPos startPos = ais.startPos();
        while (pos.getY() > 0) {
            IBlockState state = world.getBlockState(pos);
            AxisAlignedBB bb = state.getBoundingBox(world, pos).offset(pos);
            if (bb != null) {
                if (ais.movementType == 2 && startPos.getY() <= pos.getY() && state.getMaterial() == Material.WATER) {
                    pos = pos.down();
                    continue;
                }
                return bb.maxY;
            }
            pos = pos.down();
        }
        return 0;
    }

    private BlockPos calculateTopPos(BlockPos pos) {
        BlockPos check = pos;
        while (check.getY() > 0) {
            IBlockState state = world.getBlockState(pos);
            AxisAlignedBB bb = state.getBoundingBox(world, pos).offset(pos);
            if (bb != null) {
                return check;
            }
            check = check.down();
        }
        return pos;
    }

    public boolean isInRange(Entity entity, double range) {
        return this.isInRange(entity.posX, entity.posY, entity.posZ, range);
    }

    public boolean isInRange(double posX, double posY, double posZ, double range) {
        double y = Math.abs(this.posY - posY);
        if (posY >= 0 && y > range)
            return false;

        double x = Math.abs(this.posX - posX);
        double z = Math.abs(this.posZ - posZ);

        return x <= range && z <= range;
    }

    public void givePlayerItem(EntityPlayer player, ItemStack item) {
        if (world.isRemote) {
            return;
        }
        item = item.copy();
        float f = 0.7F;
        double d = (double) (world.rand.nextFloat() * f)
                + (double) (1.0F - f);
        double d1 = (double) (world.rand.nextFloat() * f)
                + (double) (1.0F - f);
        double d2 = (double) (world.rand.nextFloat() * f)
                + (double) (1.0F - f);
        EntityItem entityitem = new EntityItem(world, posX + d, posY + d1,
                posZ + d2, item);
        entityitem.setPickupDelay(2);
        world.spawnEntity(entityitem);

        int i = item.getCount();

        if (player.inventory.addItemStackToInventory(item)) {
            this.world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.onItemPickup(entityitem, i);

            if (item.getCount() <= 0) {
                entityitem.setDead();
            }
        }
    }

    @Override
    public boolean isPlayerSleeping() {
        return currentAnimation == AnimationType.SLEEP && !isAttacking();
    }
//
//	@Override
//	public boolean isRiding() {
//		return currentAnimation == AnimationType.SITTING && !isAttacking() || getRidingEntity() != null;
//	}

    public boolean isWalking() {
        return ais.getMovingType() != 0 || isAttacking() || isFollower() || dataManager.get(Walking);
    }

    @Override
    public boolean isSneaking() {
        return currentAnimation == AnimationType.SNEAK;
    }

    @Override
    public void knockBack(Entity par1Entity, float strength, double ratioX, double ratioZ) {
        super.knockBack(par1Entity, strength * (2 - stats.resistances.knockback), ratioX, ratioZ);
    }

    public Faction getFaction() {
        Faction fac = FactionController.instance.getFaction(dataManager.get(FactionData));
        if (fac == null) {
            return FactionController.instance.getFaction(FactionController.instance.getFirstFactionId());
        }
        return fac;
    }

    public boolean isRemote() {
        return world == null || world.isRemote;
    }

    public void setFaction(int id) {
        if (id < 0 || isRemote())
            return;
        dataManager.set(FactionData, id);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        if (stats.potionImmune)
            return false;
        if (getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD && effect.getPotion() == MobEffects.POISON)
            return false;
        return super.isPotionApplicable(effect);
    }

    public boolean isAttacking() {
        return dataManager.get(Attacking);
    }

    public boolean isKilled() {
        return isDead || dataManager.get(Killed);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        try {
            Server.writeNBT(buffer, writeSpawnData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NBTTagCompound writeSpawnData() {
        NBTTagCompound compound = new NBTTagCompound();
        display.writeToNBT(compound);
        compound.setInteger("MaxHealth", stats.maxHealth);
        compound.setTag("Armor", NBTTags.nbtIItemStackMap(inventory.armor));
        compound.setTag("Weapons", NBTTags.nbtIItemStackMap(inventory.weapons));
        compound.setInteger("Speed", ais.getWalkingSpeed());
        compound.setBoolean("DeadBody", stats.hideKilledBody);
        compound.setInteger("StandingState", ais.getStandingType());
        compound.setInteger("MovingState", ais.getMovingType());
        compound.setInteger("Orientation", ais.orientation);
        compound.setInteger("Role", advanced.role);
        compound.setInteger("Job", advanced.job);
        if (advanced.job == JobType.BARD) {
            NBTTagCompound bard = new NBTTagCompound();
            jobInterface.writeToNBT(bard);
            compound.setTag("Bard", bard);
        }
        if (advanced.job == JobType.PUPPET) {
            NBTTagCompound bard = new NBTTagCompound();
            jobInterface.writeToNBT(bard);
            compound.setTag("Puppet", bard);
        }
        if (advanced.role == RoleType.COMPANION) {
            NBTTagCompound bard = new NBTTagCompound();
            roleInterface.writeToNBT(bard);
            compound.setTag("Companion", bard);
        }

        if (this instanceof EntityCustomNpc) {
            compound.setTag("ModelData", ((EntityCustomNpc) this).modelData.writeToNBT());
        }
        return compound;
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        try {
            readSpawnData(Server.readNBT(buf));
        } catch (IOException e) {
        }
    }

    public void readSpawnData(NBTTagCompound compound) {
        stats.setMaxHealth(compound.getInteger("MaxHealth"));
        ais.setWalkingSpeed(compound.getInteger("Speed"));
        stats.hideKilledBody = compound.getBoolean("DeadBody");
        ais.setStandingType(compound.getInteger("StandingState"));
        ais.setMovingType(compound.getInteger("MovingState"));
        ais.orientation = compound.getInteger("Orientation");

        inventory.armor = NBTTags.getIItemStackMap(compound.getTagList("Armor", 10));
        inventory.weapons = NBTTags.getIItemStackMap(compound.getTagList("Weapons", 10));
        advanced.setRole(compound.getInteger("Role"));
        advanced.setJob(compound.getInteger("Job"));
        if (advanced.job == JobType.BARD) {
            NBTTagCompound bard = compound.getCompoundTag("Bard");
            jobInterface.readFromNBT(bard);
        }
        if (advanced.job == JobType.PUPPET) {
            NBTTagCompound puppet = compound.getCompoundTag("Puppet");
            jobInterface.readFromNBT(puppet);
        }
        if (advanced.role == RoleType.COMPANION) {
            NBTTagCompound puppet = compound.getCompoundTag("Companion");
            roleInterface.readFromNBT(puppet);
        }
        if (this instanceof EntityCustomNpc) {
            ((EntityCustomNpc) this).modelData.readFromNBT(compound.getCompoundTag("ModelData"));
        }
        display.readToNBT(compound);
    }

    @Override
    public Entity getCommandSenderEntity() {
        if (world.isRemote)
            return this;
        EntityUtil.Copy(this, CommandPlayer);
        CommandPlayer.setWorld(world);
        CommandPlayer.setPosition(posX, posY, posZ);
        return CommandPlayer;
    }

    @Override
    public String getName() {
        return display.getName();
    }

    @Override
    public BlockPos getPosition() {
        return new BlockPos(posX, posY, posZ);
    }

    @Override
    public Vec3d getPositionVector() {
        return new Vec3d(posX, posY, posZ);
    }

    @Override
    public boolean canAttackClass(Class par1Class) {
        return EntityBat.class != par1Class;
    }

    public void setImmuneToFire(boolean immuneToFire) {
        this.isImmuneToFire = immuneToFire;
        stats.immuneToFire = immuneToFire;
    }

    @Override
    public void fall(float distance, float modifier) {
        if (!this.stats.noFallDamage)
            super.fall(distance, modifier);
    }

    @Override
    public void setInWeb() {
        if (!stats.ignoreCobweb)
            super.setInWeb();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isKilled() && display.getHasHitbox();
    }

    @Override
    public EnumPushReaction getPushReaction() {
        return display.getHasHitbox() ? super.getPushReaction() : EnumPushReaction.IGNORE;
    }

    public EntityAIRangedAttack getRangedTask() {
        return this.aiRange;
    }

    public String getRoleData() {
        return dataManager.get(RoleData);
    }

    public void setRoleData(String s) {
        dataManager.set(RoleData, s);
    }

    public String getJobData() {
        return dataManager.get(RoleData);
    }

    public void setJobData(String s) {
        dataManager.set(RoleData, s);
    }

    @Override
    public World getEntityWorld() {
        return world;
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return display.getVisible() == 1 && (player.getHeldItemMainhand().isEmpty() || player.getHeldItemMainhand().getItem() != CustomItems.wand);
    }

    @Override
    public boolean isInvisible() {
        return display.getVisible() != 0;
    }

    @Override
    public void sendMessage(ITextComponent var1) {
    }

    public void setCurrentAnimation(int animation) {
        currentAnimation = animation;
        dataManager.set(Animation, animation);
    }

    public boolean canSee(Entity entity) {
        return this.getEntitySenses().canSee(entity);
    }

    public boolean isFollower() {
        if (advanced.scenes.getOwner() != null)
            return true;
        return roleInterface != null && roleInterface.isFollowing() || jobInterface != null && jobInterface.isFollowing();
    }

    public EntityLivingBase getOwner() {
        if (advanced.scenes.getOwner() != null)
            return advanced.scenes.getOwner();
        if (advanced.role == RoleType.FOLLOWER && roleInterface instanceof RoleFollower)
            return ((RoleFollower) roleInterface).owner;

        if (advanced.role == RoleType.COMPANION && roleInterface instanceof RoleCompanion)
            return ((RoleCompanion) roleInterface).owner;

        if (advanced.job == JobType.FOLLOWER && jobInterface instanceof JobFollower)
            return ((JobFollower) jobInterface).following;

        return null;
    }

    public boolean hasOwner() {
        if (advanced.scenes.getOwner() != null)
            return true;
        return advanced.role == RoleType.FOLLOWER && ((RoleFollower) roleInterface).hasOwner() ||
                advanced.role == RoleType.COMPANION && ((RoleCompanion) roleInterface).hasOwner() ||
                advanced.job == JobType.FOLLOWER && ((JobFollower) jobInterface).hasOwner();
    }

    public int followRange() {
        if (advanced.scenes.getOwner() != null)
            return 4;
        if (advanced.role == RoleType.FOLLOWER && roleInterface.isFollowing())
            return 6;
        if (advanced.role == RoleType.COMPANION && roleInterface.isFollowing())
            return 4;
        if (advanced.job == JobType.FOLLOWER && jobInterface.isFollowing())
            return 4;

        return 15;
    }

    @Override
    public void setHomePosAndDistance(BlockPos pos, int range) {
        super.setHomePosAndDistance(pos, range);
        ais.setStartPos(pos);
    }

    @Override
    protected float applyArmorCalculations(DamageSource source, float damage) {
        if (advanced.role == RoleType.COMPANION)
            damage = ((RoleCompanion) roleInterface).applyArmorCalculations(source, damage);
        return damage;
    }

    @Override
    public boolean isOnSameTeam(Entity entity) {
        if (!isRemote()) {
            if (entity instanceof EntityPlayer && getFaction().isFriendlyToPlayer((EntityPlayer) entity))
                return true;
            if (entity == getOwner())
                return true;
            if (entity instanceof EntityNPCInterface && ((EntityNPCInterface) entity).faction.id == faction.id)
                return true;
        }
        return super.isOnSameTeam(entity);
    }

    public void setDataWatcher(EntityDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void travel(float f1, float f2, float f3) {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        super.travel(f1, f2, f3);
        if (advanced.role == RoleType.COMPANION && !isRemote())
            ((RoleCompanion) roleInterface).addMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);
    }

    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean getLeashed() {
        return false;
    }

    public boolean nearPosition(BlockPos pos) {
        BlockPos npcpos = getPosition();
        float x = npcpos.getX() - pos.getX();
        float z = npcpos.getZ() - pos.getZ();
        float y = npcpos.getY() - pos.getY();
        float height = MathHelper.ceil(this.height + 1) * MathHelper.ceil(this.height + 1);
        return x * x + z * z < 2.5 && y * y < height + 2.5;
    }

    public void tpTo(EntityLivingBase owner) {
        if (owner == null)
            return;
        EnumFacing facing = owner.getHorizontalFacing().getOpposite();
        BlockPos pos = new BlockPos(owner.posX, owner.getEntityBoundingBox().minY, owner.posZ);
        pos = pos.add(facing.getXOffset(), 0, facing.getZOffset());
        pos = calculateTopPos(pos);

        for (int i = -1; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                BlockPos check;
                if (facing.getXOffset() == 0) {
                    check = pos.add(i, 0, j * facing.getZOffset());
                } else {
                    check = pos.add(j * facing.getXOffset(), 0, i);
                }
                check = calculateTopPos(check);
                if (!world.getBlockState(check).isFullBlock() && !world.getBlockState(check.up()).isFullBlock()) {
                    setLocationAndAngles(check.getX() + 0.5F, check.getY(), check.getZ() + 0.5F, rotationYaw, rotationPitch);
                    this.getNavigator().clearPath();
                    break;
                }
            }
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean getCanSpawnHere() {
        return this.getBlockPathWeight(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ)) >= 0.0F && this.world.getBlockState((new BlockPos(this)).down()).canEntitySpawn(this);
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }
}
