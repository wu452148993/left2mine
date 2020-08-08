package net.thecallunxz.left2mine.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;

public abstract class BlockCustomDirectional extends BlockBase {
   public static final PropertyDirection FACING = PropertyDirection.create("facing");

   public BlockCustomDirectional(Material materialIn, String name) {
      super(materialIn, name);
   }
}
