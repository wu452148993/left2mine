package net.thecallunxz.left2mine.events;

import com.google.common.base.Predicate;
import java.util.Iterator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.nodes.BlockNodeParent;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeParent;
import net.thecallunxz.left2mine.capabilities.CapabilityStats;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.capabilities.IStats;
import net.thecallunxz.left2mine.capabilities.StatsEnum;
import net.thecallunxz.left2mine.capabilities.StatsProvider;
import net.thecallunxz.left2mine.entities.EntityItemLoot;
import net.thecallunxz.left2mine.entities.decals.EntityDecal;
import net.thecallunxz.left2mine.entities.mobs.EntityBossInfected;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;
import net.thecallunxz.left2mine.entities.mobs.EntitySpecialInfected;
import net.thecallunxz.left2mine.entities.mobs.IRagdollEntities;
import net.thecallunxz.left2mine.entities.mobs.ITriggerDeath;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityBoomer;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityTank;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.entities.projectiles.DamageSourceShot;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.items.ItemBase;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.BoomerCorpseMessage;
import net.thecallunxz.left2mine.networking.client.CorpseGunMessage;
import net.thecallunxz.left2mine.networking.client.DecalMessage;
import net.thecallunxz.left2mine.networking.client.EquippedMessage;
import net.thecallunxz.left2mine.networking.client.HunterCorpseMessage;
import net.thecallunxz.left2mine.networking.client.HurtArrowMessage;
import net.thecallunxz.left2mine.networking.client.PlayerDeathMessage;
import net.thecallunxz.left2mine.networking.client.ShaderMessage;
import net.thecallunxz.left2mine.networking.client.SmokerCorpseMessage;
import net.thecallunxz.left2mine.networking.client.StatsMessage;
import net.thecallunxz.left2mine.networking.client.SurvivalUpdateMessage;
import net.thecallunxz.left2mine.networking.client.TankCorpseMessage;
import net.thecallunxz.left2mine.networking.client.WitchCorpseMessage;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class CommonEventHandler {
   @SubscribeEvent
   public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof EntityPlayer) {
         event.addCapability(new ResourceLocation("left2mine", "equipped"), new EquipProvider());
         event.addCapability(new ResourceLocation("left2mine", "stats"), new StatsProvider());
      }
   }

   @SubscribeEvent
   public void clonePlayerEvent(Clone event) {
      if (event.isWasDeath()) {
         IStats statsOrig = (IStats)event.getOriginal().getCapability(StatsProvider.STATS, (EnumFacing)null);
         IStats statsNew = (IStats)event.getEntityPlayer().getCapability(StatsProvider.STATS, (EnumFacing)null);
         CapabilityStats.syncStats(statsOrig, statsNew, event.getEntityPlayer());
      }

   }

   @SubscribeEvent
   public void respawnPlayerEvent(PlayerRespawnEvent event) {
      if (!event.isEndConquered()) {
         IStats stats = (IStats)event.player.getCapability(StatsProvider.STATS, (EnumFacing)null);
         Left2MinePacket.INSTANCE.sendToAll(new PlayerDeathMessage(event.player.getDisplayNameString(), 0));
         stats.setDeaths(stats.getDeaths() + 1);
         CapabilityStats.updateStats(stats, event.player);
         if (event.player instanceof EntityPlayerMP) {
            WorldDataLeft2Mine data = WorldDataLeft2Mine.get(event.player.getEntityWorld());
            EntityPlayerMP player = (EntityPlayerMP)event.player;
            IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            equip.setLives(3);
            Left2MinePacket.INSTANCE.sendTo(new ShaderMessage(0), player);
            if (player.inventory.isEmpty() && data.isInGame() && player.isSpectator()) {
               Predicate<Entity> predicate = EntitySelectors.CAN_AI_TARGET;
               EntityPlayer closest = event.player.getEntityWorld().getClosestPlayer(player.posX, player.posY, player.posZ, Double.MAX_VALUE, predicate);
               if (closest != null) {
                  player.setPositionAndUpdate(closest.posX, closest.posY, closest.posZ);
                  player.setSpectatingEntity(closest);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onEntityInteract(EntityInteractSpecific event) {
      if (event.getTarget() instanceof EntityItemLoot) {
         if (!event.getWorld().isRemote && event.getTarget().onGround && !event.getEntityPlayer().isSpectator()) {
            EntityItemLoot loot = (EntityItemLoot)event.getTarget();
            if (loot.getCustomAge() > 20) {
               ItemStack stack = loot.getItem();
               ItemBase item = (ItemBase)loot.getItem().getItem();
               InventoryPlayer inventory = event.getEntityPlayer().inventory;
               int slot = item.getSlot(item.getItemType());
               if (inventory.getStackInSlot(slot).getItem() != Items.AIR) {
                  if (ItemStack.areItemStacksEqual(inventory.getStackInSlot(slot), stack)) {
                     return;
                  }

                  event.getEntityPlayer().dropItem(inventory.getStackInSlot(slot), false);
               }

               if (stack.getItem() != Items.AIR && event.getTarget().onGround) {
                  loot.pickup(event.getEntityPlayer());
                  IEquip equip = (IEquip)event.getEntityPlayer().getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
                  equip.addEquipped(stack);
                  Left2MinePacket.INSTANCE.sendToAll(new EquippedMessage(equip.getEquippedList(), event.getEntityPlayer()));
                  event.getWorld().playSound((EntityPlayer)null, event.getPos(), InitSounds.item_pickup, SoundCategory.PLAYERS, 0.5F, 1.0F);
               }
            }
         }

         event.setCanceled(true);
         event.setCancellationResult(EnumActionResult.SUCCESS);
      } else if (event.getTarget() instanceof EntityPlayerMP) {
         if (!event.getWorld().isRemote) {
            EntityPlayerMP player = (EntityPlayerMP)event.getTarget();
            IEquip equipPlayer = (IEquip)event.getEntityPlayer().getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            IEquip equipPlayer2 = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            if (!equipPlayer.getLying() && equipPlayer2.getLying() && !equipPlayer.getPinned() && !equipPlayer2.getPinned()) {
               int seenBy = 0;
               Iterator var6 = event.getWorld().getEntitiesWithinAABB(Entity.class, player.getEntityBoundingBox().grow(3.0D)).iterator();

               label68: {
                  Entity entity;
                  do {
                     do {
                        if (!var6.hasNext()) {
                           break label68;
                        }

                        entity = (Entity)var6.next();
                     } while(!(entity instanceof EntityCommonInfected));
                  } while(((EntityCommonInfected)entity).getAttackTarget() != event.getEntityPlayer() && ((EntityCommonInfected)entity).getAttackTarget() != player);

                  ++seenBy;
               }

               if (seenBy == 0) {
                  player.removePotionEffect(InitPotions.incapacitated);
                  var6 = event.getWorld().playerEntities.iterator();

                  while(var6.hasNext()) {
                     EntityPlayer players = (EntityPlayer)var6.next();
                     players.sendMessage(new TextComponentTranslation("message.player.helpsuccess", new Object[]{event.getEntityPlayer().getName(), player.getName()}));
                  }
               } else {
                  event.getEntityPlayer().sendStatusMessage((new TextComponentTranslation("message.player.helpfail", new Object[]{player.getName()})).setStyle((new Style()).setColor(TextFormatting.RED)), true);
               }
            }
         }

         event.setCanceled(true);
         event.setCancellationResult(EnumActionResult.SUCCESS);
      }
   }

   @SubscribeEvent
   public void onRightClickBlock(RightClickBlock event) {
      IBlockState state = event.getWorld().getBlockState(event.getPos());
      if (state.getBlock() instanceof BlockContainer && !event.getEntityPlayer().capabilities.allowEdit) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void onEntityJoin(EntityJoinWorldEvent event) {
      if (!event.getWorld().isRemote) {
         WorldDataLeft2Mine data = WorldDataLeft2Mine.get(event.getWorld());
         if (event.getEntity().getClass() == EntityItem.class) {
            EntityItem item = (EntityItem)event.getEntity();
            if (!item.getItem().isEmpty() && item.getItem().getItem() instanceof ItemBase && data.isInGame()) {
               EntityItemLoot loot = new EntityItemLoot((EntityItem)event.getEntity());
               event.getEntity().setDead();
               loot.setInfinitePickupDelay();
               event.setResult(Result.DENY);
               event.setCanceled(true);
               event.getWorld().spawnEntity(loot);
               return;
            }
         }

         if (event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)event.getEntity();
            IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            if (equip.getLives() <= 1) {
               Left2MinePacket.INSTANCE.sendTo(new ShaderMessage(1), player);
            }

            if (data.isSurvivalInGame()) {
               Left2MinePacket.INSTANCE.sendTo(new SurvivalUpdateMessage(0, data.getGameStartTime(), data.getBestSurvivalTime()), player);
            }

            if (data.hasSurvivalStarted()) {
               Left2MinePacket.INSTANCE.sendTo(new SurvivalUpdateMessage(2, data.getGameStartTime(), data.getBestSurvivalTime()), player);
            }

            if (player.inventory.isEmpty() && data.isInGame() && player.isSpectator()) {
               Predicate<Entity> predicate = EntitySelectors.CAN_AI_TARGET;
               EntityPlayer closest = event.getEntity().getEntityWorld().getClosestPlayer(player.posX, player.posY, player.posZ, Double.MAX_VALUE, predicate);
               if (closest != null) {
                  player.setPositionAndUpdate(closest.posX, closest.posY, closest.posZ);
                  player.setSpectatingEntity(closest);
               }
            }
         }

      }
   }

   @SubscribeEvent
   public void onThrowItem(ItemTossEvent event) {
      if (event.getEntityItem().getItem().getItem() instanceof ItemBase) {
         EntityPlayer player = event.getPlayer();
         if (player != null) {
            IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            equip.removeEquipped(event.getEntityItem().getItem());
            if (!player.getEntityWorld().isRemote) {
               Left2MinePacket.INSTANCE.sendToAll(new EquippedMessage(equip.getEquippedList(), player));
            }
         }
      }

   }

   @SubscribeEvent
   public void onAttack(LivingAttackEvent event) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(event.getEntity().getEntityWorld());
      if (event.getSource() instanceof EntityDamageSource && event.getSource().getTrueSource() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getSource().getTrueSource();
         if (data.isInGame() || !player.capabilities.allowEdit) {
            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void onLivingDeath(LivingDeathEvent event) {
      EntityPlayer player;
      if (event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         if (!player.isPotionActive(InitPotions.incapacitated) && equip.getLives() > 1) {
            BlockPos highPos = Left2MineUtilities.getHighestBelow(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), player.getEntityWorld());
            if (highPos != null) {
               IStats stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
               stats.setIncapacitations(stats.getIncapacitations() + 1);
               Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.INCAPACITATIONS, stats.getIncapacitations(), player));
               player.addPotionEffect(new PotionEffect(InitPotions.incapacitated, 9999, 0, true, false));
               if (!player.getEntityWorld().isRemote) {
                  player.ticksExisted = 1;
               }

               if (player.posY > (double)highPos.getY()) {
                  player.setPositionAndUpdate(player.posX, (double)highPos.getY(), player.posZ);
               } else {
                  player.setPositionAndUpdate(player.posX, player.posY, player.posZ);
               }

               Iterator var20 = event.getEntity().getEntityWorld().playerEntities.iterator();

               while(var20.hasNext()) {
                  player = (EntityPlayer)var20.next();
                  player.sendMessage(new TextComponentTranslation("message.player.incapacitated", new Object[]{player.getName()}));
               }

               WorldDataLeft2Mine data = WorldDataLeft2Mine.get(event.getEntity().getEntityWorld());
               if (data.isInGame() && Left2MineUtilities.checkGameOver(event.getEntity().getEntityWorld(), (EntityPlayer)event.getEntity())) {
                  Left2MineUtilities.startGameOver(event.getEntity().getEntityWorld());
               }

               event.setCanceled(true);
               return;
            }
         }
      }

      if (!event.getEntity().getEntityWorld().isRemote) {
         Entity entity = event.getEntity();
         double offset = entity.getEntityWorld().rand.nextDouble() - 0.2D;
         Left2MinePacket.INSTANCE.sendToAll(new DecalMessage(1, EntityDecal.EnumDecalSide.ALL, entity.lastTickPosX - entity.motionX, entity.lastTickPosY - entity.motionY, entity.lastTickPosZ - entity.motionZ, false));
         Left2MinePacket.INSTANCE.sendToAll(new DecalMessage(1, EntityDecal.EnumDecalSide.WALLS, entity.lastTickPosX - entity.motionX, entity.lastTickPosY + (double)entity.getEyeHeight() - offset - entity.motionY, entity.lastTickPosZ - entity.motionZ, false));
         WorldDataLeft2Mine data = WorldDataLeft2Mine.get(event.getEntity().getEntityWorld());
         DamageSourceShot damageSource;
         IStats stats;
         EntityPlayer player;
         IStats stats;
         if (event.getEntity() instanceof EntityCommonInfected) {
            if (event.getSource() instanceof DamageSourceShot) {
               damageSource = (DamageSourceShot)event.getSource();
               if (damageSource.getShooter() instanceof EntityPlayer) {
                  player = (EntityPlayer)damageSource.getShooter();
                  stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                  stats.setCommonKilled(stats.getCommonKilled() + 1);
                  Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.COMMONKILLED, stats.getCommonKilled(), player));
               }
            } else if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
               player = (EntityPlayer)event.getSource().getTrueSource();
               stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
               stats.setCommonKilled(stats.getCommonKilled() + 1);
               Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.COMMONKILLED, stats.getCommonKilled(), player));
            }

            EntityCommonInfected infected = (EntityCommonInfected)event.getEntity();
            if (infected.isPanic()) {
               data.setHordeCount(data.getHordeCount() - 1);
            } else {
               data.setWanderingCount(data.getWanderingCount() - 1);
            }
         }

         if (event.getEntity() instanceof EntityHunter) {
            if (event.getSource() instanceof DamageSourceShot) {
               damageSource = (DamageSourceShot)event.getSource();
               if (damageSource.getShooter() instanceof EntityPlayer) {
                  player = (EntityPlayer)damageSource.getShooter();
                  stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                  stats.setHuntersKilled(stats.getHuntersKilled() + 1);
                  Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.HUNTERSKILLED, stats.getHuntersKilled(), player));
               }
            } else if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
               player = (EntityPlayer)event.getSource().getTrueSource();
               stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
               stats.setHuntersKilled(stats.getHuntersKilled() + 1);
               Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.HUNTERSKILLED, stats.getHuntersKilled(), player));
            }
         }

         if (event.getEntity() instanceof EntityBoomer) {
            if (event.getSource() instanceof DamageSourceShot) {
               damageSource = (DamageSourceShot)event.getSource();
               if (damageSource.getShooter() instanceof EntityPlayer) {
                  player = (EntityPlayer)damageSource.getShooter();
                  stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                  stats.setBoomersKilled(stats.getBoomersKilled() + 1);
                  Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.BOOMERSKILLED, stats.getBoomersKilled(), player));
               }
            } else if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
               player = (EntityPlayer)event.getSource().getTrueSource();
               stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
               stats.setBoomersKilled(stats.getBoomersKilled() + 1);
               Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.BOOMERSKILLED, stats.getBoomersKilled(), player));
            }
         }

         if (event.getEntity() instanceof EntitySmoker) {
            if (event.getSource() instanceof DamageSourceShot) {
               damageSource = (DamageSourceShot)event.getSource();
               if (damageSource.getShooter() instanceof EntityPlayer) {
                  player = (EntityPlayer)damageSource.getShooter();
                  stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                  stats.setSmokersKilled(stats.getSmokersKilled() + 1);
                  Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.SMOKERSKILLED, stats.getSmokersKilled(), player));
               }
            } else if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
               player = (EntityPlayer)event.getSource().getTrueSource();
               stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
               stats.setSmokersKilled(stats.getSmokersKilled() + 1);
               Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.SMOKERSKILLED, stats.getSmokersKilled(), player));
            }
         }

         if (event.getEntity() instanceof EntitySpecialInfected) {
            data.setSpecialCount(data.getSpecialCount() - 1);
         }

         if (event.getEntity() instanceof EntityBossInfected) {
            data.setBossCount(data.getBossCount() - 1);
         }

         if (event.getEntity() instanceof EntityPlayer) {
            IEquip equip = (IEquip)event.getEntity().getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            equip.clearEquipped();
            Left2MinePacket.INSTANCE.sendToAll(new EquippedMessage(equip.getEquippedList(), (EntityPlayer)event.getEntity()));
            if (data.isInGame()) {
               ((EntityPlayer)event.getEntity()).inventory.dropAllItems();
               ((EntityPlayer)event.getEntity()).setGameType(GameType.SPECTATOR);
               if (Left2MineUtilities.checkGameOver(event.getEntity().getEntityWorld(), (EntityPlayer)event.getEntity())) {
                  Left2MineUtilities.startGameOver(event.getEntity().getEntityWorld());
               }
            }
         }

         if (event.getEntity() instanceof IRagdollEntities) {
            long timeKilled = event.getEntity().getEntityWorld().getTotalWorldTime();
            if (event.getSource() != null) {
               Vec3d damageLoc = event.getSource().getDamageLocation();
               if (event.getSource() instanceof DamageSourceShot) {
                  DamageSourceShot lastDamage = (DamageSourceShot)event.getSource();
                  this.spawnCorpseGun(damageLoc, event.getEntity(), lastDamage.getPower(), lastDamage.getDamage(), timeKilled);
               } else if (event.getSource().getDamageLocation() != null) {
                  this.spawnCorpseGun(damageLoc, event.getEntity(), 1.0F, 10.0F, timeKilled);
               } else if (((EntityLiving)event.getEntity()).getAttackTarget() != null) {
                  this.spawnCorpseGun(((EntityLiving)event.getEntity()).getAttackTarget().getPositionVector(), event.getEntity(), 1.0F, 10.0F, timeKilled);
               } else {
                  this.spawnCorpseGun(event.getEntity().getPositionVector().add(event.getEntity().getLookVec().scale(-1.0D)), event.getEntity(), 1.0F, 10.0F, timeKilled);
               }
            } else if (((EntityLiving)event.getEntity()).getAttackTarget() != null) {
               this.spawnCorpseGun(((EntityLiving)event.getEntity()).getAttackTarget().getPositionVector(), event.getEntity(), 1.0F, 10.0F, timeKilled);
            } else {
               this.spawnCorpseGun(event.getEntity().getPositionVector().add(event.getEntity().getLookVec().scale(-1.0D)), event.getEntity(), 1.0F, 10.0F, timeKilled);
            }
         }

         if (event.getEntity() instanceof ITriggerDeath && !event.getEntity().isDead) {
            ITriggerDeath deadMob = (ITriggerDeath)event.getEntity();
            deadMob.onDeath();
         }
      }

   }

   private void spawnCorpseGun(Vec3d damageLoc, Entity entity, float power, float damage, long timeKilled) {
      if (entity instanceof EntityCommonInfected) {
         Left2MinePacket.INSTANCE.sendToDimension(new CorpseGunMessage(damageLoc.x, damageLoc.y, damageLoc.z, entity, power, damage, timeKilled), entity.dimension);
      } else if (entity instanceof EntityHunter) {
         EntityHunter hunt = (EntityHunter)entity;
         Left2MinePacket.INSTANCE.sendToDimension(new HunterCorpseMessage(damageLoc.x, damageLoc.y, damageLoc.z, hunt, power, damage, timeKilled, hunt.isPouncing() || hunt.isSneaking() || hunt.isPinned()), entity.dimension);
      } else if (entity instanceof EntitySmoker) {
         Left2MinePacket.INSTANCE.sendToDimension(new SmokerCorpseMessage(damageLoc.x, damageLoc.y, damageLoc.z, entity, power, damage, timeKilled), entity.dimension);
      } else if (entity instanceof EntityBoomer) {
         Left2MinePacket.INSTANCE.sendToDimension(new BoomerCorpseMessage(damageLoc.x, damageLoc.y, damageLoc.z, entity, power, damage, timeKilled), entity.dimension);
      } else if (entity instanceof EntityTank) {
         Left2MinePacket.INSTANCE.sendToDimension(new TankCorpseMessage(damageLoc.x, damageLoc.y, damageLoc.z, entity, power, damage, timeKilled), entity.dimension);
      } else if (entity instanceof EntityWitch) {
         EntityWitch witch = (EntityWitch)entity;
         Left2MinePacket.INSTANCE.sendToDimension(new WitchCorpseMessage(damageLoc.x, damageLoc.y, damageLoc.z, entity, power, damage, timeKilled, witch.getAttackingState() == 0), entity.dimension);
      }

   }

   @SubscribeEvent
   public void onLivingFall(LivingFallEvent event) {
      if (event.getEntity() instanceof IRagdollEntities) {
         event.setCanceled(true);
      } else {
         if (event.getEntity() instanceof EntityPlayer) {
            if (event.getDistance() <= 4.0F) {
               event.setCanceled(true);
               return;
            }

            if (event.getDistance() >= 10.0F) {
               event.setDamageMultiplier(100.0F);
               event.getEntityLiving().addPotionEffect(new PotionEffect(InitPotions.incapacitated, 9999, 0, true, false));
            }

            event.setDamageMultiplier(event.getDistance() - 2.0F);
         }

      }
   }

   @SubscribeEvent
   public void onLivingHurt(LivingHurtEvent event) {
      if (!(event.getEntity() instanceof EntityItemLoot)) {
         Entity entity;
         if (event.getEntity() instanceof EntityPlayer) {
            if (event.getSource().getTrueSource() != null) {
               entity = event.getSource().getTrueSource();
               Left2MinePacket.INSTANCE.sendTo(new HurtArrowMessage(entity.posX, entity.posY, entity.posZ), (EntityPlayerMP)event.getEntity());
            } else if (event.getSource().getDamageLocation() != null) {
               Vec3d pos = event.getSource().getDamageLocation();
               Left2MinePacket.INSTANCE.sendTo(new HurtArrowMessage(pos.x, pos.y, pos.z), (EntityPlayerMP)event.getEntity());
            }
         }

         entity = event.getEntity();
         if (!entity.getEntityWorld().isRemote) {
            if (entity.getEntityWorld().rand.nextInt(20) == 0) {
               double offset = entity.getEntityWorld().rand.nextDouble() - 0.2D;
               Left2MinePacket.INSTANCE.sendToAll(new DecalMessage(1, EntityDecal.EnumDecalSide.ALL, entity.lastTickPosX - entity.motionX, entity.lastTickPosY - entity.motionY, entity.lastTickPosZ - entity.motionZ, true));
               Left2MinePacket.INSTANCE.sendToAll(new DecalMessage(1, EntityDecal.EnumDecalSide.WALLS, entity.lastTickPosX - entity.motionX, entity.lastTickPosY + (double)entity.getEyeHeight() - offset - entity.motionY, entity.lastTickPosZ - entity.motionZ, true));
            }
         } else {
            Main.proxy.addBlood(event.getEntityLiving(), 10, true);
         }

      }
   }

   @SubscribeEvent
   public void onBlockBreak(BreakEvent event) {
      if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockNodeParent) {
         TileEntity te = event.getWorld().getTileEntity(event.getPos());
         if (te != null && te instanceof TileEntityNodeParent) {
            TileEntityNodeParent parent = (TileEntityNodeParent)te;
            if (parent.getBreakTimer() < event.getWorld().getTotalWorldTime()) {
               event.getPlayer().sendMessage(new TextComponentTranslation("message.breakparentalert1", new Object[0]));
               event.getPlayer().sendMessage(new TextComponentTranslation("message.breakparentalert2", new Object[0]));
               event.getPlayer().sendMessage(new TextComponentTranslation("message.breakparentalert3", new Object[0]));
               parent.setBreakTimer(event.getWorld().getTotalWorldTime() + 100L);
               event.setCanceled(true);
               return;
            }
         }
      }

   }
}
