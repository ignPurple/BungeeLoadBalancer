package net.ignpurple.bungee.hubrandomizer;

import net.ignpurple.bungee.hubrandomizer.command.HubRandomizerCommand;
import net.ignpurple.bungee.hubrandomizer.listener.JoinListener;
import net.ignpurple.bungee.hubrandomizer.strategy.RandomStrategy;
import net.ignpurple.bungee.hubrandomizer.strategy.Strategy;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

public class BungeeHubRandomizer extends Plugin {
    private final boolean newer = AsyncEvent.class.isAssignableFrom(PostLoginEvent.class);
    private Configuration configuration;
    private RandomStrategy strategy;

    public void onEnable() {
        this.initConfig();
        this.initStrategy();
        this.initListeners();
        this.initCommands();
    }

    public boolean isNewer() {
        return this.newer;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public RandomStrategy getStrategy() {
        return this.strategy;
    }

    public void reloadConfig() {
        this.initConfig();
        this.initStrategy();
    }

    private void initConfig() {
        try {
            final File file = new File(this.getDataFolder(), "config.yml");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                Files.copy(this.getResourceAsStream("config.yml"), file.toPath());
            }

            this.configuration = ConfigurationProvider
                .getProvider(YamlConfiguration.class)
                .load(file);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void initStrategy() {
        final String strategyEnumName = this.configuration.getString("strategy").replace("-", "_").toUpperCase(Locale.ROOT);
        final Strategy strategy = Strategy.valueOf(strategyEnumName);
        this.strategy = strategy.instantiate(this);
    }

    private void initListeners() {
        new JoinListener(this);
    }

    private void initCommands() {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new HubRandomizerCommand(this));
    }
}
