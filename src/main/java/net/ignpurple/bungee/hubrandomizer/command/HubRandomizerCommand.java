package net.ignpurple.bungee.hubrandomizer.command;

import net.ignpurple.bungee.hubrandomizer.BungeeHubRandomizer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class HubRandomizerCommand extends Command {
    private final BungeeHubRandomizer plugin;

    public HubRandomizerCommand(BungeeHubRandomizer plugin) {
        super("hubrandomizer", "hubrandomizer.config", "randomhub", "hubrandom");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.plugin.reloadConfig();
        final TextComponent component = new TextComponent("Reloaded configuration successfully");
        component.setColor(ChatColor.GREEN);
        sender.sendMessage(component);
    }
}
