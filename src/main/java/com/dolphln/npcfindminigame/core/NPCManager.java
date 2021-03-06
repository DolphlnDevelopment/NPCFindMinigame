package com.dolphln.npcfindminigame.core;

import com.dolphln.npcfindminigame.NPCFindMinigame;
import com.dolphln.npcfindminigame.files.ConfigNPC;
import com.dolphln.npcfindminigame.models.BasicLocation;
import com.dolphln.npcfindminigame.utils.BlockUtils;
import com.dolphln.npcfindminigame.utils.FireworkUtils;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.modifier.AnimationModifier;
import com.github.juliarn.npc.profile.Profile;
import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

public class NPCManager implements Listener {

    private final NPCFindMinigame plugin;
    private final NPCPool npcPool;
    private boolean running;
    private BukkitTask gameTask;
    private Integer time;
    private NPC playingNPC;

    public NPCManager(NPCFindMinigame plugin) {
        this.plugin = plugin;

        this.npcPool = NPCPool
                .builder(plugin)
                .actionDistance(2)
                .spawnDistance(80)
                .build();
        this.playingNPC = null;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public CreateGameResult createMinigame() {
        if (this.running) return CreateGameResult.GAME_ALREADY_RUNNING;
        this.running = true;

        int radius = plugin.getConfigFile().getConfig().getInt("random_settings.radius");
        int initialY = plugin.getConfigFile().getConfig().getInt("random_settings.y_level");

        BasicLocation npcLocation = null;
        int retries = plugin.getConfigFile().getConfig().getInt("retries");
        for (int i = 0; i < retries && npcLocation == null; i++) {
            BasicLocation randomLoc = plugin.getDataFile().getHubCuboid().getRandomLocation();
            World world = Bukkit.getWorld(randomLoc.getWorldName());

            for (int y = initialY - radius; y < initialY + radius; y++) {
                Block block = world.getBlockAt(randomLoc.getX(), y, randomLoc.getZ());
                if (block.getType() == Material.AIR && block.getLocation().add(0, 1, 0).getBlock().getType() == Material.AIR && BlockUtils.isBlockStatic(block.getLocation().add(0, -1, 0).getBlock().getType(), false)) {
                    npcLocation = new BasicLocation(randomLoc.getX(), randomLoc.getLocation().getBlock().getType().toString().toLowerCase().contains("slab") ? y - 0.5 : y, randomLoc.getZ(), randomLoc.getWorldName());
                    System.out.println("The npc is spawning at x:" + npcLocation.getX() + " y:" + npcLocation.getY() + " z:" + npcLocation.getZ());
                    break;
                }
            }
        }

        if (npcLocation == null) {
            this.running = false;
            return CreateGameResult.CANNOT_GET_LOCATION;
        }

        this.time = 15;
        Location finalNpcLocation = npcLocation.getLocation().add(0.5, 0, 0.5);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (time == 0) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            startMinigame(finalNpcLocation);
                        }
                    }.runTask(plugin);
                    cancel();
                }
                BaseComponent[] message = MineDown.parse(plugin.getConfigFile().getConfig().getString("message.npc_start_countdown").replaceAll("%time%", String.valueOf(time)));
                Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message));
                time--;
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L);

        return CreateGameResult.SUCCESSFUL;
    }

    public void startMinigame(final Location loc) {
        String npcName = createNPC(loc);
        this.time = plugin.getConfigFile().getConfig().getInt("max_time_to_find_npc");

        int speed_power = plugin.getConfigFile().getConfig().getInt("level_of_speed");
        String title = ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getConfig().getString("message.npc_start_title.title"));
        String subtitle = ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getConfig().getString("message.npc_start_title.subtitle"));
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(title, subtitle, 1*20, 5*20, 1*20);
            player.removePotionEffect(PotionEffectType.SPEED);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, speed_power, true, false, false));
        });

        String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getConfig().getString("message.npc_hint")
                .replaceAll("%name%", npcName)
                .replaceAll("%biome%", loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).toString()));
        Bukkit.broadcastMessage(message);

        this.gameTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (time == 0) {
                    finishGame();
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getConfig().getString("message.timeout_message")));
                    cancel();
                }
                time--;

                if (time % 5 == 0.0) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, speed_power, true, false, false)));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
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

    public void finishGame() {
        running = false;

        FireworkUtils.spawnFireworks(playingNPC.getLocation(), 1);
        npcPool.removeNPC(playingNPC.getEntityId());
        this.playingNPC = null;
        Bukkit.getOnlinePlayers().forEach(p -> p.removePotionEffect(PotionEffectType.SPEED));
    }

    @EventHandler
    public void handleNPCInteract(PlayerNPCInteractEvent e) {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();

        if (!running || this.playingNPC == null || this.playingNPC != npc) return;
        running = false;
        this.gameTask.cancel();

        npc.animation().queue(AnimationModifier.EntityAnimation.TAKE_DAMAGE).send();
        FireworkUtils.spawnFireworks(npc.getLocation(), 1);

        if (plugin.isMysqlEnabled()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getDatabase().addWin(player.getUniqueId());
                }
            }.runTaskAsynchronously(plugin);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                finishGame();
            }
        }.runTaskLater(plugin, 4L);

        player.sendTitle(
                ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getConfig().getString("message.winner_title.title")),
                ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getConfig().getString("message.winner_title.subtitle")),
                0, 4*20, 1*20);
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getConfig().getString("message.winner_message").replaceAll("%player%", player.getDisplayName())));
        plugin.runCommands(plugin.getConfigFile().getConfig().getStringList("winner_commands"), player);
    }

    public String getNPCName() {
        return this.didItStart() ? this.playingNPC.getProfile().getName() : "";
    }

    public boolean isRunning() {
        return running;
    }

    public boolean didItStart() {
        return this.playingNPC != null;
    }

    public enum CreateGameResult {
        CANNOT_GET_LOCATION(), GAME_ALREADY_RUNNING(), SUCCESSFUL()
    }
}
