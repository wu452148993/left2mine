package net.thecallunxz.left2mine.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.items.ItemBase;

@SideOnly(Side.CLIENT)
public class LayerEquip implements LayerRenderer<EntityPlayer> {
   public void doRenderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      ItemStack inHand = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
      IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);

      for(int i = 0; i < equip.getEquippedList().size(); ++i) {
         ItemStack curStack = (ItemStack)equip.getEquippedList().get(i);
         if (curStack.getItem() instanceof ItemBase && inHand.getItem() != curStack.getItem() && !curStack.isEmpty()) {
            Item item = curStack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();
            GlStateManager.pushMatrix();
            if (player.isSneaking()) {
               GlStateManager.translate(0.0F, 0.2F, 0.0F);
               GlStateManager.rotate(28.8F, 1.0F, 0.0F, 0.0F);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            float f3 = 0.625F;
            GlStateManager.translate(0.0F, -0.25F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.625F, -0.625F, -0.625F);
            minecraft.getItemRenderer().renderItem(player, curStack, TransformType.HEAD);
            GlStateManager.popMatrix();
         }
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
