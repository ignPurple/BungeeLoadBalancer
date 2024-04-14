package net.ignpurple.bungee.loadbalancer.strategy.registry;

import net.ignpurple.api.loadbalancer.strategy.ServerPickingStrategy;
import net.ignpurple.api.loadbalancer.strategy.comparator.ServerInfoComparators;
import net.ignpurple.bungee.loadbalancer.strategy.ComparatorPickingStrategy;
import net.ignpurple.bungee.loadbalancer.strategy.RandomPickingStrategy;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerPickingStrategyRegistry {
    private final Map<String, ServerPickingStrategy> strategies;

    public ServerPickingStrategyRegistry() {
        this.strategies = new ConcurrentHashMap<>();
        this.registerDefaults();
    }

    public ServerPickingStrategy getStrategy(String id) {
        final String formattedId = this.formatId(id);
        if (!this.strategies.containsKey(formattedId)) {
            throw new IllegalArgumentException("The Registered Strategies does not contain a strategy for id (" + id + ")");
        }

        return this.strategies.get(formattedId);
    }

    public void register(String id, ServerPickingStrategy strategy) {
        final String formattedId = this.formatId(id);
        if (this.strategies.containsKey(formattedId)) {
            throw new IllegalArgumentException("The Registered Strategies already contains a value with id (" + id + ")");
        }

        this.strategies.put(formattedId, strategy);
    }

    private String formatId(String id) {
        return id.toLowerCase(Locale.ROOT);
    }

    private void registerDefaults() {
        this.register("least-players", new ComparatorPickingStrategy(ServerInfoComparators.LEAST_PLAYERS));
        this.register("most-players", new ComparatorPickingStrategy(ServerInfoComparators.MOST_PLAYERS));
        this.register("random", new RandomPickingStrategy());
    }
}
