package noppes.npcs.common.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.ParticleType;
import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.api.entity.IEntityProjectile;
import noppes.npcs.api.event.ProjectileEvent;
import noppes.npcs.common.entity.data.DataRanged;
import noppes.npcs.controllers.ScriptContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityProjectile extends EntityThrowable {
    private static final DataParameter<Boolean> Gravity = EntityDataManager.createKey(EntityProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Arrow = EntityDataManager.createKey(EntityProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Is3d = EntityDataManager.createKey(EntityProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Glows = EntityDataManager.createKey(EntityProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Rotating = EntityDataManager.createKey(EntityProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Sticks = EntityDataManager.createKey(EntityProjectile.class, DataSerializers.BOOLEAN);

    private static final DataParameter<ItemStack> ItemStackThrown = EntityDataManager.createKey(EntityProjectile.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Integer> Velocity = EntityDataManager.createKey(EntityProjectile.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> Size = EntityDataManager.createKey(EntityProjectile.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> Particle = EntityDataManager.createKey(EntityProjectile.class, DataSerializers.VARINT);
    public int throwableShake = 0;
    public int arrowShake = 0;
    public boolean canBePickedUp = false;
    public boolean destroyedOnEntityHit = true;
    public int ticksInAir = 0;
    /**
     * Properties settable by GUI
     */

    public float damage = 5;
    public int punch = 0;
    public boolean accelerate = false;
    public boolean explosiveDamage = true;
    public int explosiveRadius = 0;
    public int effect = PotionEffectType.NONE;
    public int duration = 5;
    public int amplify = 0;
    public int accuracy = 60;
    public IProjectileCallback callback;
    public List<ScriptContainer> scripts = new ArrayList<>();
    protected boolean inGround = false;
    private BlockPos tilePos = BlockPos.ORIGIN;
    private Block inTile;
    private int inData = 0;
    /**
     * Is the entity that throws this 'thing' (snowball, ender pearl, eye of ender or potion)
     */
    private EntityLivingBase thrower;
    private EntityNPCInterface npc;
    private String throwerName = null;
    private int ticksInGround;
    private double accelerationX;
    private double accelerationY;
    private double accelerationZ;

    public EntityProjectile(World par1World) {
        super(par1World);
        this.setSize(0.25F, 0.25F);
    }

    public EntityProjectile(World par1World, EntityLivingBase par2EntityLiving, ItemStack item, boolean isNPC) {
        super(par1World);
        this.thrower = par2EntityLiving;
        if (this.thrower != null)
            this.throwerName = this.thrower.getUniqueID().toString();
        setThrownItem(item);
        this.dataManager.set(Arrow, this.getItem() == Items.ARROW);
        this.setSize(getSize() / 10f, getSize() / 10f);
        this.setLocationAndAngles(par2EntityLiving.posX, par2EntityLiving.posY + (double) par2EntityLiving.getEyeHeight(), par2EntityLiving.posZ, par2EntityLiving.rotationYaw, par2EntityLiving.rotationPitch);
        this.posX -= (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.1F);
        this.posY -= 0.1f;
        this.posZ -= (double) (MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.1F);
        this.setPosition(this.posX, this.posY, this.posZ);

        if (isNPC) {
            this.npc = (EntityNPCInterface) this.thrower;
            this.getStatProperties(this.npc.stats.ranged);
        }
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(ItemStackThrown, ItemStack.EMPTY);
        this.dataManager.register(Velocity, 10);
        this.dataManager.register(Size, 10);
        this.dataManager.register(Particle, 0);

        this.dataManager.register(Gravity, false);
        this.dataManager.register(Glows, false);
        this.dataManager.register(Arrow, false);
        this.dataManager.register(Is3d, false);
        this.dataManager.register(Rotating, false);
        this.dataManager.register(Sticks, false);
    }

    @Override
    @SideOnly(Side.CLIENT)

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double par1) {
        double d1 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
        d1 *= 64.0D;
        return par1 < d1 * d1;
    }

    public void setThrownItem(ItemStack item) {
        dataManager.set(ItemStackThrown, item);
    }

    public int getSize() {
        return this.dataManager.get(Size);
    }

    /**
     * Par: X, Y, Z, Angle, Accuracy
     */
    @Override
    public void shoot(double par1, double par3, double par5, float par7, float par8) {
        float f2 = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
        float f3 = MathHelper.sqrt(par1 * par1 + par5 * par5);
        float yaw = (float) (Math.atan2(par1, par5) * 180.0D / Math.PI);
        float pitch = this.hasGravity() ? par7 : (float) (Math.atan2(par3, (double) f3) * 180.0D / Math.PI);
        this.prevRotationYaw = this.rotationYaw = yaw;
        this.prevRotationPitch = this.rotationPitch = pitch;
        this.motionX = (double) (MathHelper.sin(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI));
        this.motionZ = (double) (MathHelper.cos(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI));
        this.motionY = (double) (MathHelper.sin((pitch + 1.0F) / 180.0F * (float) Math.PI));
        this.motionX += this.rand.nextGaussian() * 0.007499999832361937D * (double) par8;
        this.motionZ += this.rand.nextGaussian() * 0.007499999832361937D * (double) par8;
        this.motionY += this.rand.nextGaussian() * 0.007499999832361937D * (double) par8;
        this.motionX *= this.getSpeed();
        this.motionZ *= this.getSpeed();
        this.motionY *= this.getSpeed();
        this.accelerationX = par1 / f2 * 0.1D;
        this.accelerationY = par3 / f2 * 0.1D;
        this.accelerationZ = par5 / f2 * 0.1D;
        this.ticksInGround = 0;
    }

    /**
     * get an angle for firing at coordinates XYZ
     * Par: X Distance, Y Distance, Z Distance, Horizontial Distance
     */
    public float getAngleForXYZ(double varX, double varY, double varZ, double horiDist, boolean arc) {
        float g = this.getGravityVelocity();
        float var1 = this.getSpeed() * this.getSpeed();
        double var2 = (g * horiDist);
        double var3 = ((g * horiDist * horiDist) + (2 * varY * var1));
        double var4 = (var1 * var1) - (g * var3);
        if (var4 < 0) return 30.0F;
        float var6 = arc ? var1 + MathHelper.sqrt(var4) : var1 - MathHelper.sqrt(var4);
        float var7 = (float) (Math.atan2(var6, var2) * 180.0D / Math.PI);
        return var7;
    }

    public void shoot(float speed) {
        double varX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI));
        double varZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI));
        double varY = (double) (-MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI));
        this.shoot(varX, varY, varZ, -rotationPitch, speed);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double par1, double par3, double par5, float par7, float par8, int par9, boolean bo) {
        if (world.isRemote && inGround)
            return;
        this.setPosition(par1, par3, par5);
        this.setRotation(par7, par8);
    }

    @Override
    public void onUpdate() {
        super.onEntityUpdate();

        if (++ticksExisted % 10 == 0) {
            EventHooks.onProjectileTick(this);
        }

        if (this.effect == PotionEffectType.FIRE && !this.inGround)
            this.setFire(1);

        IBlockState state = this.world.getBlockState(tilePos);
        Block block = state.getBlock();

        if ((this.isArrow() || this.sticksToWalls()) && tilePos != BlockPos.ORIGIN) {
            AxisAlignedBB axisalignedbb = state.getCollisionBoundingBox(this.world, tilePos);

            if (axisalignedbb != null && axisalignedbb.contains(new Vec3d(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        if (this.inGround) {
            int j = block.getMetaFromState(state);
            if (block == this.inTile && j == this.inData) {
                ++this.ticksInGround;

                if (this.ticksInGround == 1200) {
                    this.setDead();
                }
            } else {
                this.inGround = false;
                this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
                this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
                this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        } else {
            ++this.ticksInAir;

            if (this.ticksInAir == 1200) {
                this.setDead();
            }
            Vec3d vec3 = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec31 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult movingobjectposition = this.world.rayTraceBlocks(vec3, vec31, false, true, false);
            vec3 = new Vec3d(this.posX, this.posY, this.posZ);
            vec31 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (movingobjectposition != null) {
                vec31 = new Vec3d(movingobjectposition.hitVec.x, movingobjectposition.hitVec.y, movingobjectposition.hitVec.z);
            }
            if (!this.world.isRemote) {
                Entity entity = null;
                List list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(this.motionX, this.motionY, this.motionZ).grow(1.0D, 1.0D, 1.0D));
                double d0 = 0.0D;
                EntityLivingBase entityliving = this.getThrower();

                for (int k = 0; k < list.size(); ++k) {
                    Entity entity1 = (Entity) list.get(k);
                    if (entity1.canBeCollidedWith() && (!entity1.isEntityEqual(this.thrower) || this.ticksInAir >= 25)) {
                        float f = 0.3F;
                        AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double) f, (double) f, (double) f);
                        RayTraceResult movingobjectposition1 = axisalignedbb.calculateIntercept(vec3, vec31);

                        if (movingobjectposition1 != null) {
                            double d1 = vec3.distanceTo(movingobjectposition1.hitVec);

                            if (d1 < d0 || d0 == 0.0D) {
                                entity = entity1;
                                d0 = d1;
                            }
                        }
                    }
                }

                if (entity != null) {
                    movingobjectposition = new RayTraceResult(entity);
                }

                if (movingobjectposition != null && movingobjectposition.entityHit != null) {
                    if (npc != null && movingobjectposition.entityHit instanceof EntityLivingBase &&
                            npc.isOnSameTeam(movingobjectposition.entityHit)) {
                        movingobjectposition = null;
                    } else if (movingobjectposition.entityHit instanceof EntityPlayer) {
                        EntityPlayer entityplayer = (EntityPlayer) movingobjectposition.entityHit;
                        if (entityplayer.capabilities.disableDamage ||
                                this.thrower instanceof EntityPlayer && !((EntityPlayer) this.thrower).canAttackPlayer(entityplayer)) {
                            movingobjectposition = null;
                        }
                    }
                }
            }

            if (movingobjectposition != null) {
                if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK && this.world.getBlockState(movingobjectposition.getBlockPos()).getBlock() == Blocks.PORTAL) {
                    this.setPortal(movingobjectposition.getBlockPos());
                } else {
                    this.dataManager.set(Rotating, false);
                    this.onImpact(movingobjectposition);
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

            for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f1) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch);
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw);
            if (this.isRotating()) {
                int spin = isBlock() ? 10 : 20;
                this.rotationPitch -= (this.ticksInAir % 15) * spin * getSpeed();
            }
            float f2 = this.getMotionFactor();
            float f3 = this.getGravityVelocity();

            if (this.isInWater()) {
                if (world.isRemote) {
                    for (int k = 0; k < 4; ++k) {
                        float f4 = 0.25F;
                        this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double) f4, this.posY - this.motionY * (double) f4, this.posZ - this.motionZ * (double) f4, this.motionX, this.motionY, this.motionZ);
                    }
                }

                f2 = 0.8F;
            }

            this.motionX *= (double) f2;
            this.motionY *= (double) f2;
            this.motionZ *= (double) f2;

            if (hasGravity())
                this.motionY -= (double) f3;

            if (accelerate) {
                this.motionX += this.accelerationX;
                this.motionY += this.accelerationY;
                this.motionZ += this.accelerationZ;
            }

            if (world.isRemote && this.dataManager.get(Particle) > 0) {
                this.world.spawnParticle(ParticleType.getMCType(dataManager.get(Particle)), this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            }
            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }

    public boolean isBlock() {
        ItemStack item = this.getItemDisplay();
        if (item.isEmpty())
            return false;
        return item.getItem() instanceof ItemBlock;
    }

    private Item getItem() {
        ItemStack item = this.getItemDisplay();
        if (item.isEmpty())
            return Items.AIR;
        return item.getItem();
    }

    protected float getMotionFactor() {
        return accelerate ? 0.95F : 1.0F;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    @Override
    protected void onImpact(RayTraceResult movingobjectposition) {
        if (!world.isRemote) {
            BlockPos pos = null;
            ProjectileEvent.ImpactEvent event;
            if (movingobjectposition.entityHit != null) {
                pos = movingobjectposition.entityHit.getPosition();
                event = new ProjectileEvent.ImpactEvent((IEntityProjectile) NpcAPI.instance().getIEntity(this), 0, movingobjectposition.entityHit);
            } else {
                pos = movingobjectposition.getBlockPos();
                event = new ProjectileEvent.ImpactEvent((IEntityProjectile) NpcAPI.instance().getIEntity(this), 1, NpcAPI.instance().getIBlock(world, pos));
            }

            if (pos == BlockPos.ORIGIN)
                pos = new BlockPos(movingobjectposition.hitVec);
            if (callback != null && callback.onImpact(this, pos, movingobjectposition.entityHit))
                return;

            EventHooks.onProjectileImpact(this, event);
        }

        if (movingobjectposition.entityHit != null) {
            float damage = this.damage;
            if (damage == 0)
                damage = 0.001f;

            if (movingobjectposition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), damage)) {
                if (movingobjectposition.entityHit instanceof EntityLivingBase && (this.isArrow() || this.sticksToWalls())) {
                    EntityLivingBase entityliving = (EntityLivingBase) movingobjectposition.entityHit;

                    if (!this.world.isRemote) {
                        entityliving.setArrowCountInEntity(entityliving.getArrowCountInEntity() + 1);
                    }

                    if (destroyedOnEntityHit && !(movingobjectposition.entityHit instanceof EntityEnderman)) {
                        this.setDead();
                    }
                }

                if (this.isBlock()) {
                    this.world.playEvent(null, 2001, movingobjectposition.entityHit.getPosition(), Item.getIdFromItem(getItem()));
                } else if (!this.isArrow() && !this.sticksToWalls()) {
                    int[] intArr = new int[]{Item.getIdFromItem(getItem())};
                    if (getItem().getHasSubtypes())
                        intArr = new int[]{Item.getIdFromItem(getItem()), getItemDisplay().getMetadata()};
                    for (int i = 0; i < 8; ++i) {
                        world.spawnParticle(EnumParticleTypes.ITEM_CRACK, posX, posY, posZ, rand.nextGaussian() * 0.15, rand.nextGaussian() * 0.2, rand.nextGaussian() * 0.15, intArr);
                    }
                }

                if (this.punch > 0) {
                    float f3 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                    if (f3 > 0.0F) {
                        movingobjectposition.entityHit.addVelocity(this.motionX * (double) this.punch * 0.6000000238418579D / (double) f3, 0.1D, this.motionZ * (double) this.punch * 0.6000000238418579D / (double) f3);
                    }
                }

                if (this.effect != PotionEffectType.NONE && movingobjectposition.entityHit instanceof EntityLivingBase) {
                    if (this.effect != PotionEffectType.FIRE) {
                        Potion p = PotionEffectType.getMCType(effect);
                        ((EntityLivingBase) movingobjectposition.entityHit).addPotionEffect(new PotionEffect(p, this.duration * 20, this.amplify));
                    } else {
                        movingobjectposition.entityHit.setFire(duration);
                    }
                }
            } else if (this.hasGravity() && (this.isArrow() || this.sticksToWalls())) {
                this.motionX *= -0.10000000149011612D;
                this.motionY *= -0.10000000149011612D;
                this.motionZ *= -0.10000000149011612D;
                this.rotationYaw += 180.0F;
                this.prevRotationYaw += 180.0F;
                this.ticksInAir = 0;
            }
        } else {
            if (this.isArrow() || this.sticksToWalls()) {
//    			if (this.sticksToWalls()) {
//    				float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
//    				float f1 = this.isArrow() ? 0.0F :this.isRotating() ? 180.0F : 225.0F;
//                    this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f) * 180.0D / Math.PI) + f1;
//    			}
                this.tilePos = movingobjectposition.getBlockPos();
                IBlockState state = world.getBlockState(tilePos);
                this.inTile = state.getBlock();
                this.inData = inTile.getMetaFromState(state);
                this.motionX = (double) ((float) (movingobjectposition.hitVec.x - this.posX));
                this.motionY = (double) ((float) (movingobjectposition.hitVec.y - this.posY));
                this.motionZ = (double) ((float) (movingobjectposition.hitVec.z - this.posZ));
                float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                this.posX -= this.motionX / (double) f2 * 0.05000000074505806D;
                this.posY -= this.motionY / (double) f2 * 0.05000000074505806D;
                this.posZ -= this.motionZ / (double) f2 * 0.05000000074505806D;
                this.inGround = true;
                this.arrowShake = 7;

                if (!this.hasGravity()) {
                    this.dataManager.set(Gravity, true);
                }

                if (this.inTile != null) {//onEntityCollidedWithBlock
                    inTile.onEntityCollision(this.world, this.tilePos, state, this);
                }
            } else {
                if (this.isBlock()) {
                    this.world.playEvent(null, 2001, getPosition(), Item.getIdFromItem(getItem()));
                } else {
                    int[] intArr = new int[]{Item.getIdFromItem(getItem())};
                    if (getItem().getHasSubtypes())
                        intArr = new int[]{Item.getIdFromItem(getItem()), getItemDisplay().getMetadata()};
                    for (int i = 0; i < 8; ++i) {
                        world.spawnParticle(EnumParticleTypes.ITEM_CRACK, posX, posY, posZ, rand.nextGaussian() * 0.15, rand.nextGaussian() * 0.2, rand.nextGaussian() * 0.15, intArr);
                    }
                }
            }
        }


        if (this.explosiveRadius > 0) {
            boolean terraindamage = this.world.getGameRules().getBoolean("mobGriefing") && explosiveDamage;
            world.newExplosion(getThrower() == null ? this : getThrower(), posX, posY, posZ, explosiveRadius, this.effect == PotionEffectType.FIRE, terraindamage);

            if (this.effect != PotionEffectType.NONE) {
                AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().grow(explosiveRadius * 2, explosiveRadius * 2, explosiveRadius * 2);
                List<EntityLivingBase> list1 = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
                for (EntityLivingBase entity : list1) {
                    if (this.effect != PotionEffectType.FIRE) {
                        Potion p = PotionEffectType.getMCType(effect);
                        if (p != null)
                            entity.addPotionEffect(new PotionEffect(p, this.duration * 20, this.amplify));
                    } else {
                        entity.setFire(duration);
                    }
                }
                this.world.playEvent(null, 2002, getPosition(), this.getPotionColor(this.effect));
            }

            this.setDead();
        }

        if (!this.world.isRemote && !this.isArrow() && !this.sticksToWalls()) {
            this.setDead();
        }
    }

    private void blockParticles() {

    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setShort("xTile", (short) this.tilePos.getX());
        par1NBTTagCompound.setShort("yTile", (short) this.tilePos.getY());
        par1NBTTagCompound.setShort("zTile", (short) this.tilePos.getZ());
        par1NBTTagCompound.setByte("inTile", (byte) Block.getIdFromBlock(this.inTile));
        par1NBTTagCompound.setByte("inData", (byte) this.inData);
        par1NBTTagCompound.setByte("shake", (byte) this.throwableShake);
        par1NBTTagCompound.setBoolean("inGround", this.inGround);
        par1NBTTagCompound.setBoolean("isArrow", this.isArrow());
        par1NBTTagCompound.setTag("direction", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
        par1NBTTagCompound.setBoolean("canBePickedUp", canBePickedUp);

        if ((this.throwerName == null || this.throwerName.length() == 0) && this.thrower != null && this.thrower instanceof EntityPlayer) {
            this.throwerName = this.thrower.getUniqueID().toString();
        }

        par1NBTTagCompound.setString("ownerName", this.throwerName == null ? "" : this.throwerName);
        par1NBTTagCompound.setTag("Item", this.getItemDisplay().writeToNBT(new NBTTagCompound()));

        par1NBTTagCompound.setFloat("damagev2", damage);
        par1NBTTagCompound.setInteger("punch", punch);
        par1NBTTagCompound.setInteger("size", this.dataManager.get(Size));
        par1NBTTagCompound.setInteger("velocity", this.dataManager.get(Velocity));
        par1NBTTagCompound.setInteger("explosiveRadius", explosiveRadius);
        par1NBTTagCompound.setInteger("effectDuration", duration);
        par1NBTTagCompound.setBoolean("gravity", this.hasGravity());
        par1NBTTagCompound.setBoolean("accelerate", this.accelerate);
        par1NBTTagCompound.setBoolean("glows", this.dataManager.get(Glows));
        par1NBTTagCompound.setInteger("PotionEffect", effect);
        par1NBTTagCompound.setInteger("trailenum", this.dataManager.get(Particle));
        par1NBTTagCompound.setBoolean("Render3D", this.dataManager.get(Is3d));
        par1NBTTagCompound.setBoolean("Spins", this.dataManager.get(Rotating));
        par1NBTTagCompound.setBoolean("Sticks", this.dataManager.get(Sticks));
        par1NBTTagCompound.setInteger("accuracy", accuracy);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.tilePos = new BlockPos(compound.getShort("xTile"), compound.getShort("yTile"), compound.getShort("zTile"));
        this.inTile = Block.getBlockById(compound.getByte("inTile") & 255);
        this.inData = compound.getByte("inData") & 255;
        this.throwableShake = compound.getByte("shake") & 255;
        this.inGround = compound.getByte("inGround") == 1;
        this.dataManager.set(Arrow, compound.getBoolean("isArrow"));
        this.throwerName = compound.getString("ownerName");
        this.canBePickedUp = compound.getBoolean("canBePickedUp");

        this.damage = compound.getFloat("damagev2");
        this.punch = compound.getInteger("punch");
        this.explosiveRadius = compound.getInteger("explosiveRadius");
        this.duration = compound.getInteger("effectDuration");
        this.accelerate = compound.getBoolean("accelerate");
        this.effect = compound.getInteger("PotionEffect");
        this.accuracy = compound.getInteger("accuracy");


        this.dataManager.set(Particle, compound.getInteger("trailenum"));
        this.dataManager.set(Size, compound.getInteger("size"));
        this.dataManager.set(Glows, compound.getBoolean("glows"));
        this.dataManager.set(Velocity, compound.getInteger("velocity"));
        this.dataManager.set(Gravity, compound.getBoolean("gravity"));
        this.dataManager.set(Is3d, compound.getBoolean("Render3D"));
        this.dataManager.set(Rotating, compound.getBoolean("Spins"));
        this.dataManager.set(Sticks, compound.getBoolean("Sticks"));

        if (this.throwerName != null && this.throwerName.length() == 0) {
            this.throwerName = null;
        }
        if (compound.hasKey("direction")) {
            NBTTagList nbttaglist = compound.getTagList("direction", 6);
            this.motionX = nbttaglist.getDoubleAt(0);
            this.motionY = nbttaglist.getDoubleAt(1);
            this.motionZ = nbttaglist.getDoubleAt(2);
        }

        NBTTagCompound var2 = compound.getCompoundTag("Item");
        ItemStack item = new ItemStack(var2);

        if (item.isEmpty())
            this.setDead();
        else
            dataManager.set(ItemStackThrown, item);
    }

    @Override
    public EntityLivingBase getThrower() {
        if (throwerName == null || throwerName.isEmpty())
            return null;
        try {
            UUID uuid = UUID.fromString(throwerName);
            if (this.thrower == null && uuid != null)
                this.thrower = this.world.getPlayerEntityByUUID(uuid);
        } catch (IllegalArgumentException ex) {

        }

        return this.thrower;
    }

    private int getPotionColor(int p) {
        switch (p) {
            case PotionEffectType.POISON:
                return 32660;
            case PotionEffectType.HUNGER:
                return 32660;
            case PotionEffectType.WEAKNESS:
                return 32696;
            case PotionEffectType.SLOWNESS:
                return 32698;
            case PotionEffectType.NAUSEA:
                return 32732;
            case PotionEffectType.BLINDNESS:
                return 15;
            case PotionEffectType.WITHER:
                return 32732;
            default:
                return 0;
        }
    }

    public void getStatProperties(DataRanged stats) {
        this.damage = stats.getStrength();
        this.punch = stats.getKnockback();
        this.accelerate = stats.getAccelerate();
        this.explosiveRadius = stats.getExplodeSize();
        this.effect = stats.getEffectType();
        this.duration = stats.getEffectTime();
        this.amplify = stats.getEffectStrength();
        this.setParticleEffect(stats.getParticle());
        this.dataManager.set(Size, stats.getSize());
        this.dataManager.set(Glows, stats.getGlows());
        this.setSpeed(stats.getSpeed());
        this.setHasGravity(stats.getHasGravity());
        setIs3D(stats.getRender3D());
        this.setRotating(stats.getSpins());
        this.setStickInWall(stats.getSticks());
    }

    public void setParticleEffect(int type) {
        this.dataManager.set(Particle, type);
    }

    public void setHasGravity(boolean bo) {
        this.dataManager.set(Gravity, bo);
    }

    public void setIs3D(boolean bo) {
        this.dataManager.set(Is3d, bo);
    }

    public void setStickInWall(boolean bo) {
        this.dataManager.set(Sticks, bo);
    }

    public ItemStack getItemDisplay() {
        return dataManager.get(ItemStackThrown);
    }

    @Override
    public float getBrightness() {
        return this.dataManager.get(Glows) ? 1.0F : super.getBrightness();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getBrightnessForRender() {
        return this.dataManager.get(Glows) ? 15728880 : super.getBrightnessForRender();
    }

    public boolean hasGravity() {
        return this.dataManager.get(Gravity);
    }

    public float getSpeed() {
        return this.dataManager.get(Velocity) / 10.0F;
    }

    public void setSpeed(int speed) {
        this.dataManager.set(Velocity, speed);
    }

    public boolean isArrow() {
        return this.dataManager.get(Arrow);
    }

    public boolean isRotating() {
        return this.dataManager.get(Rotating);
    }

    public void setRotating(boolean bo) {
        dataManager.set(Rotating, bo);
    }

    public boolean glows() {
        return this.dataManager.get(Glows);
    }

    public boolean is3D() {
        return this.dataManager.get(Is3d) || isBlock();
    }

    public boolean sticksToWalls() {
        return this.is3D() && this.dataManager.get(Sticks);
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
        if (this.world.isRemote || !canBePickedUp || !this.inGround || this.arrowShake > 0)
            return;

        if (par1EntityPlayer.inventory.addItemStackToInventory(getItemDisplay())) {
            inGround = false;
            this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            par1EntityPlayer.onItemPickup(this, 1);
            this.setDead();
        }
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        if (!getItemDisplay().isEmpty())
            return new TextComponentTranslation(getItemDisplay().getDisplayName());
        return super.getDisplayName();
    }

    public interface IProjectileCallback {
        boolean onImpact(EntityProjectile entityProjectile, BlockPos pos, Entity entity);
    }
}
