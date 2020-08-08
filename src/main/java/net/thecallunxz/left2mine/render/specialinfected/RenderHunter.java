package net.thecallunxz.left2mine.render.specialinfected;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;
import net.thecallunxz.left2mine.models.ModelHunter;

public class RenderHunter extends RenderLiving<EntityHunter> {
   private ResourceLocation mobTexture = new ResourceLocation("left2mine:textures/entity/mobs/hunter.png");
   public static final RenderHunter.Factory FACTORY = new RenderHunter.Factory();

   public RenderHunter(RenderManager rendermanagerIn) {
      super(rendermanagerIn, new ModelHunter(), 0.0F);
   }

   @Nonnull
   protected ResourceLocation getEntityTexture(@Nonnull EntityHunter entity) {
      return this.mobTexture;
   }

   public static class Factory implements IRenderFactory<EntityHunter> {
      public Render<? super EntityHunter> createRenderFor(RenderManager manager) {
         return new RenderHunter(manager);
      }
   }
}
