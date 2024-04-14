package net.ignpurple.api.loadbalancer.strategy;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.CompletableFuture;

public interface ServerPickingStrategy {

    CompletableFuture<ServerInfo> getTargetServer(ProxiedPlayer player);
}
