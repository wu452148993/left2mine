package net.thecallunxz.left2mine.entities.mobs;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.entities.ICustomDimensions;
import net.thecallunxz.left2mine.init.InitPotions;

public class EntityCustomMob extends EntityMob {
   private static final DataParameter<Boolean> ON_FIRECUSTOM;

   public EntityCustomMob(World worldIn) {
      super(worldIn);
      this.experienceValue = 0;
      this.stepHeight = 1.1F;
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.isOnFire()) {
         if (this.world.isRemote) {
            for(int j = 0; j < 10; ++j) {
               double w = (double)this.width;
               double h = (double)this.height;
               if (this instanceof ICustomDimensions) {
                  w = (double)((ICustomDimensions)this).getCustomWidth();
                  h = (double)((ICustomDimensions)this).getCustomHeight();
               }

               double addX = this.world.rand.nextDouble() * w - w / 2.0D;
               double addY = this.world.rand.nextDouble() * h;
               double addZ = this.world.rand.nextDouble() * w - w / 2.0D;
               Main.proxy.displayMobFire(this.world, this.posX + addX, this.posY + addY, this.posZ + addZ, 0, 0.05D, 0);
            }
         }

         if (this.isInWater()) {
            this.setOnFire(false);
            if (this.isPotionActive(InitPotions.on_fire)) {
               this.removePotionEffect(InitPotions.on_fire);
            }
         }
      }

   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(ON_FIRECUSTOM, false);
   }

   public boolean isOnFire() {
      return (Boolean)this.dataManager.get(ON_FIRECUSTOM);
   }

   public void setOnFire(boolean fire) {
      this.dataManager.set(ON_FIRECUSTOM, fire);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
   }

   public int getMaxFallHeight() {
      return 100;
   }

   protected DamageSource getDamageSource() {
      return new EntityDamageSource("mob", this);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("OnFireCustom", this.isOnFire());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setOnFire(compound.getBoolean("OnFireCustom"));
   }

   static {
      ON_FIRECUSTOM = EntityDataManager.createKey(EntityCustomMob.class, DataSerializers.BOOLEAN);
   }
}
