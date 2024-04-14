package net.ignpurple.bungee.loadbalancer;

import net.ignpurple.api.loadbalancer.strategy.ServerPickingStrategy;
import net.ignpurple.bungee.loadbalancer.command.LoadBalancerCommand;
import net.ignpurple.bungee.loadbalancer.listener.LoadBalancerConnectListener;
import net.ignpurple.bungee.loadbalancer.strategy.registry.ServerPickingStrategyRegistry;
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

public class BungeeLoadBalancer extends Plugin {
    private final boolean newer = AsyncEvent.class.isAssignableFrom(PostLoginEvent.class);
    private ServerPickingStrategyRegistry strategyRegistry;
    private ServerPickingStrategy defaultStrategy;
    private Configuration configuration;

    public void onLoad() {
        this.initRegistries();
    }

    public void onEnable() {
        this.initConfig();
        this.initListeners();
        this.initCommands();
    }

    public boolean isNewer() {
        return this.newer;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public ServerPickingStrategyRegistry getStrategyRegistry() {
        return this.strategyRegistry;
    }

    public ServerPickingStrategy getDefaultStrategy() {
        return this.defaultStrategy;
    }

    /**
     * Configuration
     */

    public void reloadConfig() {
        this.initConfig();
    }

    /**
     * Initialization
     */

    private void initRegistries() {
        this.strategyRegistry = new ServerPickingStrategyRegistry();
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

            this.defaultStrategy = this.strategyRegistry.getStrategy(this.configuration.getString("strategy"));

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void initListeners() {
        new LoadBalancerConnectListener(this);
    }

    private void initCommands() {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new LoadBalancerCommand(this));
    }
}
