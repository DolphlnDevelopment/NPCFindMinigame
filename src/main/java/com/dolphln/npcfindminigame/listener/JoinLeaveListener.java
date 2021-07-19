package com.dolphln.npcfindminigame.listener;

import com.dolphln.npcfindminigame.NPCFindMinigame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JoinLeaveListener implements Listener {

    private final NPCFindMinigame plugin;

    public JoinLeaveListener(NPCFindMinigame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (plugin.getNpcManager().didItStart()) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, plugin.getConfigFile().getConfig().getInt("speed_power"), true, false, true));
        }
    }
}
