package net.ignpurple.bungee.hubrandomizer.listener;

import net.ignpurple.bungee.hubrandomizer.BungeeHubRandomizer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.logging.Level;

public class JoinListener implements Listener {
    private final BungeeHubRandomizer plugin;

    public JoinListener(BungeeHubRandomizer plugin) {
        this.plugin = plugin;
        if (this.plugin.isNewer()) {
            new PostLoginListener(this.plugin);
            return;
        }

        ProxyServer.getInstance().getPluginManager().registerListener(this.plugin, this);
        ProxyServer.getInstance().getLogger().log(Level.INFO, "[ BUNGEEHUBRANDOMIZER ] It is recommended to upgrade to the latest version of BungeeCord for BungeeHubRandomizer to function properly.");
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (event.getPlayer().getServer() != null) {
            return;
        }

        this.plugin.getStrategy().getRandomServer(event.getPlayer(), (randomServer) -> {
            if (randomServer == null) {
                return;
            }

            event.setTarget(randomServer);
            event.setCancelled(false);
        });
    }
}
