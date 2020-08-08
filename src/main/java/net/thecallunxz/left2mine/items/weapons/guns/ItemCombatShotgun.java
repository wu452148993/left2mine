package net.thecallunxz.left2mine.items.weapons.guns;

import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.items.ItemBase;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;
import net.thecallunxz.left2mine.items.weapons.properties.ProjectileProperties;

public class ItemCombatShotgun extends ItemGun {
   public static GunProperties gun = new GunProperties() {
      public void setProperties() {
         this.maxFireCooldown = 5;
         this.maxReloadTime = 10;
         this.magazineSize = 10;
         this.storageSize = 90;
         this.recoil = 6.0F;
         this.accuracy = 6.0F;
         this.maxBloom = 1000;
         this.bloomAdd = 1000;
         this.bloomSubtract = 150;
         this.shots = 8;
         this.auto = true;
         this.infinite = false;
         this.refillable = true;
         this.shellreload = true;
         this.doubleBloomSub = false;
         this.fireSound = InitSounds.combatshotgun_fire;
         this.equipSound = InitSounds.autoshotgun_equip;
         this.reloadSound = InitSounds.pumpshotgun_reload;
         this.pumpSound = InitSounds.pumpshotgun_pump;
         this.canZoom = false;
         this.zoomFovModifier = 0.0F;
         this.zoomXOffset = 0.0D;
         this.zoomYOffset = 0.0D;
         this.zoomZOffset = 0.0D;
         this.zoomSmooth = false;
         this.flashXOffset = -0.2D;
         this.flashYOffset = 0.1D;
         this.flashZOffset = 0.0D;
         this.scopeXOffset = 0.0D;
         this.scopeYOffset = 0.0D;
         this.scopeZOffset = 0.0D;
      }
   };
   public static ProjectileProperties projectile = new ProjectileProperties() {
      public void setProperties() {
         this.type = "SHELL";
         this.damage = 4.0F;
         this.size = 0.02F;
         this.power = 3.0F;
         this.speed = 10.0D;
         this.life = 30;
         this.visible = false;
         this.gravity = false;
         this.damageReduceOverLife = true;
         this.damageReduceIfNotZoomed = false;
      }
   };

   public ItemCombatShotgun(String name, ItemBase.EnumItemType type) {
      super(name, type, gun, projectile);
   }
}
