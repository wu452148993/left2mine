package net.thecallunxz.left2mine.events;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeChild;
import net.thecallunxz.left2mine.capabilities.CapabilityStats;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.capabilities.IStats;
import net.thecallunxz.left2mine.capabilities.StatsProvider;
import net.thecallunxz.left2mine.commands.CommandWinGame;
import net.thecallunxz.left2mine.config.Left2MineConfig;
import net.thecallunxz.left2mine.entities.EntityPipebomb;
import net.thecallunxz.left2mine.entities.mobs.EntityBossInfected;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;
import net.thecallunxz.left2mine.entities.mobs.EntitySpecialInfected;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityBoomer;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityTank;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.init.InitItems;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.AnimationAngleMessage;
import net.thecallunxz.left2mine.networking.client.EquippedMessage;
import net.thecallunxz.left2mine.networking.client.LyingMessage;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.networking.client.PinnedMessage;
import net.thecallunxz.left2mine.networking.client.PlayerDeathMessage;
import net.thecallunxz.left2mine.util.DifficultyUtil;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class TickableAIDirector {
   private HashMap<EntityPlayer, Integer> currentslot = new HashMap();
   private long panicScreamTime = 0L;
   private long panicWarnTime = 0L;
   private long panicDelay = 0L;
   private int panicWavesLeft = 0;
   private int lastType = -1;
   private int lastBoss = -1;
   private int checkInterval = 2;
   private boolean forceWitch = false;
   private boolean forceTank = false;

   @SubscribeEvent
   public void onTickWorld(WorldTickEvent event) {
      if (Left2MineConfig.lowSpecDirectorMode) {
         this.checkInterval = 5;
      } else {
         this.checkInterval = 2;
      }

      WorldDataLeft2Mine data;
      Iterator var3;
      EntityPlayer player;
      if (!event.world.isRemote) {
         data = WorldDataLeft2Mine.get(event.world);
         if (this.panicWarnTime != 0L && this.panicWarnTime <= event.world.getTotalWorldTime()) {
            data.setReadyToPanic(true);
            this.panicWarnTime = 0L;
         }

         if (this.getPanicDelay() != 0L && this.getPanicDelay() <= event.world.getTotalWorldTime()) {
            this.panicDelay = 0L;
            data.setInPanicEvent(true);
            data.setReadyToPanic(true);
            data.setDirectorEnabled(true);
         }

         if (CommandWinGame.winTime != 0L && CommandWinGame.winTime <= event.world.getTotalWorldTime()) {
            CommandWinGame.winTime = 0L;
            this.panicWarnTime = 0L;
            Left2MineUtilities.winGame(event.world);
         }

         if (Left2MineUtilities.gameOverTime != 0L && Left2MineUtilities.gameOverTime <= event.world.getTotalWorldTime()) {
            Left2MineUtilities.gameOverTime = 0L;
            this.panicWarnTime = 0L;
            Left2MineUtilities.gameOver(event.world, true);
         }

         if (Left2MineUtilities.pistolTime != 0L && Left2MineUtilities.pistolTime <= event.world.getTotalWorldTime()) {
            Left2MineUtilities.pistolTime = 0L;
            this.panicWarnTime = 0L;
            var3 = event.world.playerEntities.iterator();

            while(var3.hasNext()) {
               player = (EntityPlayer)var3.next();
               if (!player.isCreative()) {
                  ItemStack stack = new ItemStack(InitItems.pistol, 1);
                  player.replaceItemInInventory(1, stack);
                  IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
                  equip.clearEquipped();
                  equip.addEquipped(stack);
                  Left2MinePacket.INSTANCE.sendToAll(new EquippedMessage(equip.getEquippedList(), player));
               }
            }
         }
      }

      if (!event.world.isRemote) {
         data = WorldDataLeft2Mine.get(event.world);
         if (data.isInGame()) {
            int i;
            List bosses;
            ArrayList hordeLocs;
            AxisAlignedBB spawnArea;
            List specials;
            if (data.getWanderingCount() < 250) {
               hordeLocs = data.getActiveNodes();
               if (data.getActiveNodes().size() > 0) {
                  int getLoc = event.world.rand.nextInt(Math.min(data.getActiveNodes().size(), 15));
                  BlockPos randPos = (BlockPos)hordeLocs.get(getLoc);
                  TileEntityNodeChild node = (TileEntityNodeChild)event.world.getTileEntity(randPos);
                  if (node == null) {
                     data.removeActiveNode(getLoc);
                  } else if (!node.isActive()) {
                     data.removeActiveNode(getLoc);
                  } else {
                     Predicate<Entity> predicate = EntitySelectors.CAN_AI_TARGET;
                     EntityPlayer player = event.world.getClosestPlayer((double)randPos.getX(), (double)randPos.getY(), (double)randPos.getZ(), 50.0D, predicate);
                     i = 0;
                     if (player == null) {
                        data.removeActiveNode(getLoc);
                        data.addActiveNode(randPos);
                     } else {
                        bosses = event.world.playerEntities;

                        for(int i = 0; i < bosses.size(); ++i) {
                           EntityPlayer player2 = (EntityPlayer)bosses.get(i);
                           if (predicate.apply(player2) && Math.sqrt(player2.getDistanceSqToCenter(randPos)) < 70.0D && (Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player2.posX, player2.posY + (double)player2.getEyeHeight(), player2.posZ), new Vec3d((double)randPos.getX() + 0.5D, (double)randPos.getY(), (double)randPos.getZ() + 0.5D), event.world, 0) == null || Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player2.posX, player2.posY + (double)player2.getEyeHeight(), player2.posZ), new Vec3d((double)randPos.getX() + 0.5D, (double)randPos.getY() + 1.7D, (double)randPos.getZ() + 0.5D), event.world, 0) == null)) {
                              ++i;
                              break;
                           }
                        }

                        if (i != 0) {
                           node.setActive(false);
                           data.removeActiveNode(getLoc);
                        } else {
                           spawnArea = (new AxisAlignedBB(randPos, randPos.up())).grow(DifficultyUtil.getSpawnRadius(event.world), 1.0D, DifficultyUtil.getSpawnRadius(event.world));
                           specials = event.world.getEntitiesWithinAABB(EntityCommonInfected.class, spawnArea);
                           if (specials.isEmpty()) {
                              node.setActive(false);
                              data.removeActiveNode(getLoc);
                              this.spawnCommonInfected(randPos, event.world);
                           } else {
                              boolean panicFound = false;
                              Iterator var14 = specials.iterator();

                              label410:
                              while(true) {
                                 EntityCommonInfected zombie;
                                 do {
                                    if (!var14.hasNext()) {
                                       if (!panicFound) {
                                          node.setActive(false);
                                          data.removeActiveNode(getLoc);
                                       } else {
                                          node.setActive(false);
                                          data.removeActiveNode(getLoc);
                                          this.spawnCommonInfected(randPos, event.world);
                                       }
                                       break label410;
                                    }

                                    zombie = (EntityCommonInfected)var14.next();
                                 } while(!zombie.isPanic() && !(zombie.getAttackTarget() instanceof EntityPipebomb));

                                 panicFound = true;
                              }
                           }
                        }
                     }
                  }
               }
            }

            BlockPos pos;
            Predicate predicate;
            EntityPlayer player;
            int seenBy;
            List players;
            EntityPlayer player2;
            Path path;
            BlockPos endPoint;
            if (!data.isReadyToPanic()) {
               if (data.getDirectorEnabled()) {
                  if (data.isInPanicEvent()) {
                     if (event.world.getTotalWorldTime() - data.getLastPanicTime() > (long)DifficultyUtil.getPanicEventMaxTimer(event.world)) {
                        data.setReadyToPanic(true);
                        data.setLastPanicTime(event.world.getTotalWorldTime());
                     }
                  } else if (!data.isSurvivalInGame() && event.world.getTotalWorldTime() - data.getLastPanicTime() > (long)DifficultyUtil.getPanicMaxTimer(event.world) && event.world.rand.nextInt((int)((double)DifficultyUtil.getPanicMaxTimer(event.world) * 1.5D)) == 0 && this.panicWarnTime == 0L) {
                     this.panicWarnTime = event.world.getTotalWorldTime() + 40L;
                     data.setLastPanicTime(event.world.getTotalWorldTime());
                     var3 = event.world.playerEntities.iterator();

                     while(var3.hasNext()) {
                        player = (EntityPlayer)var3.next();
                        Left2MinePacket.INSTANCE.sendTo(new MovingSoundMessage(player, 14), (EntityPlayerMP)player);
                     }
                  }
               }
            } else if (data.getHordeCount() < 45 && event.world.getTotalWorldTime() % (long)this.checkInterval == 0L) {
               hordeLocs = data.getHordeNodes();
               if (hordeLocs.size() > 0) {
                  pos = (BlockPos)hordeLocs.get(event.world.rand.nextInt(hordeLocs.size()));
                  predicate = EntitySelectors.CAN_AI_TARGET;
                  player = event.world.getClosestPlayer((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 70.0D, predicate);
                  if (player != null) {
                     seenBy = 0;
                     players = event.world.playerEntities;

                     for(i = 0; i < players.size(); ++i) {
                        player2 = (EntityPlayer)players.get(i);
                        if (predicate.apply(player2) && Math.sqrt(player2.getDistanceSq(pos)) < 70.0D && (Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player2.posX, player2.posY + (double)player2.getEyeHeight(), player2.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D), event.world, 0) == null || Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player2.posX, player2.posY + (double)player2.getEyeHeight(), player2.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 1.7D, (double)pos.getZ() + 0.5D), event.world, 0) == null)) {
                           ++seenBy;
                           break;
                        }
                     }

                     if (seenBy == 0) {
                        path = Left2MineUtilities.getFullPathToPos(pos, player.getPosition(), event.world);
                        if (path != null && path.getFinalPathPoint() != null) {
                           endPoint = new BlockPos(path.getFinalPathPoint().x, path.getFinalPathPoint().y, path.getFinalPathPoint().z);
                           if (endPoint.distanceSq(player.getPosition()) < 2.0D) {
                              if (data.isInPanicEvent()) {
                                 if (path.getCurrentPathLength() > 10) {
                                    this.spawnCommonInfectedPanic(pos, event.world, player);
                                    data.setReadyToPanic(false);
                                    data.setLastPanicTime(event.world.getTotalWorldTime());
                                 }
                              } else if (path.getCurrentPathLength() > 15) {
                                 this.spawnCommonInfectedPanic(pos, event.world, player);
                                 data.setReadyToPanic(false);
                                 data.setLastPanicTime(event.world.getTotalWorldTime());
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (!data.getReadyToSpecial()) {
               if (data.getDirectorEnabled()) {
                  if (data.isInPanicEvent()) {
                     if (event.world.getTotalWorldTime() - data.getLastSpecialTime() > (long)DifficultyUtil.getSpecialEventMaxTimer(event.world)) {
                        data.setReadyToSpecial(true);
                        data.setLastSpecialTime(event.world.getTotalWorldTime());
                     }
                  } else if (!data.isSurvivalInGame() && event.world.getTotalWorldTime() - data.getLastSpecialTime() > (long)DifficultyUtil.getSpecialMaxTimer(event.world) && event.world.rand.nextInt(100) == 0) {
                     data.setReadyToSpecial(true);
                     data.setLastSpecialTime(event.world.getTotalWorldTime());
                  }
               }
            } else if (data.getSpecialCount() < 4 && (event.world.getTotalWorldTime() + 1L) % (long)this.checkInterval == 0L) {
               hordeLocs = data.getHordeNodes();
               if (hordeLocs.size() != 0) {
                  pos = (BlockPos)hordeLocs.get(event.world.rand.nextInt(hordeLocs.size()));
                  predicate = EntitySelectors.CAN_AI_TARGET;
                  player = event.world.getClosestPlayer((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 70.0D, predicate);
                  if (player != null) {
                     seenBy = 0;
                     players = event.world.playerEntities;

                     for(i = 0; i < players.size(); ++i) {
                        player2 = (EntityPlayer)players.get(i);
                        if (predicate.apply(player2) && Math.sqrt(player2.getDistanceSq(pos)) < 70.0D && (Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player2.posX, player2.posY + (double)player2.getEyeHeight(), player2.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D), event.world, 0) == null || Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player2.posX, player2.posY + (double)player2.getEyeHeight(), player2.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 1.7D, (double)pos.getZ() + 0.5D), event.world, 0) == null)) {
                           ++seenBy;
                           break;
                        }
                     }

                     if (seenBy == 0) {
                        path = Left2MineUtilities.getFullPathToPos(pos, player.getPosition(), event.world);
                        if (path != null && path.getFinalPathPoint() != null) {
                           endPoint = new BlockPos(path.getFinalPathPoint().x, path.getFinalPathPoint().y, path.getFinalPathPoint().z);
                           if (endPoint.distanceSq(player.getPosition()) < 2.0D) {
                              if (data.isInPanicEvent()) {
                                 if (path.getCurrentPathLength() > 3) {
                                    this.spawnSpecialInfected(pos, event.world, player);
                                    data.setReadyToSpecial(false);
                                    data.setLastSpecialTime(event.world.getTotalWorldTime());
                                    if (event.world.rand.nextBoolean() && event.world.rand.nextBoolean()) {
                                       data.addLurkingSpecial("random");
                                    }
                                 }
                              } else if (path.getCurrentPathLength() > 3) {
                                 this.spawnSpecialInfected(pos, event.world, player);
                                 data.setReadyToSpecial(false);
                                 data.setLastSpecialTime(event.world.getTotalWorldTime());
                                 if (event.world.rand.nextBoolean() && event.world.rand.nextBoolean()) {
                                    data.addLurkingSpecial("random");
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (data.getReadyToBoss() && !data.isInPanicEvent()) {
               if ((event.world.getTotalWorldTime() + 1L) % (long)this.checkInterval == 0L) {
                  hordeLocs = data.getBossNodes();
                  if (hordeLocs.size() != 0) {
                     pos = (BlockPos)hordeLocs.get(event.world.rand.nextInt(hordeLocs.size()));
                     predicate = EntitySelectors.CAN_AI_TARGET;
                     player = event.world.getClosestPlayer((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 40.0D, predicate);
                     if (player != null) {
                        seenBy = 0;
                        players = event.world.playerEntities;

                        for(i = 0; i < players.size(); ++i) {
                           player2 = (EntityPlayer)players.get(i);
                           if (predicate.apply(player2) && Math.sqrt(player2.getDistanceSq(pos)) < 40.0D && (Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player2.posX, player2.posY + (double)player2.getEyeHeight(), player2.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D), event.world, 0) == null || Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player2.posX, player2.posY + (double)player2.getEyeHeight(), player2.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 1.7D, (double)pos.getZ() + 0.5D), event.world, 0) == null)) {
                              ++seenBy;
                              break;
                           }
                        }

                        if (seenBy == 0) {
                           AxisAlignedBB spawnArea = (new AxisAlignedBB(pos, pos.up())).grow(DifficultyUtil.getSpawnRadius(event.world) * 4.0D, 1.0D, DifficultyUtil.getSpawnRadius(event.world) * 4.0D);
                           bosses = event.world.getEntitiesWithinAABB(EntityBossInfected.class, spawnArea);
                           if (bosses.isEmpty()) {
                              this.spawnBossInfected(pos, event.world);
                              data.setReadyToBoss(false);
                              data.setLastBossTime(event.world.getTotalWorldTime() - (long)event.world.rand.nextInt(1000));
                           }
                        }
                     }
                  }
               }
            } else if (data.getDirectorEnabled()) {
               if (!data.isInPanicEvent()) {
                  if (event.world.getTotalWorldTime() - data.getLastBossTime() > (long)DifficultyUtil.getBossMaxTimer(event.world)) {
                     data.setReadyToBoss(true);
                     data.setLastBossTime(event.world.getTotalWorldTime() - (long)event.world.rand.nextInt(1000));
                  }
               } else if (data.isSurvivalInGame() && data.hasSurvivalStarted() && event.world.getTotalWorldTime() - data.getLastBossTime() > (long)DifficultyUtil.getBossMaxTimerSurvival(event.world)) {
                  data.addLurkingSpecial("tank");
                  data.setLastBossTime(event.world.getTotalWorldTime() - (long)event.world.rand.nextInt(200));
               }
            }

            if (data.getLurkingCount() > 0 && (event.world.getTotalWorldTime() + 1L) % (long)this.checkInterval == 0L) {
               hordeLocs = data.getHordeNodes();
               if (hordeLocs.size() != 0) {
                  pos = (BlockPos)hordeLocs.get(event.world.rand.nextInt(hordeLocs.size()));
                  predicate = EntitySelectors.CAN_AI_TARGET;
                  player = event.world.getClosestPlayer((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 70.0D, predicate);
                  if (player != null) {
                     seenBy = 0;
                     players = event.world.playerEntities;

                     for(i = 0; i < players.size(); ++i) {
                        player2 = (EntityPlayer)players.get(i);
                        if (predicate.apply(player2) && Math.sqrt(player2.getDistanceSq(pos)) < 70.0D && (Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player2.posX, player2.posY + (double)player2.getEyeHeight(), player2.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D), event.world, 0) == null || Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player2.posX, player2.posY + (double)player2.getEyeHeight(), player2.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 1.7D, (double)pos.getZ() + 0.5D), event.world, 0) == null)) {
                           ++seenBy;
                           break;
                        }
                     }

                     if (seenBy == 0) {
                        path = Left2MineUtilities.getFullPathToPos(pos, player.getPosition(), event.world);
                        if (path != null && path.getFinalPathPoint() != null) {
                           endPoint = new BlockPos(path.getFinalPathPoint().x, path.getFinalPathPoint().y, path.getFinalPathPoint().z);
                           if (endPoint.distanceSq(player.getPosition()) < 2.0D && path.getCurrentPathLength() > 5) {
                              this.spawnCommonInfectedLurking(pos, event.world, player);
                              data.setLurkingCount(data.getLurkingCount() - 1);
                           }
                        }
                     }
                  }
               }
            }

            if (data.getLurkingSpecialList().size() > 0 && event.world.getTotalWorldTime() % (long)this.checkInterval == 0L) {
               hordeLocs = data.getHordeNodes();
               if (hordeLocs.size() != 0) {
                  pos = (BlockPos)hordeLocs.get(event.world.rand.nextInt(hordeLocs.size()));
                  predicate = EntitySelectors.CAN_AI_TARGET;
                  player = event.world.getClosestPlayer((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 70.0D, predicate);
                  if (player != null) {
                     seenBy = 0;
                     players = event.world.playerEntities;

                     for(i = 0; i < players.size(); ++i) {
                        player2 = (EntityPlayer)players.get(i);
                        if (predicate.apply(player2) && Math.sqrt(player2.getDistanceSq(pos)) < 70.0D && (Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player2.posX, player2.posY + (double)player2.getEyeHeight(), player2.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D), event.world, 0) == null || Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player2.posX, player2.posY + (double)player2.getEyeHeight(), player2.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 1.7D, (double)pos.getZ() + 0.5D), event.world, 0) == null)) {
                           ++seenBy;
                           break;
                        }
                     }

                     if (seenBy == 0) {
                        path = Left2MineUtilities.getFullPathToPos(pos, player.getPosition(), event.world);
                        if (path != null && path.getFinalPathPoint() != null) {
                           endPoint = new BlockPos(path.getFinalPathPoint().x, path.getFinalPathPoint().y, path.getFinalPathPoint().z);
                           if (endPoint.distanceSq(player.getPosition()) < 2.0D && path.getCurrentPathLength() > 5 && data.getLurkingSpecialList().get(0) != null) {
                              spawnArea = (new AxisAlignedBB(pos, pos.up())).grow(DifficultyUtil.getSpawnRadius(event.world) * 4.0D, 1.0D, DifficultyUtil.getSpawnRadius(event.world) * 4.0D);
                              specials = event.world.getEntitiesWithinAABB(EntitySpecialInfected.class, spawnArea);
                              if (specials.isEmpty()) {
                                 String name = (String)data.getLurkingSpecialList().get(0);
                                 this.spawnSpecialInfectedLurking(pos, event.world, player, name);
                                 data.removeLurkingSpecialID(0);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onTickPlayer(PlayerTickEvent event) {
      if (!event.player.getEntityWorld().isRemote) {
         if (event.player.ticksExisted % 400 == 0 && event.player.getAbsorptionAmount() > 0.0F && !event.player.isPotionActive(InitPotions.incapacitated)) {
            event.player.setAbsorptionAmount(event.player.getAbsorptionAmount() - 0.5F);
         }

         if (event.player.getEntityWorld().playerEntities.size() == 1 && event.player.ticksExisted % 100 == 0) {
            if (event.player.isPotionActive(InitPotions.hunter_pinned) && !event.player.isPotionActive(InitPotions.incapacitated)) {
               event.player.removePotionEffect(InitPotions.hunter_pinned);
               event.player.sendMessage(new TextComponentTranslation("message.player.breakfree", new Object[]{"Hunter"}));
            }

            if (event.player.isPotionActive(InitPotions.smoker_tongued) && !event.player.isPotionActive(InitPotions.incapacitated)) {
               event.player.removePotionEffect(InitPotions.smoker_tongued);
               event.player.sendMessage(new TextComponentTranslation("message.player.breakfree", new Object[]{"Smoker"}));
            }
         }

         if (event.player.getEntityWorld().getTotalWorldTime() % 40L == 0L) {
            IEquip equip;
            IStats stats;
            if (this.currentslot.get(event.player) != null) {
               if (event.player.inventory.currentItem != (Integer)this.currentslot.get(event.player)) {
                  this.currentslot.put(event.player, event.player.inventory.currentItem);
                  equip = (IEquip)event.player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
                  stats = (IStats)event.player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                  Left2MinePacket.INSTANCE.sendToAll(new EquippedMessage(equip.getEquippedList(), event.player));
                  CapabilityStats.updateStats(stats, event.player);
               }
            } else {
               this.currentslot.put(event.player, event.player.inventory.currentItem);
               equip = (IEquip)event.player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
               stats = (IStats)event.player.getCapability(StatsProvider.STATS, (EnumFacing)null);
               Left2MinePacket.INSTANCE.sendToAll(new EquippedMessage(equip.getEquippedList(), event.player));
               CapabilityStats.updateStats(stats, event.player);
            }

            if (!event.player.capabilities.allowEdit) {
               event.player.getFoodStats().setFoodLevel(40);
               equip = (IEquip)event.player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
               Left2MinePacket.INSTANCE.sendToAll(new LyingMessage(equip.getLying(), event.player));
               Left2MinePacket.INSTANCE.sendToAll(new PinnedMessage(equip.getPinned(), event.player));
               Left2MinePacket.INSTANCE.sendToAll(new AnimationAngleMessage(equip.getAnimationAngle(), event.player));
            }

            if (event.player.isSpectator()) {
               WorldDataLeft2Mine data = WorldDataLeft2Mine.get(event.player.getEntityWorld());
               if (data.isInGame()) {
                  Left2MinePacket.INSTANCE.sendToAll(new PlayerDeathMessage(event.player.getDisplayNameString(), 0));
                  Predicate<Entity> predicate = EntitySelectors.CAN_AI_TARGET;
                  EntityPlayer closest = event.player.getEntityWorld().getClosestPlayer(event.player.posX, event.player.posY, event.player.posZ, Double.MAX_VALUE, predicate);
                  if (closest != null && event.player instanceof EntityPlayerMP && ((EntityPlayerMP)event.player).getSpectatingEntity() == event.player) {
                     ((EntityPlayerMP)event.player).setPositionAndUpdate(closest.posX, closest.posY, closest.posZ);
                     ((EntityPlayerMP)event.player).setSpectatingEntity(closest);
                  }
               }
            }
         }
      }

   }

   private void spawnCommonInfectedPanic(BlockPos pos, World world, EntityPlayer player) {
      Random rand = new Random();
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      int count = DifficultyUtil.getHordeSize(world);
      data.setLurkingCount(data.getLurkingCount() + 5 + world.rand.nextInt(5));
      if (data.isInPanicEvent()) {
         if (this.panicScreamTime <= world.getTotalWorldTime()) {
            world.playSound((EntityPlayer)null, pos, InitSounds.horde_sound, SoundCategory.HOSTILE, 25.0F, 1.0F);
            this.panicScreamTime = world.getTotalWorldTime() + 400L;
         }

         if (this.getPanicWavesLeft() > 0) {
            this.setPanicWavesLeft(this.getPanicWavesLeft() - 1);
            if (this.getPanicWavesLeft() == 0) {
               data.setInPanicEvent(false);
            }
         }
      }

      for(int i = 0; i < count; ++i) {
         double XOffset = rand.nextDouble() / 4.0D - 0.125D;
         double ZOffset = rand.nextDouble() / 4.0D - 0.125D;
         EntityCommonInfected infected = new EntityCommonInfected(world);
         infected.setPositionAndRotation((double)pos.getX() + XOffset + 0.5D, (double)pos.getY(), (double)pos.getZ() + ZOffset + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
         if (world.spawnEntity(infected)) {
            infected.setPanic(true);
            infected.setAttackTarget(player);
            Left2MineUtilities.setPanicSlow(infected);
            data.setHordeCount(data.getHordeCount() + 1);
         }
      }

   }

   private void spawnCommonInfected(BlockPos pos, World world) {
      Random rand = new Random();
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      double XOffset = rand.nextDouble() / 4.0D - 0.125D;
      double ZOffset = rand.nextDouble() / 4.0D - 0.125D;
      EntityCommonInfected infected = new EntityCommonInfected(world);
      infected.setPositionAndRotation((double)pos.getX() + XOffset + 0.5D, (double)pos.getY(), (double)pos.getZ() + ZOffset + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
      if (world.spawnEntity(infected)) {
         data.setWanderingCount(data.getWanderingCount() + 1);
      }

   }

   private void spawnBossInfected(BlockPos pos, World world) {
      Random rand = new Random();
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      double XOffset = rand.nextDouble() / 4.0D - 0.125D;
      double ZOffset = rand.nextDouble() / 4.0D - 0.125D;
      EntityBossInfected infected = null;
      if (this.isWitchForced()) {
         this.setForceWitch(false);
         infected = new EntityWitch(world);
         this.lastBoss = 0;
      } else if (this.isTankForced()) {
         this.setForceTank(false);
         infected = new EntityTank(world);
         this.lastBoss = 1;
      }

      if (infected == null) {
         if (this.lastBoss == -1) {
            if (world.rand.nextBoolean()) {
               infected = new EntityWitch(world);
               this.lastBoss = 0;
            } else {
               infected = new EntityTank(world);
               this.lastBoss = 1;
            }
         } else if (this.lastBoss == 0) {
            if (world.rand.nextInt(3) != 0) {
               infected = new EntityTank(world);
               this.lastBoss = 1;
            } else {
               infected = new EntityWitch(world);
               this.lastBoss = 0;
            }
         } else if (world.rand.nextInt(3) != 0) {
            infected = new EntityWitch(world);
            this.lastBoss = 0;
         } else {
            infected = new EntityTank(world);
            this.lastBoss = 1;
         }
      }

      ((EntityBossInfected)infected).setPositionAndRotation((double)pos.getX() + XOffset + 0.5D, (double)pos.getY(), (double)pos.getZ() + ZOffset + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
      if (world.spawnEntity((Entity)infected)) {
         data.setBossCount(data.getBossCount() + 1);
      }

   }

   private void spawnCommonInfectedLurking(BlockPos pos, World world, EntityPlayer player) {
      Random rand = new Random();
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      double XOffset = rand.nextDouble() / 4.0D - 0.125D;
      double ZOffset = rand.nextDouble() / 4.0D - 0.125D;
      EntityCommonInfected infected = new EntityCommonInfected(world);
      infected.setPositionAndRotation((double)pos.getX() + XOffset + 0.5D, (double)pos.getY(), (double)pos.getZ() + ZOffset + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
      if (world.spawnEntity(infected)) {
         infected.setPanic(true);
         infected.setAttackTarget(player);
         data.setHordeCount(data.getHordeCount() + 1);
      }

   }

   private void spawnSpecialInfected(BlockPos pos, World world, EntityPlayer player) {
      Random rand = new Random();
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      double XOffset = rand.nextDouble() / 4.0D - 0.125D;
      double ZOffset = rand.nextDouble() / 4.0D - 0.125D;
      EntitySpecialInfected infected = null;
      if (data.isInPanicEvent()) {
         infected = this.getRandomSpecialInfected(world, rand);
      } else if (player.posY + 5.0D < (double)pos.getY() && this.lastType != 2) {
         infected = new EntitySmoker(world);
         this.lastType = 2;
      } else if (Math.sqrt(player.getDistanceSq(pos)) < 5.0D && this.lastType != 1) {
         infected = new EntityBoomer(world);
         this.lastType = 1;
      } else {
         infected = this.getRandomSpecialInfected(world, rand);
      }

      ((EntitySpecialInfected)infected).setPositionAndRotation((double)pos.getX() + XOffset + 0.5D, (double)pos.getY(), (double)pos.getZ() + ZOffset + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
      if (world.spawnEntity((Entity)infected)) {
         ((EntitySpecialInfected)infected).setAttackTarget(player);
         data.setSpecialCount(data.getSpecialCount() + 1);
      }

   }

   private void spawnSpecialInfectedLurking(BlockPos pos, World world, EntityPlayer player, String name) {
      Random rand = new Random();
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      double XOffset = rand.nextDouble() / 4.0D - 0.125D;
      double ZOffset = rand.nextDouble() / 4.0D - 0.125D;
      EntitySpecialInfected infected = null;
      EntityTank tank = null;
      byte var14 = -1;
      switch(name.hashCode()) {
      case -1383384872:
         if (name.equals("boomer")) {
            var14 = 2;
         }
         break;
      case -1206091904:
         if (name.equals("hunter")) {
            var14 = 1;
         }
         break;
      case -938285885:
         if (name.equals("random")) {
            var14 = 0;
         }
         break;
      case -898538269:
         if (name.equals("smoker")) {
            var14 = 3;
         }
         break;
      case 3552490:
         if (name.equals("tank")) {
            var14 = 4;
         }
      }

      switch(var14) {
      case 0:
         infected = this.getRandomSpecialInfected(world, rand);
         break;
      case 1:
         infected = new EntityHunter(world);
         break;
      case 2:
         infected = new EntityBoomer(world);
         break;
      case 3:
         infected = new EntitySmoker(world);
         break;
      case 4:
         tank = new EntityTank(world);
      }

      if (tank == null) {
         ((EntitySpecialInfected)infected).setPositionAndRotation((double)pos.getX() + XOffset + 0.5D, (double)pos.getY(), (double)pos.getZ() + ZOffset + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
         if (world.spawnEntity((Entity)infected)) {
            ((EntitySpecialInfected)infected).setAttackTarget(player);
            data.setSpecialCount(data.getSpecialCount() + 1);
         }
      } else {
         tank.setPositionAndRotation((double)pos.getX() + XOffset + 0.5D, (double)pos.getY(), (double)pos.getZ() + ZOffset + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
         if (world.spawnEntity(tank)) {
            tank.setAttackTarget(player);
            tank.setAttacking(true);
            data.setBossCount(data.getBossCount() + 1);
         }
      }

   }

   private EntitySpecialInfected getRandomSpecialInfected(World world, Random rand) {
      Entity infected = null;
      int amountOfSpecials = 3;
      int type = rand.nextInt(amountOfSpecials);
      if (type == this.lastType && type != amountOfSpecials - 1) {
         ++type;
      } else if (type == this.lastType && type == amountOfSpecials - 1) {
         type = 0;
      }

      switch(type) {
      case 0:
         infected = new EntityHunter(world);
         break;
      case 1:
         infected = new EntityBoomer(world);
         break;
      case 2:
         infected = new EntitySmoker(world);
      }

      this.lastType = type;
      return (EntitySpecialInfected)infected;
   }

   public long getPanicScreamTime() {
      return this.panicScreamTime;
   }

   public void setPanicScreamTime(long panicScreamTime) {
      this.panicScreamTime = panicScreamTime;
   }

   public long getPanicWarnTime() {
      return this.panicWarnTime;
   }

   public void setPanicWarnTime(long panicWarnTime) {
      this.panicWarnTime = panicWarnTime;
   }

   public long getPanicDelay() {
      return this.panicDelay;
   }

   public void setPanicDelay(long panicDelay) {
      this.panicDelay = panicDelay;
   }

   public int getPanicWavesLeft() {
      return this.panicWavesLeft;
   }

   public void setPanicWavesLeft(int panicWavesLeft) {
      this.panicWavesLeft = panicWavesLeft;
   }

   public boolean isWitchForced() {
      return this.forceWitch;
   }

   public void setForceWitch(boolean forceWitch) {
      this.forceWitch = forceWitch;
   }

   public boolean isTankForced() {
      return this.forceTank;
   }

   public void setForceTank(boolean forceTank) {
      this.forceTank = forceTank;
   }
}
