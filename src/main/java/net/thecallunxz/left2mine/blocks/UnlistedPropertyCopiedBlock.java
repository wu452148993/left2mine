package net.thecallunxz.left2mine.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyCopiedBlock implements IUnlistedProperty<IBlockState> {
   public String getName() {
      return "UnlistedPropertyCopiedBlock";
   }

   public boolean isValid(IBlockState value) {
      return true;
   }

   public Class<IBlockState> getType() {
      return IBlockState.class;
   }

   public String valueToString(IBlockState value) {
      return value.toString();
   }
}
