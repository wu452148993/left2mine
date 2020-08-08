package net.thecallunxz.left2mine.entities.projectiles;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;

public class DamageSourceShot extends DamageSource {
   @Nullable
   protected Entity damageSourceEntity;
   protected float power;
   protected float damage;
   private boolean isThornsDamage;

   public DamageSourceShot(String damageTypeIn, @Nullable Entity damageSourceEntityIn, float power, float damage) {
      super(damageTypeIn);
      this.damageSourceEntity = damageSourceEntityIn;
      this.power = power;
      this.damage = damage;
   }

   @Nullable
   public Entity getTrueSource() {
      return null;
   }

   public Entity getShooter() {
      return this.damageSourceEntity;
   }

   public float getPower() {
      return this.power;
   }

   public float getDamage() {
      return this.damage;
   }

   @Nullable
   public Vec3d getDamageLocation() {
      return this.damageSourceEntity.getPositionVector();
   }
}
