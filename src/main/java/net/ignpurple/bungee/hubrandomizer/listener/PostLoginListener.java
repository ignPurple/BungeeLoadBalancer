package net.ignpurple.bungee.hubrandomizer.listener;

import net.ignpurple.bungee.hubrandomizer.BungeeHubRandomizer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener {
    private final BungeeHubRandomizer plugin;

    public PostLoginListener(BungeeHubRandomizer plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(this.plugin, this);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        event.registerIntent(this.plugin);
        this.plugin.getStrategy().getRandomServer(event.getPlayer(), (randomServer) -> {
            event.completeIntent(this.plugin);
            if (randomServer == null) {
                return;
            }

            event.setTarget(randomServer);
        });
    }
}
