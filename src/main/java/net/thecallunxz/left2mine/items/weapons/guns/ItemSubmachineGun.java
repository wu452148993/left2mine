package net.thecallunxz.left2mine.items.weapons.guns;

import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.items.ItemBase;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;
import net.thecallunxz.left2mine.items.weapons.properties.ProjectileProperties;

public class ItemSubmachineGun extends ItemGun {
   public static GunProperties gun = new GunProperties() {
      public void setProperties() {
         this.maxFireCooldown = 1;
         this.maxReloadTime = 50;
         this.magazineSize = 50;
         this.storageSize = 650;
         this.recoil = 1.75F;
         this.accuracy = 5.0F;
         this.maxBloom = 200;
         this.bloomAdd = 200;
         this.bloomSubtract = 50;
         this.shots = 1;
         this.auto = true;
         this.infinite = false;
         this.refillable = true;
         this.shellreload = false;
         this.doubleBloomSub = true;
         this.fireSound = InitSounds.smg_fire;
         this.equipSound = InitSounds.smg_equip;
         this.reloadSound = InitSounds.smg_reload;
         this.pumpSound = null;
         this.canZoom = false;
         this.zoomFovModifier = 0.0F;
         this.zoomXOffset = 0.0D;
         this.zoomYOffset = 0.0D;
         this.zoomZOffset = 0.0D;
         this.zoomSmooth = false;
         this.flashXOffset = -0.1D;
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
         this.damage = 3.5F;
         this.size = 0.02F;
         this.power = 1.5F;
         this.speed = 10.0D;
         this.life = 30;
         this.visible = false;
         this.gravity = false;
         this.damageReduceOverLife = false;
         this.damageReduceIfNotZoomed = false;
      }
   };

   public ItemSubmachineGun(String name, ItemBase.EnumItemType type) {
      super(name, type, gun, projectile);
   }
}
