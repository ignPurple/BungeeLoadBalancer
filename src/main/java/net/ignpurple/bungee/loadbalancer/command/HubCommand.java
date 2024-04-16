package net.ignpurple.bungee.loadbalancer.command;

import net.ignpurple.bungee.loadbalancer.BungeeLoadBalancer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command {
    private final BungeeLoadBalancer plugin;

    public HubCommand(BungeeLoadBalancer plugin) {
        super("hub", "loadbalancer.hub", plugin.getConfiguration().getStringList("hub-command-aliases").toArray(String[]::new));

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;
        this.plugin.getDefaultStrategy()
            .getTargetServer(player)
            .thenAccept(player::connect)
            .exceptionally((exception) -> {
                exception.printStackTrace();
                return null;
            });
    }
}
