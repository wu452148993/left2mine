package net.thecallunxz.left2mine.entities.ai;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;
import net.thecallunxz.left2mine.init.InitSounds;

public class AICustomAttackTarget<T extends EntityLivingBase> extends AICustomTarget {
   protected final Class<T> targetClass;
   private final int targetChance;
   protected final AICustomAttackTarget.Sorter sorter;
   protected final Predicate<? super T> targetEntitySelector;
   protected T targetEntity;

   public AICustomAttackTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight) {
      this(creature, classTarget, checkSight, false);
   }

   public AICustomAttackTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
      this(creature, classTarget, 10, checkSight, onlyNearby, (Predicate)null);
   }

   public AICustomAttackTarget(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector) {
      super(creature, checkSight, onlyNearby);
      this.targetClass = classTarget;
      this.targetChance = chance;
      this.sorter = new AICustomAttackTarget.Sorter(creature);
      this.setMutexBits(1);
      this.targetEntitySelector = new Predicate<T>() {
         public boolean apply(@Nullable T p_apply_1_) {
            if (p_apply_1_ == null) {
               return false;
            } else if (targetSelector != null && !targetSelector.apply(p_apply_1_)) {
               return false;
            } else {
               return !EntitySelectors.NOT_SPECTATING.apply(p_apply_1_) ? false : AICustomAttackTarget.this.isSuitableTarget(p_apply_1_, false);
            }
         }
      };
   }

   public boolean shouldExecute() {
      if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
         return false;
      } else if (this.targetClass != EntityPlayer.class && this.targetClass != EntityPlayerMP.class) {
         List<T> list = this.taskOwner.world.getEntitiesWithinAABB(this.targetClass, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
         if (list.isEmpty()) {
            return false;
         } else {
            Collections.sort(list, this.sorter);
            this.targetEntity = (EntityLivingBase)list.get(0);
            return true;
         }
      } else {
         this.targetEntity = this.taskOwner.world.getNearestAttackablePlayer(this.taskOwner.posX, this.taskOwner.posY + (double)this.taskOwner.getEyeHeight(), this.taskOwner.posZ, this.getTargetDistance(), this.getTargetDistance(), new Function<EntityPlayer, Double>() {
            @Nullable
            public Double apply(@Nullable EntityPlayer p_apply_1_) {
               ItemStack itemstack = p_apply_1_.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
               if (itemstack.getItem() == Items.SKULL) {
                  int i = itemstack.getItemDamage();
                  boolean flag = AICustomAttackTarget.this.taskOwner instanceof EntitySkeleton && i == 0;
                  boolean flag1 = AICustomAttackTarget.this.taskOwner instanceof EntityZombie && i == 2;
                  boolean flag2 = AICustomAttackTarget.this.taskOwner instanceof EntityCreeper && i == 4;
                  if (flag || flag1 || flag2) {
                     return 0.5D;
                  }
               }

               return 1.0D;
            }
         }, this.targetEntitySelector);
         return this.targetEntity != null;
      }
   }

   protected AxisAlignedBB getTargetableArea(double targetDistance) {
      return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 4.0D, targetDistance);
   }

   public void startExecuting() {
      if (this.taskOwner instanceof EntityCommonInfected && !((EntityCommonInfected)this.taskOwner).isPanic()) {
         if (this.taskOwner.getAttackTarget() == null) {
            Iterator var1 = this.taskOwner.world.getEntitiesWithinAABB(this.taskOwner.getClass(), (new AxisAlignedBB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D)).grow(25.0D, 10.0D, 25.0D)).iterator();

            while(var1.hasNext()) {
               EntityCreature entitycreature = (EntityCreature)var1.next();
               if (entitycreature instanceof EntityCommonInfected && Math.pow((double)(entitycreature.ticksExisted - this.taskOwner.ticksExisted), 2.0D) < 1600.0D && entitycreature.getAttackTarget() == null) {
                  entitycreature.setAttackTarget(this.target);
               }
            }
         }

         this.taskOwner.world.playSound((EntityPlayer)null, this.taskOwner.getPosition(), InitSounds.common_ambient_rage, SoundCategory.HOSTILE, 1.25F, 1.0F);
      }

      this.taskOwner.setAttackTarget(this.targetEntity);
      super.startExecuting();
   }

   public static class Sorter implements Comparator<Entity> {
      private final Entity entity;

      public Sorter(Entity entityIn) {
         this.entity = entityIn;
      }

      public int compare(Entity p_compare_1_, Entity p_compare_2_) {
         double d0 = this.entity.getDistanceSq(p_compare_1_);
         double d1 = this.entity.getDistanceSq(p_compare_2_);
         if (d0 < d1) {
            return -1;
         } else {
            return d0 > d1 ? 1 : 0;
         }
      }
   }
}
