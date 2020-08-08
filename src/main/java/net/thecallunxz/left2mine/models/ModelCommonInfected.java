package net.thecallunxz.left2mine.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCommonInfected extends ModelBiped {
   public ModelCommonInfected() {
      this(0.0F, false);
   }

   public ModelCommonInfected(float modelSize, boolean child) {
      super(modelSize, 0.0F, 64, child ? 32 : 64);
   }
}
