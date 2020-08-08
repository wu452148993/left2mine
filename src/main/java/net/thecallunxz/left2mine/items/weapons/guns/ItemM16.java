package net.thecallunxz.left2mine.items.weapons.guns;

import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.items.ItemBase;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;
import net.thecallunxz.left2mine.items.weapons.properties.ProjectileProperties;

public class ItemM16 extends ItemGun {
   public static GunProperties gun = new GunProperties() {
      public void setProperties() {
         this.maxFireCooldown = 1;
         this.maxReloadTime = 30;
         this.magazineSize = 50;
         this.storageSize = 360;
         this.recoil = 2.0F;
         this.accuracy = 2.0F;
         this.maxBloom = 500;
         this.bloomAdd = 250;
         this.bloomSubtract = 75;
         this.shots = 1;
         this.auto = true;
         this.infinite = false;
         this.refillable = true;
         this.shellreload = false;
         this.doubleBloomSub = false;
         this.fireSound = InitSounds.m16_fire;
         this.equipSound = InitSounds.m16_equip;
         this.reloadSound = InitSounds.m16_reload;
         this.pumpSound = null;
         this.canZoom = false;
         this.zoomFovModifier = 0.0F;
         this.zoomXOffset = 0.0D;
         this.zoomYOffset = 0.0D;
         this.zoomZOffset = 0.0D;
         this.zoomSmooth = false;
         this.flashXOffset = -0.15D;
         this.flashYOffset = 0.0D;
         this.flashZOffset = -0.25D;
         this.scopeXOffset = 0.0D;
         this.scopeYOffset = 0.0D;
         this.scopeZOffset = 0.0D;
      }
   };
   public static ProjectileProperties projectile = new ProjectileProperties() {
      public void setProperties() {
         this.type = "BASIC";
         this.damage = 4.0F;
         this.size = 0.02F;
         this.power = 2.25F;
         this.speed = 10.0D;
         this.life = 30;
         this.visible = false;
         this.gravity = false;
         this.damageReduceOverLife = false;
         this.damageReduceIfNotZoomed = false;
      }
   };

   public ItemM16(String name, ItemBase.EnumItemType type) {
      super(name, type, gun, projectile);
   }
}
