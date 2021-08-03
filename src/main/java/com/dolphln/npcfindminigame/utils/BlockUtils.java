package com.dolphln.npcfindminigame.utils;

import com.dolphln.npcfindminigame.NPCFindMinigame;
import org.bukkit.Material;

public class BlockUtils {

    private static Material[] materials = new Material[]{Material.AIR, Material.WATER, Material.LAVA, Material.GRASS, Material.SEAGRASS, Material.TALL_GRASS, Material.TALL_SEAGRASS, Material.FLOWER_POT};

    public static boolean isBlockStatic(Material mat, boolean oldMethod) {
        if (oldMethod) {
            if (!mat.isBlock()) return false;

            for (Material material : materials) {
                if (mat.equals(material)) return false;
            }

            if (mat.toString().toLowerCase().contains("potted") || mat.toString().toLowerCase().contains("LEAVES")) return false;

            return true;
        } else {
            return NPCFindMinigame.getInstance().getConfigFile().isMatValid(mat);
        }
    }

}
