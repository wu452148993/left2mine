package net.thecallunxz.left2mine.render.specialinfected;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;
import net.thecallunxz.left2mine.models.ModelSmoker;

public class RenderSmoker extends RenderLiving<EntitySmoker> {
   private ResourceLocation mobTexture = new ResourceLocation("left2mine:textures/entity/mobs/smoker.png");
   public static final RenderSmoker.Factory FACTORY = new RenderSmoker.Factory();

   public RenderSmoker(RenderManager rendermanagerIn) {
      super(rendermanagerIn, new ModelSmoker(), 0.0F);
   }

   @Nonnull
   protected ResourceLocation getEntityTexture(@Nonnull EntitySmoker entity) {
      return this.mobTexture;
   }

   public static class Factory implements IRenderFactory<EntitySmoker> {
      public Render<? super EntitySmoker> createRenderFor(RenderManager manager) {
         return new RenderSmoker(manager);
      }
   }
}
