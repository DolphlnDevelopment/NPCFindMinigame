package com.dolphln.npcfindminigame.database;

public class Query {

    // Use ? for parameters to be set

    static final String[] createTables = new String[]{
            "CREATE TABLE `players` ( `uuid` TINYTEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, `playerName` TINYTEXT CHARACTER SET utf8 COLLATE utf8_bin NULL , `wins` INT NOT NULL DEFAULT 0 , PRIMARY KEY (`uuid`(36))) ENGINE = InnoDB;",
    };

    static final String addPlayer = "INSERT INTO players(uuid) VALUES (?);";

    static final String getPlayer = "SELECT * FROM `players` WHERE `uuid` = ?;";

    static final String updatePlayerName = "UPDATE `players` SET `playerName` = ? WHERE `uuid` = ?;";

    static final String updateWins = "UPDATE `players` SET `wins` = `wins` + 1 WHERE `uuid` = ?";

    static final String getTopWins = "SELECT * FROM `players` ORDER BY `wins` DESC LIMIT ?";

}
