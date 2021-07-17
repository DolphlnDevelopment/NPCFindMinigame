package com.dolphln.npcfindminigame.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.dolphln.npcfindminigame.NPCFindMinigame;
import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

@CommandAlias("npcminigame|npcm")
@CommandPermission("npcminigame.use")
public class NPCMinigameCommand extends BaseCommand {

    private final NPCFindMinigame plugin;

    private final BaseComponent[] helpMessage;

    public NPCMinigameCommand(NPCFindMinigame plugin) {
        this.plugin = plugin;

        this.helpMessage = MineDown.parse("");
    }

    @Subcommand("help")
    @Default
    @CatchUnknown
    public void onHelp(CommandSender sender) {
        sender.spigot().sendMessage(this.helpMessage);
    }

}
