package net.ignpurple.api.loadbalancer.strategy.comparator;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.Comparator;

public class ServerInfoComparators {
    public static final Comparator<ServerInfo> LEAST_PLAYERS = Comparator
        .comparingInt(serverInfo -> serverInfo.getPlayers().size());

    public static final Comparator<ServerInfo> MOST_PLAYERS = LEAST_PLAYERS.reversed();

    private ServerInfoComparators() {}
}
