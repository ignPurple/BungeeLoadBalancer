package net.ignpurple.bungee.loadbalancer.listener;

import net.ignpurple.bungee.loadbalancer.BungeeLoadBalancer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.logging.Level;

public class LoadBalancerConnectListener implements Listener {
    private final BungeeLoadBalancer plugin;

    public LoadBalancerConnectListener(BungeeLoadBalancer plugin) {
        this.plugin = plugin;
        if (this.plugin.isNewer()) {
            new LoadBalancerLoginListener(this.plugin);
            return;
        }

        final ProxyServer server = ProxyServer.getInstance();
        server.getPluginManager().registerListener(this.plugin, this);

        this.plugin.getLogger().log(Level.INFO, "It is recommended to upgrade to the latest version of BungeeCord for BungeeLoadBalancer to function properly.");
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        if (player.getServer() != null) {
            return;
        }

        this.plugin.getDefaultStrategy()
            .getTargetServer(player)
            .thenAccept((serverInfo) -> {
                if (serverInfo == null) {
                    return;
                }

                event.setTarget(serverInfo);
            });
    }
}
