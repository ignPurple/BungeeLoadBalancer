package net.ignpurple.bungee.hubrandomizer.strategy.impl;

import net.ignpurple.bungee.hubrandomizer.BungeeHubRandomizer;
import net.ignpurple.bungee.hubrandomizer.strategy.RandomStrategy;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Comparator;
import java.util.function.Consumer;

public class LeastPlayersStrategy extends RandomStrategy {

    public LeastPlayersStrategy(BungeeHubRandomizer plugin) {
        super(plugin);
    }

    @Override
    public void getRandomServer(ProxiedPlayer player, Consumer<ServerInfo> serverInfoCallback) {
        this.getAvailableServers(player, (servers) -> serverInfoCallback.accept(servers.stream()
            .min(Comparator.comparingInt(serverInfo -> serverInfo.getPlayers().size()))
            .orElse(null)
        ));
    }
}
