package net.thecallunxz.left2mine.entities.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityHitVec {
   private Entity ent;
   private Vec3d hitLoc;

   public EntityHitVec(Entity ent, Vec3d hitLoc) {
      this.ent = ent;
      this.hitLoc = hitLoc;
   }

   public Entity getEnt() {
      return this.ent;
   }

   public void setEnt(Entity ent) {
      this.ent = ent;
   }

   public Vec3d getHitLoc() {
      return this.hitLoc;
   }

   public void setHitLoc(Vec3d hitLoc) {
      this.hitLoc = hitLoc;
   }
}
