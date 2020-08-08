package net.thecallunxz.left2mine.render.specialinfected;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityTank;
import net.thecallunxz.left2mine.models.ModelTank;

public class RenderTank extends RenderLiving<EntityTank> {
   private ResourceLocation mobTexture = new ResourceLocation("left2mine:textures/entity/mobs/tank.png");
   public static final RenderTank.Factory FACTORY = new RenderTank.Factory();

   public RenderTank(RenderManager rendermanagerIn) {
      super(rendermanagerIn, new ModelTank(), 0.0F);
   }

   @Nonnull
   protected ResourceLocation getEntityTexture(@Nonnull EntityTank entity) {
      return this.mobTexture;
   }

   public static class Factory implements IRenderFactory<EntityTank> {
      public Render<? super EntityTank> createRenderFor(RenderManager manager) {
         return new RenderTank(manager);
      }
   }
}
