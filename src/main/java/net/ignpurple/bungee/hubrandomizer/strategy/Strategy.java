package net.ignpurple.bungee.hubrandomizer.strategy;

import net.ignpurple.bungee.hubrandomizer.BungeeHubRandomizer;
import net.ignpurple.bungee.hubrandomizer.strategy.impl.LeastPlayersStrategy;
import net.ignpurple.bungee.hubrandomizer.strategy.impl.RandomServerStrategy;

import java.lang.reflect.InvocationTargetException;

public enum Strategy {
    RANDOM(RandomServerStrategy.class),
    LEAST_PLAYERS(LeastPlayersStrategy.class);

    private final Class<? extends RandomStrategy> strategyClass;

    Strategy(Class<? extends RandomStrategy> strategyClass) {
        this.strategyClass = strategyClass;
    }

    public RandomStrategy instantiate(BungeeHubRandomizer plugin) {
        try {
            return this.strategyClass.getConstructor(BungeeHubRandomizer.class).newInstance(plugin);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of a Strategy class", e);
        }
    }
}
