package com.dolphln.npcfindminigame.core;

import com.dolphln.npcfindminigame.NPCFindMinigame;
import com.dolphln.npcfindminigame.files.ConfigNPC;
import com.dolphln.npcfindminigame.utils.FireworkUtils;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.modifier.AnimationModifier;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class NPCManager implements Listener {

    private final NPCFindMinigame plugin;

    private boolean running;

    private NPCPool npcPool;
    private NPC playingNPC;

    public NPCManager(NPCFindMinigame plugin) {
        this.plugin = plugin;

        this.npcPool = NPCPool
                .builder(plugin)
                .actionDistance(2)
                .spawnDistance(80)
                .build();
        this.playingNPC = null;
    }

    public void createMinigame() {
        if (this.running) return;
        this.running = true;

        // TODO: Countdown

        // TODO: Generate Random Location

        this.startMinigame(null);
    }

    public void startMinigame(final Location loc) {
        String npcName = createNPC(loc);

        String title = ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getConfig().getString("message.npc_start_title.title"));
        String subtitle = ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getConfig().getString("message.npc_start_title.subtitle"));
        Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(title, subtitle, 1, 5, 1));

        String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getConfig().getString("message.npc_hint")
                .replaceAll("%name%", npcName)
                .replaceAll("%biome%", loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).toString()));
        Bukkit.broadcastMessage(message);
    }

    /**
     * @param loc Location to spawn the NPC (final)
     * @return NPC's name
     */
    public String createNPC(final Location loc) {
        Profile npcProfile = createProfile();
        this.playingNPC = NPC
                .builder()
                .location(loc)
                .lookAtPlayer(true)
                .imitatePlayer(false)
                .profile(npcProfile)
                .build(this.npcPool);

        return npcProfile.getName();
    }

    public Profile createProfile() {
        ConfigNPC configNPC = plugin.getConfigFile().getRandomNPC();

        Profile profile = new Profile(UUID.randomUUID(), List.of(new Profile.Property("textures", configNPC.getTexture(), configNPC.getSignature())));
        profile.complete();
        profile.setName(configNPC.getNpcName());
        return profile;
    }

    @EventHandler
    public void handleNPCInteract(PlayerNPCInteractEvent e) {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();

        if (this.playingNPC == null || this.playingNPC != npc) return;
        this.playingNPC = null;

        npc.animation().queue(AnimationModifier.EntityAnimation.TAKE_DAMAGE);
        FireworkUtils.spawnFireworks(npc.getLocation(), 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                running = false;

                npcPool.removeNPC(npc.getEntityId());
                FireworkUtils.spawnFireworks(npc.getLocation(), 1);
            }
        }.runTaskLater(plugin, 4L);

        player.sendTitle(
                ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getConfig().getString("message.winner_title.title")),
                ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getConfig().getString("message.winner_title.subtitle")),
                0, 4, 1);
        Bukkit.broadcastMessage(plugin.getConfigFile().getConfig().getString("winner_message").replaceAll("%player%", player.getDisplayName()));
    }

    public boolean isRunning() {
        return running;
    }
}
