package net.ignpurple.bungee.loadbalancer.listener;

import net.ignpurple.bungee.loadbalancer.BungeeLoadBalancer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LoadBalancerLoginListener implements Listener {
    private final BungeeLoadBalancer plugin;

    public LoadBalancerLoginListener(BungeeLoadBalancer plugin) {
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerListener(this.plugin, this);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        event.registerIntent(this.plugin);

        this.plugin.getDefaultStrategy()
            .getTargetServer(event.getPlayer())
            .thenAccept((serverInfo) -> {
                if (serverInfo == null) {
                    event.completeIntent(this.plugin);
                    return;
                }

                event.setTarget(serverInfo);
                event.completeIntent(this.plugin);
            });
    }
}
