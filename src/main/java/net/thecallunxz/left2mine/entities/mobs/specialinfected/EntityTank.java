package net.thecallunxz.left2mine.entities.mobs.specialinfected;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.entities.ICustomDimensions;
import net.thecallunxz.left2mine.entities.ICustomProjectileBox;
import net.thecallunxz.left2mine.entities.ai.AICustomDoorBreak;
import net.thecallunxz.left2mine.entities.ai.AICustomHurtAttack;
import net.thecallunxz.left2mine.entities.ai.AIPinnedTarget;
import net.thecallunxz.left2mine.entities.ai.AITankFind;
import net.thecallunxz.left2mine.entities.mobs.DamageSourceMaul;
import net.thecallunxz.left2mine.entities.mobs.EntityBossInfected;
import net.thecallunxz.left2mine.entities.mobs.IRagdollEntities;
import net.thecallunxz.left2mine.entities.mobs.ITriggerPush;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.pathfinding.PathNavigateSpecial;
import net.thecallunxz.left2mine.ragdoll.corpse.CustomCorpse;
import net.thecallunxz.left2mine.ragdoll.corpse.TankCorpse;
import net.thecallunxz.left2mine.util.DifficultyUtil;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class EntityTank extends EntityBossInfected implements ITriggerPush, IRagdollEntities, ICustomProjectileBox, ICustomDimensions {
   private static final DataParameter<Boolean> ATTACKING;
   private float lastStepModified = 0.0F;

   public EntityTank(World worldIn) {
      super(worldIn);
      this.tasks.addTask(0, new EntityTank.AIDespawn(worldIn, this));
      this.tasks.addTask(1, new AITankFind(this));
      this.tasks.addTask(2, new EntityAISwimming(this));
      this.tasks.addTask(3, new AICustomDoorBreak(this, true));
      this.tasks.addTask(4, new EntityTank.AIAttack(this));
      this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(5, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new AICustomHurtAttack(this, false));
      this.targetTasks.addTask(2, new AIPinnedTarget(this, EntityPlayer.class, false));
      this.setSize(0.8F, 1.8F);
   }

   public AxisAlignedBB getCustomProjectileBox() {
      return this.getEntityBoundingBox().grow(-1.2D, 0.2D, -1.2D);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      this.world.setEntityState(this, (byte)4);
      boolean flag = entityIn.attackEntityFrom(new DamageSourceMaul("tank", this), DifficultyUtil.getTankDamage(entityIn.getEntityWorld()));
      if (flag && entityIn instanceof EntityLivingBase) {
         ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 30, 99, true, false));
         double diffY = this.posY - (entityIn.posY - 0.3D);
         diffY = MathHelper.clamp(diffY, 0.0D, 0.3D);
         entityIn.motionY = diffY;
         Left2MineUtilities.knockBack(entityIn, this, 2.0F);
      }

      this.playSound(InitSounds.tank_hit, 2.0F, 1.0F);
      return flag;
   }

   public float getCustomHeight() {
      return 2.5F;
   }

   public float getCustomWidth() {
      return 2.0F;
   }

   protected boolean canDespawn() {
      return false;
   }

   public boolean onPush() {
      return false;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(DifficultyUtil.getTankHP(this.getEntityWorld()));
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.33D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(ATTACKING, false);
   }

   protected PathNavigate createNavigator(World worldIn) {
      return new PathNavigateSpecial(this, worldIn);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
   }

   public CustomCorpse getCorpse() {
      return new TankCorpse();
   }

   public int getMaxFallHeight() {
      return 100;
   }

   protected DamageSource getDamageSource() {
      return new EntityDamageSource("mob", this);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("Attacking", this.isAttacking());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setAttacking(compound.getBoolean("Attacking"));
   }

   protected float getSoundVolume() {
      return this.isAttacking() ? 2.0F : 1.0F;
   }

   public void playLivingSound() {
      super.playLivingSound();
      if (!this.isSilent() && !this.getEntityWorld().isRemote) {
         if (!this.isAttacking()) {
            Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this, 15));
         } else {
            Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(this, 16));
         }
      }

   }

   public int getTalkInterval() {
      return this.isAttacking() ? 40 : 80;
   }

   protected void playCustomStepSound(BlockPos pos, Block blockIn) {
      SoundType soundtype = blockIn.getSoundType(this.world.getBlockState(pos), this.world, pos, this);
      if (!blockIn.getDefaultState().getMaterial().isLiquid()) {
         this.playSound(InitSounds.tank_step, 2.0F, soundtype.getPitch());
      }

   }

   public void move(MoverType type, double x, double y, double z) {
      super.move(type, x, y, z);
      if (this.ticksExisted % 10 == 0 && this.onGround && this.distanceWalkedOnStepModified != this.lastStepModified) {
         this.lastStepModified = this.distanceWalkedOnStepModified;
         if (!this.isInWater()) {
            int j6 = MathHelper.floor(this.posX);
            int i1 = MathHelper.floor(this.posY - 0.20000000298023224D);
            int k6 = MathHelper.floor(this.posZ);
            BlockPos blockpos = new BlockPos(j6, i1, k6);
            this.playCustomStepSound(blockpos, this.world.getBlockState(blockpos).getBlock());
         }
      }

   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return null;
   }

   protected SoundEvent getDeathSound() {
      return InitSounds.tank_death;
   }

   public boolean isAttacking() {
      return (Boolean)this.dataManager.get(ATTACKING);
   }

   public void setAttacking(boolean attacking) {
      this.dataManager.set(ATTACKING, attacking);
   }

   static {
      ATTACKING = EntityDataManager.createKey(EntityTank.class, DataSerializers.BOOLEAN);
   }

   static class AIDespawn extends EntityAIBase {
      World world;
      EntityTank entitytank;

      public AIDespawn(World worldIn, EntityTank entitytank) {
         this.world = worldIn;
         this.entitytank = entitytank;
         this.setMutexBits(4);
      }

      public boolean shouldContinueExecuting() {
         return true;
      }

      public boolean shouldExecute() {
         return true;
      }

      public void updateTask() {
         WorldDataLeft2Mine data = WorldDataLeft2Mine.get(this.world);
         Predicate<Entity> predicate = EntitySelectors.CAN_AI_TARGET;
         if (this.entitytank.ticksExisted % 200 == 0) {
            EntityPlayer player = this.world.getClosestPlayer(this.entitytank.posX, this.entitytank.posY, this.entitytank.posZ, 100.0D, predicate);
            if (player == null) {
               data.addLurkingSpecial("tank");
               data.setBossCount(data.getBossCount() - 1);
               this.entitytank.setDead();
            }
         }

         if (this.entitytank.ticksExisted % 200 == 0) {
            if (this.entitytank.getAttackTarget() != null && this.entitytank.isAttacking()) {
               ((PathNavigateGround)this.entitytank.getNavigator()).setBreakDoors(true);
               ((PathNavigateGround)this.entitytank.getNavigator()).getNodeProcessor().setCanEnterDoors(true);
               ((PathNavigateGround)this.entitytank.getNavigator()).getNodeProcessor().setCanOpenDoors(true);
               if (this.entitytank.getAttackTarget() instanceof EntityPlayer && Left2MineUtilities.getPathToPos(this.entitytank.getPosition(), this.entitytank.getAttackTarget().getPosition(), this.world).distanceSq(this.entitytank.getAttackTarget().getPosition()) > 2.0D) {
                  if (Left2MineUtilities.canPlayersSeeEntity(this.world, this.entitytank)) {
                     return;
                  }

                  data.addLurkingSpecial("tank");
                  data.setBossCount(data.getBossCount() - 1);
                  this.entitytank.setDead();
               }
            } else {
               ((PathNavigateGround)this.entitytank.getNavigator()).setBreakDoors(false);
               ((PathNavigateGround)this.entitytank.getNavigator()).getNodeProcessor().setCanEnterDoors(false);
               ((PathNavigateGround)this.entitytank.getNavigator()).getNodeProcessor().setCanOpenDoors(false);
            }
         }

         if (!data.isInGame()) {
            data.setBossCount(data.getBossCount() - 1);
            this.entitytank.setDead();
         }

      }
   }

   static class AIAttack extends EntityAIAttackMelee {
      public AIAttack(EntityTank infected) {
         super(infected, 1.0D, true);
      }

      public boolean shouldExecute() {
         return super.shouldExecute() ? ((EntityTank)this.attacker).isAttacking() : false;
      }

      protected void checkAndPerformAttack(EntityLivingBase living, double number) {
         new Random();
         double d0 = this.getAttackReachSqr(living);
         if (number <= d0 * 0.75D && this.attackTick <= 0) {
            this.attackTick = 40;
            this.attacker.swingArm(EnumHand.MAIN_HAND);
            this.attacker.attackEntityAsMob(living);
         }

      }

      protected double getAttackReachSqr(EntityLivingBase attackTarget) {
         return (double)(8.0F + attackTarget.width);
      }
   }
}
