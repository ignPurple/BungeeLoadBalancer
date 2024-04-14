package net.ignpurple.bungee.hubrandomizer.strategy.impl;

import net.ignpurple.bungee.hubrandomizer.BungeeHubRandomizer;
import net.ignpurple.bungee.hubrandomizer.strategy.RandomStrategy;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class RandomServerStrategy extends RandomStrategy {

    public RandomServerStrategy(BungeeHubRandomizer plugin) {
        super(plugin);
    }

    @Override
    public void getRandomServer(ProxiedPlayer player, Consumer<ServerInfo> serverInfoCallback) {
        this.getAvailableServers(player, (servers) -> {
            if (servers.size() == 0) {
                serverInfoCallback.accept(null);
                return;
            }

            serverInfoCallback.accept(servers.get(ThreadLocalRandom.current().nextInt(servers.size())));
        });
    }
}
