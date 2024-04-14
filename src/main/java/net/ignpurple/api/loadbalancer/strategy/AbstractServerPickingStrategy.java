package net.ignpurple.api.loadbalancer.strategy;

import net.ignpurple.bungee.loadbalancer.BungeeLoadBalancer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class AbstractServerPickingStrategy implements ServerPickingStrategy {
    protected final BungeeLoadBalancer plugin;

    public AbstractServerPickingStrategy() {
        this.plugin = (BungeeLoadBalancer) ProxyServer.getInstance().getPluginManager().getPlugin("BungeeLoadBalancer");
    }

    protected List<ServerInfo> getAvailableServers(ProxiedPlayer player) {
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
                this.newerBungeePingLatch(countDownLatch, serverInfo, servers);
                continue;
            }

            servers.add(serverInfo);
            countDownLatch.countDown();
        }

        if (newer) {
            try {
                countDownLatch.await(250, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                return Collections.emptyList();
            }
        }

        return servers;
    }

    protected CompletableFuture<ServerInfo> createFuture(Supplier<ServerInfo> serverInfoSupplier) {
        if (this.plugin.isNewer()) {
            return CompletableFuture.supplyAsync(serverInfoSupplier, this.plugin.getExecutorService());
        }

        return CompletableFuture.completedFuture(serverInfoSupplier.get());
    }

    private void newerBungeePingLatch(CountDownLatch latch, ServerInfo serverInfo, List<ServerInfo> servers) {
        serverInfo.ping((ping, err) -> {
            latch.countDown();
            if (err != null) {
                return;
            }

            servers.add(serverInfo);
        });
    }
}
