package net.ignpurple.bungee.loadbalancer.strategy;

import net.ignpurple.api.loadbalancer.strategy.AbstractServerPickingStrategy;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class RandomPickingStrategy extends AbstractServerPickingStrategy {

    @Override
    public CompletableFuture<ServerInfo> getTargetServer(ProxiedPlayer player) {
        return this.createFuture(() -> {
            final List<ServerInfo> availableServers = this.getAvailableServers(player);
            return availableServers.get(ThreadLocalRandom.current().nextInt(availableServers.size()));
        });
    }
}
