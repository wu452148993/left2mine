package net.thecallunxz.left2mine.entities.decals;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityBlood extends EntityDecal {
   public EntityBlood(World var1) {
      super(var1);
   }

   public ResourceLocation getDecalTexture() {
      String location = "left2mine:textures/entity/blood/blood" + this.getTextureNumber() + ".png";
      return new ResourceLocation(location);
   }

   public int getTextureCount() {
      return 5;
   }
}
