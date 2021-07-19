package com.dolphln.npcfindminigame.utils;

import org.bukkit.Material;

public class BlockUtils {

    public static boolean isBlockStatic(Material material) {
        return !(material == Material.AIR || material == Material.WATER || material == Material.LAVA);
    }

}
