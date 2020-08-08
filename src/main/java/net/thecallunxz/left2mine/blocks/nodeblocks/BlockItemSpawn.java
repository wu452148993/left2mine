package net.thecallunxz.left2mine.blocks.nodeblocks;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.blocks.nodes.BlockNodeChild;
import net.thecallunxz.left2mine.blocks.nodes.NodeSelection;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityItemNode;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeParent;
import net.thecallunxz.left2mine.entities.EntityItemLoot;
import net.thecallunxz.left2mine.init.InitItems;
import net.thecallunxz.left2mine.items.usable.ItemNodeLinker;
import net.thecallunxz.left2mine.items.weapons.ItemGun;

public class BlockItemSpawn extends BlockNodeChild {
   public BlockItemSpawn(String name) {
      super(name);
   }

   public void update(BlockPos pos, World world) {
      TileEntityItemNode node = (TileEntityItemNode)world.getTileEntity(pos);
      if (node.isActive()) {
         node.setActive(false);
         this.spawnItem(pos, world);
      }

   }

   public void spawnItem(BlockPos pos, World world) {
      if (!world.isRemote) {
         TileEntityItemNode tileentity = (TileEntityItemNode)world.getTileEntity(pos);
         if (tileentity.getCustomSpawn().getItem() == Items.AIR) {
            tileentity.setCustomSpawn(ItemStack.EMPTY);
         }

         int repeats = tileentity.getAmount();
         if (repeats == 0) {
            Predicate<Entity> predicate = EntitySelectors.CAN_AI_TARGET;
            Iterator var6 = world.playerEntities.iterator();

            while(var6.hasNext()) {
               EntityPlayer entity = (EntityPlayer)var6.next();
               if (predicate.apply(entity)) {
                  ++repeats;
               }
            }
         }

         if (tileentity.getCustomSpawn() != ItemStack.EMPTY) {
            int chance = tileentity.getCustomSpawn().getCount();

            for(int i = 0; i < repeats; ++i) {
               boolean bool = world.rand.nextInt(10) <= chance;
               if (bool) {
                  this.spawnItemRandom(world, pos, new ItemStack(tileentity.getCustomSpawn().getItem()));
               }
            }
         } else {
            this.spawnItemPreset(pos, world, repeats, tileentity.getType());
         }
      }

   }

   private void spawnItemRandom(World world, BlockPos pos, ItemStack item) {
      Random rand = new Random();
      double xRand = rand.nextDouble() / 5.0D;
      double zRand = rand.nextDouble() / 5.0D;
      EntityItem ent = new EntityItem(world, (double)pos.getX() + 0.4D + xRand, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.4D + zRand, item);
      EntityItemLoot entLoot = new EntityItemLoot(ent);
      world.spawnEntity(entLoot);
   }

