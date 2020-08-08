package net.thecallunxz.left2mine.entities;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItemNew extends Entity {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DataParameter<ItemStack> ITEM;
   private int age;
   private int delayBeforeCanPickup;
   private int health;
   private String thrower;
   private String owner;
   public float hoverStart;
   public int lifespan;

   public EntityItemNew(World worldIn, double x, double y, double z) {
      super(worldIn);
      this.lifespan = 6000;
      this.health = 5;
      this.hoverStart = (float)(Math.random() * 3.141592653589793D * 2.0D);
      this.setSize(0.65F, 0.65F);
      this.setPosition(x, y, z);
      this.rotationYaw = (float)(Math.random() * 360.0D);
      this.motionX = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D) / 10.0F);
      this.motionY = 0.020000000298023225D;
      this.motionZ = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D) / 10.0F);
   }

   public EntityItemNew(World worldIn, double x, double y, double z, ItemStack stack) {
      this(worldIn, x, y, z);
      this.setItem(stack);
      this.lifespan = stack.getItem() == null ? 6000 : stack.getItem().getEntityLifespan(stack, worldIn);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public EntityItemNew(World worldIn) {
      super(worldIn);
      this.lifespan = 6000;
      this.health = 5;
      this.hoverStart = (float)(Math.random() * 3.141592653589793D * 2.0D);
      this.setSize(0.25F, 0.25F);
      this.setItem(ItemStack.EMPTY);
   }

   protected void entityInit() {
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
   }

   public void onUpdate() {
      if (!this.getItem().getItem().onEntityItemUpdate(new EntityItem(this.world, this.posX, this.posY, this.posZ, this.getItem()))) {
         if (this.getItem().isEmpty()) {
            this.setDead();
         } else {
            super.onUpdate();
            if (this.delayBeforeCanPickup > 0 && this.delayBeforeCanPickup != 32767) {
               --this.delayBeforeCanPickup;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            double d0 = this.motionX;
            double d1 = this.motionY;
            double d2 = this.motionZ;
            if (!this.hasNoGravity()) {
               this.motionY -= 0.03999999910593033D;
            }

            if (this.world.isRemote) {
               this.noClip = false;
            } else {
               this.noClip = this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            boolean flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;
            if (flag || this.ticksExisted % 25 == 0) {
               if (this.world.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA) {
                  this.motionY = 0.20000000298023224D;
                  this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                  this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                  this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
               }

               if (!this.world.isRemote) {
                  this.searchForOtherItemsNearby();
               }
            }

            float f = 0.98F;
            if (this.onGround) {
               f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.98F;
            }

            this.motionX *= (double)f;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= (double)f;
            if (this.onGround) {
               this.motionY *= -0.5D;
            }

            if (this.age != -32768) {
               ++this.age;
            }

            this.handleWaterMovement();
            if (!this.world.isRemote) {
               double d3 = this.motionX - d0;
               double d4 = this.motionY - d1;
               double d5 = this.motionZ - d2;
               double d6 = d3 * d3 + d4 * d4 + d5 * d5;
               if (d6 > 0.01D) {
                  this.isAirBorne = true;
               }
            }

            ItemStack item = this.getItem();
            if (!this.world.isRemote && this.age >= this.lifespan) {
            }

            if (item.isEmpty()) {
               this.setDead();
            }
         }

      }
   }

   private void searchForOtherItemsNearby() {
      Iterator var1 = this.world.getEntitiesWithinAABB(EntityItemNew.class, this.getEntityBoundingBox().grow(0.5D, 0.0D, 0.5D)).iterator();

      while(var1.hasNext()) {
         EntityItemNew entityitem = (EntityItemNew)var1.next();
         this.combineItems(entityitem);
      }

   }

   private boolean combineItems(EntityItemNew other) {
      if (other == this) {
         return false;
      } else if (other.isEntityAlive() && this.isEntityAlive()) {
         ItemStack itemstack = this.getItem();
         ItemStack itemstack1 = other.getItem();
         if (this.delayBeforeCanPickup != 32767 && other.delayBeforeCanPickup != 32767) {
            if (this.age != -32768 && other.age != -32768) {
               if (itemstack1.getItem() != itemstack.getItem()) {
                  return false;
               } else if (itemstack1.hasTagCompound() ^ itemstack.hasTagCompound()) {
                  return false;
               } else if (itemstack1.hasTagCompound() && !itemstack1.getTagCompound().equals(itemstack.getTagCompound())) {
                  return false;
               } else if (itemstack1.getItem() == null) {
                  return false;
               } else if (itemstack1.getItem().getHasSubtypes() && itemstack1.getMetadata() != itemstack.getMetadata()) {
                  return false;
               } else if (itemstack1.getCount() < itemstack.getCount()) {
                  return other.combineItems(this);
               } else if (itemstack1.getCount() + itemstack.getCount() > itemstack1.getMaxStackSize()) {
                  return false;
               } else if (!itemstack.areCapsCompatible(itemstack1)) {
                  return false;
               } else {
                  itemstack1.grow(itemstack.getCount());
                  other.delayBeforeCanPickup = Math.max(other.delayBeforeCanPickup, this.delayBeforeCanPickup);
                  other.age = Math.min(other.age, this.age);
                  other.setItem(itemstack1);
                  this.setDead();
                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void setAgeToCreativeDespawnTime() {
      this.age = 4800;
   }

   public boolean handleWaterMovement() {
      if (this.world.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.WATER, this)) {
         if (!this.inWater && !this.firstUpdate) {
            this.doWaterSplashEffect();
         }

         this.inWater = true;
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   protected void dealFireDamage(int amount) {
      this.attackEntityFrom(DamageSource.IN_FIRE, (float)amount);
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (!this.world.isRemote && !this.isDead) {
         if (this.isEntityInvulnerable(source)) {
            return false;
         } else if (!this.getItem().isEmpty() && this.getItem().getItem() == Items.NETHER_STAR && source.isExplosion()) {
            return false;
         } else {
            this.markVelocityChanged();
            this.health = (int)((float)this.health - amount);
            if (this.health <= 0) {
               this.setDead();
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static void registerFixesItem(DataFixer fixer) {
      fixer.registerWalker(FixTypes.ENTITY, new ItemStackData(EntityItemNew.class, new String[]{"Item"}));
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setShort("Health", (short)this.health);
      compound.setShort("Age", (short)this.age);
      compound.setShort("PickupDelay", (short)this.delayBeforeCanPickup);
      compound.setInteger("Lifespan", this.lifespan);
      if (this.getThrower() != null) {
         compound.setString("Thrower", this.thrower);
      }

      if (this.getOwner() != null) {
         compound.setString("Owner", this.owner);
      }

      if (!this.getItem().isEmpty()) {
         compound.setTag("Item", this.getItem().writeToNBT(new NBTTagCompound()));
      }

   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.health = compound.getShort("Health");
      this.age = compound.getShort("Age");
      if (compound.hasKey("PickupDelay")) {
         this.delayBeforeCanPickup = compound.getShort("PickupDelay");
      }

      if (compound.hasKey("Owner")) {
         this.owner = compound.getString("Owner");
      }

      if (compound.hasKey("Thrower")) {
         this.thrower = compound.getString("Thrower");
      }

      NBTTagCompound nbttagcompound = compound.getCompoundTag("Item");
      this.setItem(new ItemStack(nbttagcompound));
      if (this.getItem().isEmpty()) {
         this.setDead();
      }

      if (compound.hasKey("Lifespan")) {
         this.lifespan = compound.getInteger("Lifespan");
      }

   }

   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (!this.world.isRemote) {
         if (this.delayBeforeCanPickup > 0) {
            return;
         }

         ItemStack itemstack = this.getItem();
         Item item = itemstack.getItem();
         int i = itemstack.getCount();
         int hook = ForgeEventFactory.onItemPickup(new EntityItem(this.world, this.posX, this.posY, this.posZ, this.getItem()), entityIn);
         if (hook < 0) {
            return;
         }

         if (this.delayBeforeCanPickup <= 0 && (this.owner == null || this.lifespan - this.age <= 200 || this.owner.equals(entityIn.getName())) && (hook == 1 || i <= 0 || entityIn.inventory.addItemStackToInventory(itemstack))) {
            FMLCommonHandler.instance().firePlayerItemPickupEvent(entityIn, new EntityItem(this.world, this.posX, this.posY, this.posZ, this.getItem()), this.getItem());
            entityIn.onItemPickup(new EntityItem(this.world, this.posX, this.posY, this.posZ, this.getItem()), i);
            if (itemstack.isEmpty()) {
               this.setDead();
               itemstack.setCount(i);
            }

            entityIn.addStat(StatList.getObjectsPickedUpStats(item), i);
         }
      }

   }

   public String getName() {
      return this.hasCustomName() ? this.getCustomNameTag() : I18n.translateToLocal("item." + this.getItem().getUnlocalizedName());
   }

   public boolean canBeAttackedWithItem() {
      return false;
   }

   @Nullable
   public Entity changeDimension(int dimensionIn) {
      Entity entity = super.changeDimension(dimensionIn);
      if (!this.world.isRemote && entity instanceof EntityItemNew) {
         ((EntityItemNew)entity).searchForOtherItemsNearby();
      }

      return entity;
   }

   public ItemStack getItem() {
      return (ItemStack)this.getDataManager().get(ITEM);
   }

   public void setItem(ItemStack stack) {
      this.getDataManager().set(ITEM, stack);
      this.getDataManager().setDirty(ITEM);
   }

   public String getOwner() {
      return this.owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public String getThrower() {
      return this.thrower;
   }

   public void setThrower(String thrower) {
      this.thrower = thrower;
   }

   @SideOnly(Side.CLIENT)
   public int getAge() {
      return this.age;
   }

   public void setDefaultPickupDelay() {
      this.delayBeforeCanPickup = 10;
   }

   public void setNoPickupDelay() {
      this.delayBeforeCanPickup = 0;
   }

   public void setInfinitePickupDelay() {
      this.delayBeforeCanPickup = 32767;
   }

   public void setPickupDelay(int ticks) {
      this.delayBeforeCanPickup = ticks;
   }

   public boolean cannotPickup() {
      return this.delayBeforeCanPickup > 0;
   }

   public void setNoDespawn() {
      this.age = -6000;
   }

   public void makeFakeItem() {
      this.setInfinitePickupDelay();
      this.age = this.getItem().getItem().getEntityLifespan(this.getItem(), this.world) - 1;
   }

   static {
      ITEM = EntityDataManager.createKey(EntityItemNew.class, DataSerializers.ITEM_STACK);
   }
}
