package net.thecallunxz.left2mine.entities.mobs;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;

public class DamageSourceMaul extends DamageSource {
   @Nullable
   protected Entity damageSourceEntity;
   private boolean isThornsDamage;

   public DamageSourceMaul(String damageTypeIn, @Nullable Entity damageSourceEntityIn) {
      super(damageTypeIn);
      this.damageSourceEntity = damageSourceEntityIn;
   }

   @Nullable
   public Entity getTrueSource() {
      return null;
   }

   @Nullable
   public Vec3d getDamageLocation() {
      return this.damageSourceEntity.getPositionVector();
   }
}
