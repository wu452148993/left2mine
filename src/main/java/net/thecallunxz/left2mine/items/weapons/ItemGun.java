package net.thecallunxz.left2mine.items.weapons;

import com.google.common.collect.Multimap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.entities.mobs.ITriggerPush;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;
import net.thecallunxz.left2mine.init.InitKeybinding;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.items.ItemBase;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;
import net.thecallunxz.left2mine.items.weapons.properties.ProjectileProperties;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.networking.server.ReloadMessage;
import net.thecallunxz.left2mine.networking.server.ShootMessage;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public abstract class ItemGun extends ItemBase {
   private GunProperties gun;
   private ProjectileProperties projectile;
   private long clientNextShot;

   public ItemGun(String name, ItemBase.EnumItemType type, GunProperties gun, ProjectileProperties projectile) {
      super(name, type);
      this.gun = gun;
      this.projectile = projectile;
      this.clientNextShot = 0L;
      this.setMaxStackSize(1);
   }

   public GunProperties getGun() {
      return this.gun;
   }

   public ProjectileProperties getProjectile() {
      return this.projectile;
   }

   public void setClientNextShot(long next) {
      this.clientNextShot = next;
   }

   public boolean isReloading(World world, NBTTagCompound nbt) {
      return nbt.getLong("reloadTime") >= world.getTotalWorldTime();
   }

   public boolean lastReloadTick(World world, NBTTagCompound nbt) {
      return nbt.getLong("reloadTime") == world.getTotalWorldTime();
   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (entityIn instanceof EntityPlayer) {
         NBTTagCompound nbt;
         if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
            nbt = stack.getTagCompound();
            nbt.setInteger("ammo", this.getGun().magazineSize);
            nbt.setInteger("storage", this.getGun().storageSize);
            nbt.setLong("reloadTime", 0L);
            nbt.setLong("nextShot", 0L);
            nbt.setInteger("bloom", 0);
            nbt.setBoolean("justShot", false);
            stack.setTagCompound(nbt);
         }

         EntityPlayer player;
         if (worldIn.isRemote && isSelected && !((EntityPlayer)entityIn).isSpectator()) {
            nbt = stack.getTagCompound();
            if (this.getGun().auto && Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown()) {
               this.fire(stack, (EntityPlayer)entityIn);
            }

            if (InitKeybinding.reload.isPressed()) {
               player = (EntityPlayer)entityIn;
               Left2MinePacket.INSTANCE.sendToServer(new ReloadMessage(player.inventory.currentItem));
            }
         }

         if (!worldIn.isRemote) {
            nbt = stack.getTagCompound();
            if (this.isReloading(worldIn, nbt) && isSelected && this.lastReloadTick(worldIn, nbt)) {
               int tempAmmo;
               int tempStorage;
               if (!this.gun.shellreload) {
                  if (this.getGun().infinite) {
                     nbt.setInteger("ammo", this.getGun().magazineSize);
                  } else {
                     tempStorage = nbt.getInteger("storage");
                     tempAmmo = nbt.getInteger("ammo");
                     int newStorage = tempStorage + tempAmmo;
                     if (newStorage > this.getGun().magazineSize) {
                        nbt.setInteger("ammo", this.getGun().magazineSize);
                        nbt.setInteger("storage", newStorage - this.getGun().magazineSize);
                     } else {
                        nbt.setInteger("ammo", newStorage);
                        nbt.setInteger("storage", 0);
                     }
                  }
               } else {
                  tempStorage = nbt.getInteger("storage");
                  tempAmmo = nbt.getInteger("ammo");
                  EntityPlayer player = (EntityPlayer)entityIn;
                  if (tempStorage > 0 && tempAmmo < this.gun.magazineSize) {
                     Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(player, 2));
                  } else {
                     Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(player, 3));
                  }

                  if (this.getGun().infinite) {
                     nbt.setInteger("ammo", tempAmmo + 1);
                     if (tempAmmo + 1 < this.gun.magazineSize) {
                        nbt.setLong("reloadTime", worldIn.getTotalWorldTime() + (long)this.getGun().maxReloadTime);
                     }
                  } else if (tempStorage > 0 && tempAmmo < this.gun.magazineSize) {
                     nbt.setLong("reloadTime", worldIn.getTotalWorldTime() + (long)this.getGun().maxReloadTime);
                     nbt.setInteger("ammo", tempAmmo + 1);
                     nbt.setInteger("storage", tempStorage - 1);
                  }
               }
            }

            if (nbt.getInteger("bloom") > 0) {
               if (this.getGun().doubleBloomSub) {
                  player = (EntityPlayer)entityIn;
                  if (player.getEntityWorld().getTotalWorldTime() > this.clientNextShot) {
                     nbt.setInteger("bloom", Math.max(0, nbt.getInteger("bloom") - this.getGun().bloomSubtract * 2));
                  } else {
                     nbt.setInteger("bloom", Math.max(0, nbt.getInteger("bloom") - this.getGun().bloomSubtract));
                  }
               } else {
                  nbt.setInteger("bloom", Math.max(0, nbt.getInteger("bloom") - this.getGun().bloomSubtract));
               }
            }

            if (nbt.getBoolean("justShot")) {
               nbt.setBoolean("justShot", false);
            }

            stack.setTagCompound(nbt);
         }
      }

   }

   public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
      if (entityLiving.isSwingInProgress) {
         return true;
      } else {
         if (!this.getGun().auto && entityLiving.world.isRemote) {
            this.fire(stack, (EntityPlayer)entityLiving);
         }

         return true;
      }
   }

   public void fire(ItemStack stack, EntityLivingBase entityLiving) {
      NBTTagCompound nbt = stack.getTagCompound();
      EntityPlayer player = (EntityPlayer)entityLiving;
      IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
      if ((!player.isPotionActive(InitPotions.incapacitated) || this.gun.infinite) && !Left2MineUtilities.isPinnedPotionActive(player) && !player.isSwingInProgress && !player.isSpectator()) {
         if (nbt.getInteger("ammo") > 0) {
            if (!this.gun.shellreload) {
               if (!this.isReloading(player.getEntityWorld(), nbt) && player.getEntityWorld().getTotalWorldTime() > this.clientNextShot) {
                  this.clientNextShot = player.getEntityWorld().getTotalWorldTime() + (long)this.gun.maxFireCooldown;
                  player.rotationPitch -= this.gun.recoil;
                  player.world.playSound(player, player.getPosition(), this.gun.fireSound, SoundCategory.PLAYERS, 0.5F, 1.0F);
                  Left2MinePacket.INSTANCE.sendToServer(new ShootMessage(player.inventory.currentItem, player.getEntityWorld().getTotalWorldTime()));
               }
            } else if (player.getEntityWorld().getTotalWorldTime() > this.clientNextShot) {
               this.clientNextShot = player.getEntityWorld().getTotalWorldTime() + (long)this.gun.maxFireCooldown;
               player.rotationPitch -= this.gun.recoil;
               player.world.playSound(player, player.getPosition(), this.gun.fireSound, SoundCategory.PLAYERS, 0.5F, 1.0F);
               Left2MinePacket.INSTANCE.sendToServer(new ShootMessage(player.inventory.currentItem, player.getEntityWorld().getTotalWorldTime()));
            }
         } else {
            Left2MinePacket.INSTANCE.sendToServer(new ReloadMessage(player.inventory.currentItem));
         }
      }

   }

   public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
      return true;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      if (super.onItemRightClick(worldIn, playerIn, handIn).getType() != EnumActionResult.SUCCESS) {
         return new ActionResult(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
      } else {
         if (!Left2MineUtilities.isPinnedPotionActive(playerIn) && !playerIn.isPotionActive(InitPotions.incapacitated)) {
            playerIn.isSwingInProgress = true;
            playerIn.swingProgressInt = -1;
            List<EntityLivingBase> mobs = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, playerIn.getEntityBoundingBox().grow(2.0D, 2.0D, 2.0D));
            Iterator var5 = mobs.iterator();

            label71:
            while(true) {
               EntityLivingBase mob;
               label69:
               do {
                  while(var5.hasNext()) {
                     mob = (EntityLivingBase)var5.next();
                     if (!(mob instanceof EntityPlayer)) {
                        continue label69;
                     }

                     if (playerIn.getEntityId() != mob.getEntityId() && (Left2MineUtilities.canSeeHigh(playerIn, mob) || Left2MineUtilities.canSeeLow(playerIn, mob)) && mob.isPotionActive(InitPotions.smoker_tongued)) {
                        EntityPlayer player2 = (EntityPlayer)mob;
                        IEquip equip = (IEquip)player2.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
                        Entity ent = Left2MineUtilities.getEntityByUUID(player2.world, equip.getTetheredUUID());
                        if (ent != null && ent instanceof EntitySmoker) {
                           EntitySmoker smoker = (EntitySmoker)ent;
                           if (smoker.isSmoked()) {
                              EntityPlayer victim = smoker.getEntityWorld().getPlayerEntityByUUID(smoker.getSmokedId());
                              if (victim != null) {
                                 smoker.setRechargeTime(smoker.world.getTotalWorldTime() + 200L);
                                 victim.removePotionEffect(InitPotions.smoker_tongued);
                              }
                           }

                           smoker.setSeenTicks(0);
                           smoker.setSmoked(false);
                        }
                     }
                  }

                  for(int i = 0; i < 5; ++i) {
                     ItemStack stack = playerIn.inventory.getStackInSlot(i);
                     if (stack.getItem() instanceof ItemGun && !playerIn.isCreative()) {
                        playerIn.getCooldownTracker().setCooldown(stack.getItem(), 5);
                     }
                  }
                  break label71;
               } while(!Left2MineUtilities.canSeeHigh(playerIn, mob) && !Left2MineUtilities.canSeeLow(playerIn, mob));

               if (mob instanceof ITriggerPush) {
                  if (((ITriggerPush)mob).onPush()) {
                     Left2MineUtilities.knockBack(mob, playerIn, 1.0F);
                     mob.attackEntityFrom(DamageSource.WITHER, 0.001F);
                     mob.setRevengeTarget(playerIn);
                     mob.hurtResistantTime = 0;
                     mob.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10, 5, true, false));
                  }
               } else {
                  Left2MineUtilities.knockBack(mob, playerIn, 1.0F);
                  mob.attackEntityFrom(DamageSource.WITHER, 0.001F);
                  mob.setRevengeTarget(playerIn);
                  mob.hurtResistantTime = 0;
                  mob.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10, 5, true, false));
               }
            }
         }

         return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
      }
   }

   public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
      return true;
   }

   public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
      if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", Double.MAX_VALUE, 0));
      }

      return multimap;
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return oldStack.getItem() != newStack.getItem();
   }
}
