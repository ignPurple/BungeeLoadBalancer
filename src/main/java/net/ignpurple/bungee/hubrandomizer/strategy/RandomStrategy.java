package net.ignpurple.bungee.hubrandomizer.strategy;

import net.ignpurple.bungee.hubrandomizer.BungeeHubRandomizer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class RandomStrategy {
    private final BungeeHubRandomizer plugin;

    public RandomStrategy(BungeeHubRandomizer plugin) {
        this.plugin = plugin;
    }

    public abstract void getRandomServer(ProxiedPlayer player, Consumer<ServerInfo> serverInfoCallback);

    protected void getAvailableServers(ProxiedPlayer player, Consumer<List<ServerInfo>> serverInfoCallback) {
        final boolean newer = this.plugin.isNewer();
        final List<ServerInfo> servers = new ArrayList<>();
        final List<String> serverIds = this.plugin.getConfiguration().getStringList("servers");
        final CountDownLatch countDownLatch = new CountDownLatch(serverIds.size());
        for (final String serverId : serverIds) {
            final ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(serverId);
            if (!serverInfo.canAccess(player)) {
                countDownLatch.countDown();
                continue;
            }

            if (newer) {
                this.newerPingLatch(countDownLatch, serverInfo, servers);
                continue;
            }

            servers.add(serverInfo);
            countDownLatch.countDown();
        }

        if (newer) {
            try {
                countDownLatch.await(250, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                serverInfoCallback.accept(Collections.emptyList());
                throw new RuntimeException(e);
            }
        }

        serverInfoCallback.accept(servers);
    }

    private void newerPingLatch(CountDownLatch latch, ServerInfo serverInfo, List<ServerInfo> servers) {
        serverInfo.ping((ping, err) -> {
            latch.countDown();
            if (err != null) {
                return;
            }

            servers.add(serverInfo);
        });
    }
}
