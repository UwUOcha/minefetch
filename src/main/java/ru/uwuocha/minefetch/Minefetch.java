package ru.uwuocha.minefetch;

import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import ru.uwuocha.minefetch.command.MinefetchCommand;
import ru.uwuocha.minefetch.config.PluginConfig;
import ru.uwuocha.minefetch.config.Lang;
import ru.uwuocha.minefetch.service.InfoService;

import java.lang.reflect.Field;

/**
 * Main class of the Minefetch plugin.
 * Responsible for plugin initialization, shutdown, and component management.
 */
public final class Minefetch extends JavaPlugin {

    private PluginConfig pluginConfig;
    private InfoService infoService;
    private Lang lang;

    @Override
    public void onEnable() {
        // Initialize configuration manager
        this.pluginConfig = new PluginConfig(this);
        pluginConfig.load();

        // Initialize language manager
        this.lang = new Lang(this);
        lang.load();

        // Initialize info gathering service
        this.infoService = new InfoService(this, pluginConfig);

        // Register command programmatically
        registerCommand();

        getLogger().info("Плагин Minefetch успешно включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин Minefetch отключен!");
    }

    /**
     * Registers the /minefetch command programmatically using CommandMap.
     * This is the correct way for Paper/Purpur plugins.
     */
    private void registerCommand() {
        try {
            // Get access to the server's CommandMap via reflection
            final Field bukkitCommandMap = getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(getServer());

            // Create and register our command
            commandMap.register(
                    "minefetch", // Command name, used as default prefix
                    new MinefetchCommand(this)
            );
            getLogger().info("Команда /minefetch успешно зарегистрирована.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().severe("Не удалось зарегистрировать команду /minefetch! Плагин не будет работать корректно.");
            e.printStackTrace();
            // Disable the plugin if the command cannot be registered
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Reloads plugin configuration and language files.
     */
    public void reload() {
        pluginConfig.load();
        lang.load();
    }

    /**
     * Returns the configuration manager instance.
     * @return PluginConfig
     */
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    /**
     * Returns the info gathering service instance.
     * @return InfoService
     */
    public InfoService getInfoService() {
        return infoService;
    }

    /**
     * Provides access to language messages.
     * @return Lang instance.
     */
    public Lang getLang() {
        return lang;
    }
}
