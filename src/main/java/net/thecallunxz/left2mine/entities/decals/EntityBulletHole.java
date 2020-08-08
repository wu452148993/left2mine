package net.thecallunxz.left2mine.entities.decals;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityBulletHole extends EntityDecal {
   public EntityBulletHole(World var1) {
      super(var1);
      this.maxTimeAlive = 200;
   }

   public ResourceLocation getDecalTexture() {
      String location = "left2mine:textures/entity/bullethole/bullethole" + this.getTextureNumber() + ".png";
      return new ResourceLocation(location);
   }

   public int getTextureCount() {
      return 1;
   }
}
