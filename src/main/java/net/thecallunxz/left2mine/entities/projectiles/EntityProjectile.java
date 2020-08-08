package net.thecallunxz.left2mine.entities.projectiles;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.capabilities.IStats;
import net.thecallunxz.left2mine.capabilities.StatsEnum;
import net.thecallunxz.left2mine.capabilities.StatsProvider;
import net.thecallunxz.left2mine.entities.EntityItemLoot;
import net.thecallunxz.left2mine.entities.EntityItemNew;
import net.thecallunxz.left2mine.entities.decals.EntityDecal;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityTank;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;
import net.thecallunxz.left2mine.items.weapons.properties.ProjectileProperties;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.DecalMessage;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.networking.client.StatsMessage;
import net.thecallunxz.left2mine.util.DifficultyUtil;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class EntityProjectile extends Entity implements IEntityAdditionalSpawnData {
   private static final Predicate<Entity> ARROW_TARGETS;
   protected int shooterId;
   private EntityLivingBase shooter;
   private ProjectileProperties projectile;
   protected ItemStack item;

   public EntityProjectile(World worldIn) {
      super(worldIn);
      this.item = ItemStack.EMPTY;
   }

   public EntityProjectile(World worldIn, EntityPlayerMP shooter, ItemGun item) {
      this(worldIn);
      this.shooterId = shooter.getEntityId();
      this.shooter = shooter;
      this.projectile = item.getProjectile();
      float accuracy = this.calculateAccuracyServer(item, shooter, shooter.getHeldItemMainhand());
      Vec3d dir = this.getGunAccuracy(shooter.rotationPitch, shooter.rotationYaw, accuracy, worldIn.rand);
      this.motionX = dir.x * this.projectile.speed + shooter.motionX;
      this.motionY = dir.y * this.projectile.speed;
      this.motionZ = dir.z * this.projectile.speed + shooter.motionZ;
      this.updateHeading();
      this.setSize(this.projectile.size, this.projectile.size);
      this.setPosition(shooter.posX, shooter.posY + (double)shooter.getEyeHeight() - 0.10000000149011612D, shooter.posZ);
      String var6 = this.projectile.type;
      byte var7 = -1;
      switch(var6.hashCode()) {
      case 62970894:
         if (var6.equals("BASIC")) {
            var7 = 0;
         }
         break;
      case 1787432198:
         if (var6.equals("MISSILE")) {
            var7 = 1;
         }
      }

      switch(var7) {
      case 0:
         this.item = new ItemStack(Items.APPLE);
         break;
      case 1:
         this.item = new ItemStack(Items.APPLE);
      }

   }

   public static Predicate<Entity> isNotItem() {
      return (entity) -> {
         return !(entity instanceof EntityItemNew);
      };
   }

   public Vec3d getGunAccuracy(float pitch, float yaw, float accuracy, Random rand) {
      float randAccPitch = rand.nextFloat() * accuracy;
      float randAccYaw = rand.nextFloat() * accuracy;
      pitch += rand.nextBoolean() ? randAccPitch : -randAccPitch;
      yaw += rand.nextBoolean() ? randAccYaw : -randAccYaw;
      float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
      float f1 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
      float f2 = -MathHelper.cos(-pitch * 0.017453292F);
      float f3 = MathHelper.sin(-pitch * 0.017453292F);
      return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
   }

   public float calculateAccuracyServer(ItemGun item, EntityPlayerMP player, ItemStack stack) {
      GunProperties gun = item.getGun();
      float acc = gun.accuracy;
      if (player.posX != player.lastTickPosX || player.posZ != player.lastTickPosZ) {
         acc += 3.0F;
      }

      if (stack.hasTagCompound()) {
         acc += (float)stack.getTagCompound().getInteger("bloom") / 100.0F;
      }

      if (!player.onGround) {
         acc += 5.0F;
      }

      if (player.isSprinting()) {
         acc += 3.0F;
      }

      if (player.isSneaking()) {
         acc *= 0.7F;
      }

      return acc;
   }

   public ItemStack getItem() {
      return this.item;
   }

   public void superUpdate() {
      super.onUpdate();
   }

   public void onUpdate() {
      this.superUpdate();
      this.updateHeading();
      Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
      Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      RayTraceResult raytraceresult = Left2MineUtilities.RayCastCheckOpaqueDirect(vec3d1, vec3d, this.world, 0);
      vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
      vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      if (raytraceresult != null) {
         vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
      }

      EntityHitVec entHitVec = this.findEntityOnPath(vec3d1, vec3d);
      Entity entity = entHitVec.getEnt();
      if (entity != null) {
         raytraceresult = new RayTraceResult(entity);
      }

      if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)raytraceresult.entityHit;
         if (this.shooter instanceof EntityPlayer && !((EntityPlayer)this.shooter).canAttackPlayer(entityplayer)) {
            raytraceresult = null;
         }
      }

      if (raytraceresult != null && !this.world.isRemote) {
         Vec3d vechit = entHitVec.getHitLoc();
         this.onHit(raytraceresult, vechit);
      }

      this.posX += this.motionX;
      this.posY += this.motionY;
      this.posZ += this.motionZ;
      this.setPosition(this.posX, this.posY, this.posZ);
      if (this.projectile.gravity) {
         this.motionY -= 0.05D;
      }

      if (this.ticksExisted >= this.projectile.life) {
         this.setDead();
      }

   }

   @Nullable
   protected EntityHitVec findEntityOnPath(Vec3d start, Vec3d end) {
      Entity entity = null;
      Vec3d hitVec = null;
      List<Entity> list = Left2MineUtilities.getEntitiesInAABBexcluding(this.world, this, Left2MineUtilities.getEntityCustomBox(this).expand(this.motionX, this.motionY, this.motionZ), ARROW_TARGETS);
      double closestDistance = 0.0D;

      for(int i = 0; i < list.size(); ++i) {
         Entity hitEntity = (Entity)list.get(i);
         if (hitEntity instanceof EntityPlayer) {
            IEquip equip = (IEquip)hitEntity.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            if (hitEntity != this.shooter && !equip.getPinned() && !equip.getLying()) {
               AxisAlignedBB axisalignedbb = Left2MineUtilities.getEntityCustomBox(hitEntity);
               RayTraceResult result = axisalignedbb.calculateIntercept(start, end);
               if (result != null) {
                  hitVec = result.hitVec;
                  double distanceToHit = start.squareDistanceTo(result.hitVec);
                  if (distanceToHit < closestDistance || closestDistance == 0.0D) {
                     entity = hitEntity;
                     closestDistance = distanceToHit;
                  }
               }
            }
         } else if (hitEntity != this.shooter) {
            AxisAlignedBB axisalignedbb = Left2MineUtilities.getEntityCustomBox(hitEntity).grow(0.30000001192092896D);
            RayTraceResult result = axisalignedbb.calculateIntercept(start, end);
            if (result != null) {
               hitVec = result.hitVec;
               double distanceToHit = start.squareDistanceTo(result.hitVec);
               if (distanceToHit < closestDistance || closestDistance == 0.0D) {
                  entity = hitEntity;
                  closestDistance = distanceToHit;
               }
            }
         }
      }

      return new EntityHitVec(entity, hitVec);
   }

   protected void onHit(RayTraceResult raytraceResultIn, Vec3d hitLoc) {
      Entity entity = raytraceResultIn.entityHit;
      if (!(entity instanceof EntityItemLoot)) {
         if (entity != null) {
            if (entity.getEntityId() != this.shooterId) {
               float damage = this.projectile.damage;
               if (this.projectile.damageReduceOverLife) {
                  float percent = ((float)this.projectile.life - (float)this.ticksExisted) / (float)this.projectile.life;
                  damage = this.projectile.damage * percent + this.projectile.damage / (float)this.projectile.life;
               }

               String var29 = this.projectile.type;
               byte var30 = -1;
               switch(var29.hashCode()) {
               case 62970894:
                  if (var29.equals("BASIC")) {
                     var30 = 0;
                  }
                  break;
               case 63789090:
                  if (var29.equals("ADVANCED")) {
                     var30 = 2;
                  }
                  break;
               case 78865936:
                  if (var29.equals("SHELL")) {
                     var30 = 1;
                  }
                  break;
               case 1001953582:
                  if (var29.equals("GRENADE")) {
                     var30 = 3;
                  }
               }

               switch(var30) {
               case 0:
               case 1:
               case 2:
                  entity.hurtResistantTime = 0;
                  if (entity instanceof EntityPlayer) {
                     entity.attackEntityFrom(new DamageSourceShot("shot", this.shooter, this.projectile.power, damage), damage * DifficultyUtil.getFriendlyFireReduction(this.world));
                  } else {
                     if (entity instanceof EntityLivingBase) {
                        EntityLivingBase living = (EntityLivingBase)entity;
                        living.setRevengeTarget(this.shooter);
                     }

                     if (entity instanceof EntityWitch) {
                        EntityWitch witch = (EntityWitch)entity;
                        Iterator var34;
                        EntityPlayer players;
                        if (this.projectile.type.equals("SHELL")) {
                           if (this.shooter.getDistance(witch) < 3.0F) {
                              if (witch.getAttackingState() == 0) {
                                 if (hitLoc.y > entity.posY + (double)entity.height * 0.775D) {
                                    damage *= 12.0F;
                                 } else {
                                    witch.setAngerLevel(2000);
                                 }
                              }
                           } else if (witch.getAttackingState() == 0) {
                              var34 = witch.getEntityWorld().playerEntities.iterator();

                              while(var34.hasNext()) {
                                 players = (EntityPlayer)var34.next();
                                 players.sendMessage(new TextComponentTranslation("message.witch.startled", new Object[]{this.shooter.getName()}));
                              }

                              witch.setAttackTarget(this.shooter);
                              witch.setAttackingState(1);
                              Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(witch, 18));
                           }
                        } else if (witch.getAttackingState() == 0) {
                           var34 = witch.getEntityWorld().playerEntities.iterator();

                           while(var34.hasNext()) {
                              players = (EntityPlayer)var34.next();
                              players.sendMessage(new TextComponentTranslation("message.witch.startled", new Object[]{this.shooter.getName()}));
                           }

                           witch.setAttackTarget(this.shooter);
                           witch.setAttackingState(1);
                           Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(witch, 18));
                        }
                     }

                     if (entity instanceof EntityCommonInfected) {
                        if (hitLoc.y > entity.posY + (double)entity.height * 0.775D) {
                           damage *= 2.0F;
                        } else {
                           damage *= DifficultyUtil.getDamageMultiplier(this.world);
                        }
                     }

                     if (this.shooter instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer)this.shooter;
                        int damageInt = Math.round(damage);
                        IStats stats;
                        if (entity instanceof EntityTank) {
                           stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                           stats.setTankDamage(stats.getTankDamage() + damageInt);
                           Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.TANKDAMAGE, stats.getTankDamage(), player));
                        }

                        if (entity instanceof EntityWitch) {
                           stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                           stats.setWitchDamage(stats.getWitchDamage() + damageInt);
                           Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.WITCHDAMAGE, stats.getWitchDamage(), player));
                        }
                     }

                     entity.attackEntityFrom(new DamageSourceShot("shot", this.shooter, this.projectile.power, damage), damage);
                  }
                  break;
               case 3:
                  this.world.createExplosion(this.shooter, raytraceResultIn.hitVec.x, raytraceResultIn.hitVec.y, raytraceResultIn.hitVec.z, 5.0F, true);
               }

               this.setDead();
            }
         } else {
            if (raytraceResultIn.getBlockPos() != null) {
               BlockPos pos = raytraceResultIn.getBlockPos();
               IBlockState state = this.world.getBlockState(pos);
               Block block = state.getBlock();
               if (!block.isReplaceable(this.world, raytraceResultIn.getBlockPos())) {
                  this.setDead();
               }

               EntityDecal.EnumDecalSide side = EntityDecal.EnumDecalSide.ALL;
               boolean render = false;
               double hitX = raytraceResultIn.hitVec.x;
               double hitY = raytraceResultIn.hitVec.y;
               double hitZ = raytraceResultIn.hitVec.z;
               double midX = (double)pos.getX() + 0.5D;
               double midY = (double)pos.getY() + 0.5D;
               double midZ = (double)pos.getZ() + 0.5D;
               double diffX = hitX - midX;
               double diffY = hitY - midY;
               double diffZ = hitZ - midZ;
               if (diffX == 0.0D) {
                  if (this.shooter.posX < hitX) {
                     hitX -= 0.5D;
                     diffX -= 0.5D;
                  } else {
                     hitX += 0.5D;
                     diffX += 0.5D;
                  }
               }

               if (diffY == 0.0D) {
                  hitY += 0.5D;
                  diffY += 0.5D;
               }

               if (diffZ == 0.0D) {
                  if (this.shooter.posZ < hitZ) {
                     hitZ -= 0.5D;
                     diffZ -= 0.5D;
                  } else {
                     hitZ += 0.5D;
                     diffZ += 0.5D;
                  }
               }

               if (diffX == -0.5D) {
                  side = EntityDecal.EnumDecalSide.EAST;
                  render = true;
               }

               if (diffX == 0.5D) {
                  side = EntityDecal.EnumDecalSide.WEST;
                  render = true;
               }

               if (diffZ == -0.5D) {
                  side = EntityDecal.EnumDecalSide.SOUTH;
                  render = true;
               }

               if (diffZ == 0.5D) {
                  side = EntityDecal.EnumDecalSide.NORTH;
                  render = true;
               }

               if (diffY == 0.5D) {
                  side = EntityDecal.EnumDecalSide.FLOOR;
                  render = true;
               }

               if (render) {
                  Left2MinePacket.INSTANCE.sendToAll(new DecalMessage(0, side, hitX, hitY + 0.095D, hitZ, false));
               }

               if (this.projectile.type == "SHELL") {
                  this.world.playSound((EntityPlayer)null, pos, InitSounds.ricochet, SoundCategory.PLAYERS, 0.15F, 1.0F);
               } else {
                  this.world.playSound((EntityPlayer)null, pos, InitSounds.ricochet, SoundCategory.PLAYERS, 0.75F, 1.0F);
               }
            }

         }
      }
   }

   public boolean shouldRenderInPass(int pass) {
      return this.projectile.visible;
   }

   protected void entityInit() {
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      this.projectile = new ProjectileProperties();
      this.projectile.deserializeNBT(compound.getCompoundTag("projectile"));
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      compound.setTag("projectile", this.projectile.serializeNBT());
   }

   public void writeSpawnData(ByteBuf buffer) {
      ByteBufUtils.writeTag(buffer, this.projectile.serializeNBT());
      buffer.writeInt(this.shooterId);
      buffer.writeFloat(this.rotationYaw);
      buffer.writeFloat(this.rotationPitch);
      ByteBufUtils.writeItemStack(buffer, this.item);
   }

   public void readSpawnData(ByteBuf additionalData) {
      if (this.projectile == null) {
         this.projectile = new ProjectileProperties();
      }

      this.projectile.deserializeNBT(ByteBufUtils.readTag(additionalData));
      this.shooterId = additionalData.readInt();
      this.rotationYaw = additionalData.readFloat();
      this.prevRotationYaw = this.rotationYaw;
      this.rotationPitch = additionalData.readFloat();
      this.prevRotationPitch = this.rotationPitch;
      this.item = ByteBufUtils.readItemStack(additionalData);
   }

   public void updateHeading() {
      float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 57.29577951308232D);
      this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * 57.29577951308232D);
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
   }

   public ProjectileProperties getProjectile() {
      return this.projectile;
   }

   static {
      ARROW_TARGETS = Predicates.and(new Predicate[]{EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, (entity) -> {
         return entity.canBeCollidedWith();
      }, isNotItem()});
   }
}
