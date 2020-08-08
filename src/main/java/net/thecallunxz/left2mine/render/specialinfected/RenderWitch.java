package net.thecallunxz.left2mine.render.specialinfected;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.models.ModelWitch;
import net.thecallunxz.left2mine.render.LayerWitchEyes;

public class RenderWitch extends RenderLiving<EntityWitch> {
   public static final RenderWitch.Factory FACTORY = new RenderWitch.Factory();
   private ResourceLocation mobTexture = new ResourceLocation("left2mine:textures/entity/mobs/witch.png");

   public RenderWitch(RenderManager rendermanagerIn) {
      super(rendermanagerIn, new ModelWitch(), 0.0F);
      this.addLayer(new LayerWitchEyes(this));
   }

   @Nonnull
   protected ResourceLocation getEntityTexture(@Nonnull EntityWitch entity) {
      return this.mobTexture;
   }

   public static class Factory implements IRenderFactory<EntityWitch> {
      public Render<? super EntityWitch> createRenderFor(RenderManager manager) {
         return new RenderWitch(manager);
      }
   }
}
