package com.dolphln.npcfindminigame.core;

import com.dolphln.npcfindminigame.NPCFindMinigame;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class NPCManager {

    private final NPCFindMinigame plugin;

    private NPCPool npcPool;

    public NPCManager(NPCFindMinigame plugin) {
        this.plugin = plugin;

        this.npcPool = NPCPool
                .builder(plugin)
                .actionDistance(2)
                .spawnDistance(80)
                .build();
    }

    public void createNPC(String playerName, String textureValue, Location loc) {
        NPC npc = NPC
                .builder()
                .location(loc)
                .lookAtPlayer(true)
                .imitatePlayer(false)
                .profile(createProfile(playerName, textureValue))
                .build(this.npcPool);
    }

    public Profile createProfile(String playerName, String textureValue) {
        Profile profile = new Profile(UUID.randomUUID(), List.of(new Profile.Property("textures", textureValue, "")));
        profile.complete();
        profile.setName(playerName);
        return profile;
    }
}
