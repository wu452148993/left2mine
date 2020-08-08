package net.thecallunxz.left2mine.entities.decals;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityScorch extends EntityDecal {
   public EntityScorch(World var1) {
      super(var1);
   }

   public ResourceLocation getDecalTexture() {
      String location = "left2mine:textures/entity/scorch/scorch" + this.getTextureNumber() + ".png";
      return new ResourceLocation(location);
   }

   public int getTextureCount() {
      return 1;
   }
}
