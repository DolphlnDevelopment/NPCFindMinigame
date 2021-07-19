package com.dolphln.npcfindminigame.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.dolphln.npcfindminigame.NPCFindMinigame;
import com.dolphln.npcfindminigame.core.NPCManager;
import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("npcminigame")
@CommandPermission("npcminigame.use")
public class NPCMinigameCommand extends BaseCommand {

    private final NPCFindMinigame plugin;

    private final BaseComponent[] helpMessage;

    public NPCMinigameCommand(NPCFindMinigame plugin) {
        this.plugin = plugin;

        this.helpMessage = MineDown.parse("&gray&Commands for &aqua&/npcm&gray&:\n&white&  - /npcm pos1\n  - /npcm pos2\n  - /npcm start");
    }

    @Subcommand("help")
    @Default
    @CatchUnknown
    public void onHelp(CommandSender sender) {
        sender.spigot().sendMessage(this.helpMessage);
    }

    @Subcommand("pos1")
    public void onPos1(CommandSender sender) {
        Player player = playerCheck(sender);
        if (player == null) return;

        Location playerPos = player.getLocation();

        plugin.getDataFile().getHubCuboid().setPoint1(playerPos.getBlockX(), playerPos.getBlockY(), playerPos.getBlockZ());
        plugin.getDataFile().setLocation("hub.pos1", playerPos);
        plugin.getDataFile().save();

        player.spigot().sendMessage(MineDown.parse("&green&You've set the position 1."));
    }

    @Subcommand("pos2")
    public void onPos2(CommandSender sender) {
        Player player = playerCheck(sender);
        if (player == null) return;

        Location playerPos = player.getLocation();

        plugin.getDataFile().getHubCuboid().setPoint2(playerPos.getBlockX(), playerPos.getBlockY(), playerPos.getBlockZ());
        plugin.getDataFile().setLocation("hub.pos2", playerPos);
        plugin.getDataFile().save();

        player.spigot().sendMessage(MineDown.parse("&green&You've set the position 2."));
    }

    @Subcommand("start")
    public void onStart(CommandSender sender) {
        NPCManager.CreateGameResult res = plugin.getNpcManager().createMinigame();

        if (res == NPCManager.CreateGameResult.CANNOT_GET_LOCATION) {
            sender.spigot().sendMessage(MineDown.parse("&red&Sorry, but I couldn't find a good location for the NPC. Please, adjust the parameters on the config."));
        } else if (res == NPCManager.CreateGameResult.GAME_ALREADY_RUNNING) {
            sender.spigot().sendMessage(MineDown.parse("&red&Sorry, but a game already started."));
        } else if (res == NPCManager.CreateGameResult.SUCCESSFUL) {
            sender.spigot().sendMessage(MineDown.parse("&green&The game has been started. Enjoy it!"));
        }
    }

    public Player playerCheck(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.spigot().sendMessage(MineDown.parse("&red&You're a console, you cannot run this command!"));
            return null;
        }
        return (Player) sender;
    }

}