   private void spawnItemPreset(BlockPos pos, World world, int repeats, int type) {
      for(int i = 0; i < repeats; ++i) {
         int randInt = world.rand.nextInt(10);
         switch(type) {
         case 0:
            switch(randInt) {
            case 0:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.firstaid, 1));
               continue;
            case 1:
            case 2:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.pills, 1));
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.pills, 1));
               continue;
            default:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.pills, 1));
               continue;
            }
         case 1:
            switch(randInt) {
            case 0:
            case 1:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.pills, 1));
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.firstaid, 1));
               continue;
            case 2:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.pills, 1));
               continue;
            default:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.firstaid, 1));
               continue;
            }
         case 2:
            randInt = world.rand.nextInt(4);
            switch(randInt) {
            case 0:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.submachinegun, 1));
               continue;
            case 1:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.silencedsubmachinegun, 1));
               continue;
            case 2:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.chromeshotgun, 1));
               continue;
            case 3:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.pumpshotgun, 1));
            default:
               continue;
            }
         case 3:
            randInt = world.rand.nextInt(4);
            switch(randInt) {
            case 0:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.ak47, 1));
               continue;
            case 1:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.m16rifle, 1));
               continue;
            case 2:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.combatshotgun, 1));
               continue;
            case 3:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.autoshotgun, 1));
            default:
               continue;
            }
         case 4:
            randInt = world.rand.nextInt(2);
            switch(randInt) {
            case 0:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.pipebomb, 1));
               break;
            case 1:
               this.spawnItemRandom(world, pos, new ItemStack(InitItems.molotov, 1));
            }
         }
      }

   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return NodeSelection.isNodeSelected() ? new TileEntityItemNode(NodeSelection.getSelectedNode()) : new TileEntityItemNode();
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (playerIn.isCreative()) {
         ItemStack stack = playerIn.getHeldItem(hand);
         if (stack.getItem() instanceof ItemNodeLinker) {
            return false;
         } else {
            TileEntityItemNode tileentity = (TileEntityItemNode)worldIn.getTileEntity(pos);
            if (playerIn.isSneaking()) {
               if (tileentity.getAmount() < 10) {
                  tileentity.setAmount(tileentity.getAmount() + 1);
               } else {
                  tileentity.setAmount(0);
               }

               if (!worldIn.isRemote) {
                  if (tileentity.getAmount() == 0) {
                     playerIn.sendMessage(new TextComponentTranslation("message.itemspawner.customamount", new Object[0]));
                  } else {
                     playerIn.sendMessage(new TextComponentTranslation("message.itemspawner.amount", new Object[]{tileentity.getAmount()}));
                  }
               }

               return !(stack.getItem() instanceof ItemGun);
            } else if (stack == ItemStack.EMPTY) {
               tileentity.setCustomSpawn(ItemStack.EMPTY);
               if (tileentity.getType() < 4) {
                  tileentity.setType(tileentity.getType() + 1);
               } else {
                  tileentity.setType(0);
               }

               if (!worldIn.isRemote) {
                  playerIn.sendMessage(new TextComponentTranslation("message.itemspawner.type", new Object[]{this.getNameFromType(tileentity.getType())}));
               }

               return !(stack.getItem() instanceof ItemGun);
            } else {
               if (stack.getItem() == tileentity.getCustomSpawn().getItem()) {
                  if (tileentity.getCustomSpawn().getCount() < 10) {
                     tileentity.getCustomSpawn().setCount(tileentity.getCustomSpawn().getCount() + 1);
                  } else {
                     tileentity.getCustomSpawn().setCount(1);
                  }
               } else {
                  tileentity.setCustomSpawn(new ItemStack(stack.getItem(), 10));
               }

               if (!worldIn.isRemote) {
                  playerIn.sendMessage(new TextComponentTranslation("message.itemspawner.customtype", new Object[]{tileentity.getCustomSpawn().getTextComponent(), tileentity.getCustomSpawn().getCount() * 10 + "%"}));
               }

               return !(stack.getItem() instanceof ItemGun);
            }
         }
      } else {
         return false;
      }
   }

   private String getNameFromType(int type) {
      String name = "PRESET ERROR";
      switch(type) {
      case 0:
         name = (new TextComponentTranslation("message.itemspawner.preset.minorhealing", new Object[0])).getFormattedText();
         break;
      case 1:
         name = (new TextComponentTranslation("message.itemspawner.preset.majorhealing", new Object[0])).getFormattedText();
         break;
      case 2:
         name = (new TextComponentTranslation("message.itemspawner.preset.t1weapons", new Object[0])).getFormattedText();
         break;
      case 3:
         name = (new TextComponentTranslation("message.itemspawner.preset.t2weapons", new Object[0])).getFormattedText();
         break;
      case 4:
         name = (new TextComponentTranslation("message.itemspawner.preset.grenades", new Object[0])).getFormattedText();
      }

      return name;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      if (NodeSelection.getSelectedNode() != null && NodeSelection.isNodeSelected()) {
         TileEntity tileentity = worldIn.getTileEntity(NodeSelection.getSelectedNode());
         if (tileentity instanceof TileEntityNodeParent) {
            ((TileEntityNodeParent)tileentity).addChildNode(pos);
         }
      }

      return true;
   }
}
