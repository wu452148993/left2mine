package net.thecallunxz.left2mine.items.weapons.guns;

import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.items.ItemBase;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;
import net.thecallunxz.left2mine.items.weapons.properties.ProjectileProperties;

public class ItemPistol extends ItemGun {
   public static GunProperties gun = new GunProperties() {
      public void setProperties() {
         this.maxFireCooldown = 2;
         this.maxReloadTime = 30;
         this.magazineSize = 15;
         this.storageSize = 0;
         this.recoil = 1.5F;
         this.accuracy = 3.0F;
         this.maxBloom = 200;
         this.bloomAdd = 200;
         this.bloomSubtract = 100;
         this.shots = 1;
         this.auto = false;
         this.infinite = true;
         this.refillable = false;
         this.shellreload = false;
         this.doubleBloomSub = false;
         this.fireSound = InitSounds.pistol_fire;
         this.equipSound = InitSounds.pistol_equip;
         this.reloadSound = InitSounds.pistol_reload;
         this.pumpSound = null;
         this.canZoom = false;
         this.zoomFovModifier = 0.0F;
         this.zoomXOffset = 0.0D;
         this.zoomYOffset = 0.0D;
         this.zoomZOffset = 0.0D;
         this.zoomSmooth = false;
         this.flashXOffset = 0.55D;
         this.flashYOffset = -0.2D;
         this.flashZOffset = 0.0D;
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
         this.power = 1.0F;
         this.speed = 10.0D;
         this.life = 30;
         this.visible = false;
         this.gravity = false;
         this.damageReduceOverLife = false;
         this.damageReduceIfNotZoomed = false;
      }
   };

   public ItemPistol(String name, ItemBase.EnumItemType type) {
      super(name, type, gun, projectile);
   }
}
