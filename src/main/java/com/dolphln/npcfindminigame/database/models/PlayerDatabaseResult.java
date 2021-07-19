package com.dolphln.npcfindminigame.database.models;

import java.util.UUID;

public record PlayerDatabaseResult(UUID uuid, String playerName, int wins) {

}
