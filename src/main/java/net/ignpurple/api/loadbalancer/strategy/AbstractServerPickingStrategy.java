package net.ignpurple.api.loadbalancer.strategy;

import net.ignpurple.bungee.loadbalancer.BungeeLoadBalancer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
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
        final boolean newProxy = this.plugin.isNewer();
        final List<ServerInfo> servers = new ArrayList<>();
        final List<String> serverIds = this.plugin.getConfiguration().getStringList("servers");
        final CountDownLatch countDownLatch = new CountDownLatch(serverIds.size());
        for (final String serverId : serverIds) {
            if (!serverId.startsWith("[") || !serverId.endsWith("]")) {
                final ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(serverId);
                this.determineServer(countDownLatch, player, serverInfo, servers, newProxy);
                continue;
            }

            final String replacedId = serverId.replace("[", "").replace("]", "").toLowerCase(Locale.ROOT);
            servers.addAll(ProxyServer.getInstance().getServers()
                .entrySet()
                .stream()
                .filter((entry) -> entry.getKey().startsWith(replacedId))
                .filter((entry) -> {
                    final int originalSize = servers.size();
                    this.determineServer(null, player, entry.getValue(), servers, newProxy);
                    return servers.size() > originalSize;
                }).map(Map.Entry::getValue)
                .toList());

            countDownLatch.countDown();
        }

        if (newProxy) {
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

    private void determineServer(CountDownLatch latch, ProxiedPlayer player, ServerInfo serverInfo, List<ServerInfo> servers, boolean newProxy) {
        if (!serverInfo.canAccess(player)) {
            if (latch == null) {
                return;
            }

            latch.countDown();
            return;
        }

        if (newProxy) {
            this.newerBungeePingLatch(latch, serverInfo, servers);
            return;
        }

        servers.add(serverInfo);
        if (latch == null) {
            return;
        }

        latch.countDown();
    }

    private void newerBungeePingLatch(CountDownLatch latch, ServerInfo serverInfo, List<ServerInfo> servers) {
        final CountDownLatch pingLatch = new CountDownLatch(1);
        serverInfo.ping((ping, err) -> {
            pingLatch.countDown();
            if (latch != null) {
                latch.countDown();
            }

            if (err != null) {
                return;
            }

            servers.add(serverInfo);
        });

        try {
            pingLatch.await(150, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
