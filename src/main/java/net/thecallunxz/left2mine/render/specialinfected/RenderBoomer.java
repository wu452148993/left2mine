package net.thecallunxz.left2mine.render.specialinfected;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityBoomer;
import net.thecallunxz.left2mine.models.ModelBoomer;

public class RenderBoomer extends RenderLiving<EntityBoomer> {
   private ResourceLocation mobTexture = new ResourceLocation("left2mine:textures/entity/mobs/boomer.png");
   public static final RenderBoomer.Factory FACTORY = new RenderBoomer.Factory();

   public RenderBoomer(RenderManager rendermanagerIn) {
      super(rendermanagerIn, new ModelBoomer(), 0.0F);
   }

   @Nonnull
   protected ResourceLocation getEntityTexture(@Nonnull EntityBoomer entity) {
      return this.mobTexture;
   }

   public static class Factory implements IRenderFactory<EntityBoomer> {
      public Render<? super EntityBoomer> createRenderFor(RenderManager manager) {
         return new RenderBoomer(manager);
      }
   }
}
