package net.ignpurple.bungee.loadbalancer.command;

import net.ignpurple.bungee.loadbalancer.BungeeLoadBalancer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class LoadBalancerCommand extends Command {
    private final BungeeLoadBalancer plugin;

    public LoadBalancerCommand(BungeeLoadBalancer plugin) {
        super("loadbalancer", "loadbalancer.admin", "randomhub", "bungeeloadbalancer");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            final TextComponent usage = new TextComponent("Usage: /loadbalancer reload");
            usage.setColor(ChatColor.RED);

            sender.sendMessage(usage);
            return;
        }

        this.plugin.reloadConfig();

        final TextComponent reloadedComponent = new TextComponent("Reloaded configuration successfully");
        reloadedComponent.setColor(ChatColor.GREEN);

        sender.sendMessage(reloadedComponent);
    }

}
