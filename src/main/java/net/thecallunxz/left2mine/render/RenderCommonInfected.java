package net.thecallunxz.left2mine.render;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.thecallunxz.left2mine.config.Left2MineConfig;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;
import net.thecallunxz.left2mine.models.ModelCommonInfected;

public class RenderCommonInfected extends RenderLiving<EntityCommonInfected> {
   public static final RenderCommonInfected.Factory FACTORY = new RenderCommonInfected.Factory();

   public RenderCommonInfected(RenderManager rendermanagerIn) {
      super(rendermanagerIn, new ModelCommonInfected(), 0.0F);
   }

   @Nonnull
   protected ResourceLocation getEntityTexture(@Nonnull EntityCommonInfected entity) {
      int skinNum = entity.getSkinNumber();
      if (!Left2MineConfig.randomCommonInfectedSkins) {
         skinNum = 0;
      }

      String location = "left2mine:textures/entity/mobs/commoninfected/zombie" + skinNum + ".png";
      return new ResourceLocation(location);
   }

   public static class Factory implements IRenderFactory<EntityCommonInfected> {
      public Render<? super EntityCommonInfected> createRenderFor(RenderManager manager) {
         return new RenderCommonInfected(manager);
      }
   }
}
