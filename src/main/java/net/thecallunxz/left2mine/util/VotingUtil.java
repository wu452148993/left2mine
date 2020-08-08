package net.thecallunxz.left2mine.util;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;

public class VotingUtil {
   public static ArrayList<EntityPlayer> votedPlayerList = new ArrayList();
   public static int maxVoters = 0;
   public static int voting = 0;

   public static void resetVoting() {
      voting = 0;
      maxVoters = 0;
      votedPlayerList.clear();
   }
}
