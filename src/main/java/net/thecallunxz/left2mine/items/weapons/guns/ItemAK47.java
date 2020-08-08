package net.thecallunxz.left2mine.items.weapons.guns;

import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.items.ItemBase;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;
import net.thecallunxz.left2mine.items.weapons.properties.ProjectileProperties;

public class ItemAK47 extends ItemGun {
   public static GunProperties gun = new GunProperties() {
      public void setProperties() {
         this.maxFireCooldown = 2;
         this.maxReloadTime = 50;
         this.magazineSize = 40;
         this.storageSize = 360;
         this.recoil = 2.0F;
         this.accuracy = 1.75F;
         this.maxBloom = 800;
         this.bloomAdd = 400;
         this.bloomSubtract = 100;
         this.shots = 1;
         this.auto = true;
         this.infinite = false;
         this.refillable = true;
         this.shellreload = false;
         this.doubleBloomSub = false;
         this.fireSound = InitSounds.ak47_fire;
         this.equipSound = InitSounds.ak47_equip;
         this.reloadSound = InitSounds.ak47_reload;
         this.pumpSound = null;
         this.canZoom = false;
         this.zoomFovModifier = 0.0F;
         this.zoomXOffset = 0.0D;
         this.zoomYOffset = 0.0D;
         this.zoomZOffset = 0.0D;
         this.zoomSmooth = false;
         this.flashXOffset = 0.0D;
         this.flashYOffset = 0.0D;
         this.flashZOffset = 0.0D;
         this.scopeXOffset = 0.0D;
         this.scopeYOffset = 0.0D;
         this.scopeZOffset = 0.0D;
      }
   };
   public static ProjectileProperties projectile = new ProjectileProperties() {
      public void setProperties() {
         this.type = "BASIC";
         this.damage = 10.0F;
         this.size = 0.02F;
         this.power = 2.0F;
         this.speed = 10.0D;
         this.life = 30;
         this.visible = false;
         this.gravity = false;
         this.damageReduceOverLife = false;
         this.damageReduceIfNotZoomed = false;
      }
   };

   public ItemAK47(String name, ItemBase.EnumItemType type) {
      super(name, type, gun, projectile);
   }
}
