package net.thecallunxz.left2mine.init;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.entities.EntityItemLoot;
import net.thecallunxz.left2mine.entities.EntityMolotov;
import net.thecallunxz.left2mine.entities.EntityPipebomb;
import net.thecallunxz.left2mine.entities.decals.EntityBlood;
import net.thecallunxz.left2mine.entities.decals.EntityBulletHole;
import net.thecallunxz.left2mine.entities.decals.EntityScorch;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityBoomer;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityTank;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.entities.projectiles.EntityProjectile;
import net.thecallunxz.left2mine.render.RenderCommonInfected;
import net.thecallunxz.left2mine.render.RenderDecal;
import net.thecallunxz.left2mine.render.RenderItemLoot;
import net.thecallunxz.left2mine.render.projectiles.RenderMolotov;
import net.thecallunxz.left2mine.render.projectiles.RenderPipebomb;
import net.thecallunxz.left2mine.render.projectiles.RenderProjectile;
import net.thecallunxz.left2mine.render.specialinfected.RenderBoomer;
import net.thecallunxz.left2mine.render.specialinfected.RenderHunter;
import net.thecallunxz.left2mine.render.specialinfected.RenderSmoker;
import net.thecallunxz.left2mine.render.specialinfected.RenderTank;
import net.thecallunxz.left2mine.render.specialinfected.RenderWitch;

public class InitEntities {
   private static int entityID = 0;

   public static void register() {
      registerEntity(EntityCommonInfected.class, "commoninfected", 80, 3, false);
      registerEntity(EntityHunter.class, "hunter", 80, 3, true);
      registerEntity(EntityBoomer.class, "boomer", 80, 3, false);
      registerEntity(EntitySmoker.class, "smoker", 80, 3, false);
      registerEntity(EntityTank.class, "tank", 80, 3, false);
      registerEntity(EntityWitch.class, "witch", 80, 3, false);
      registerEntity(EntityItemLoot.class, "itemloot", 64, 1, true);
      registerEntity(EntityPipebomb.class, "pipebomb", 64, 1, true);
      registerEntity(EntityMolotov.class, "molotov", 64, 1, true);
      registerEntity(EntityProjectile.class, "projectile", 64, 1, true);
      registerEntity(EntityBlood.class, "blood", 80, 10, false);
      registerEntity(EntityBulletHole.class, "bullethole", 80, 10, false);
      registerEntity(EntityScorch.class, "scorch", 80, 10, false);
   }

   @SideOnly(Side.CLIENT)
   public static void initModels() {
      RenderingRegistry.registerEntityRenderingHandler(EntityCommonInfected.class, RenderCommonInfected.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityHunter.class, RenderHunter.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityBoomer.class, RenderBoomer.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntitySmoker.class, RenderSmoker.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityTank.class, RenderTank.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityWitch.class, RenderWitch.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityItemLoot.class, RenderItemLoot.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityProjectile.class, RenderProjectile.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityPipebomb.class, RenderPipebomb.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityMolotov.class, RenderMolotov.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityBlood.class, RenderDecal.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityBulletHole.class, RenderDecal.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityScorch.class, RenderDecal.FACTORY);
   }

   private static void registerEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
      EntityRegistry.registerModEntity(new ResourceLocation("left2mine:" + entityName), entityClass, entityName, entityID++, Main.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
   }
}
