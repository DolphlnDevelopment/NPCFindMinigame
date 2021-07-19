package com.dolphln.npcfindminigame.listener;

import com.dolphln.npcfindminigame.NPCFindMinigame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public record JoinLeaveListener(NPCFindMinigame plugin) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (plugin.getNpcManager().didItStart()) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, plugin.getConfigFile().getConfig().getInt("speed_power"), true, false, true));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinAsync(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getDatabase().addPlayer(e.getPlayer().getUniqueId(), e.getPlayer().getDisplayName());
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (plugin.getNpcManager().isRunning()) {
            e.getPlayer().removePotionEffect(PotionEffectType.SPEED);
        }
    }
}
