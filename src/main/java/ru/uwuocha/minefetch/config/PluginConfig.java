package ru.uwuocha.minefetch.config;

import ru.uwuocha.minefetch.Minefetch;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages plugin configuration (config.yml) and ASCII art loading.
 */
public class PluginConfig {

    private final Minefetch plugin;
    private List<String> asciiArtLines;
    private List<String> consoleAsciiArtLines;
    private List<String> orderedModules;
    private Set<String> enabledModules;
    private String lang;

    public PluginConfig(Minefetch plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads or reloads the configuration and ASCII art.
     */
    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        loadAsciiArt();
        this.lang = plugin.getConfig().getString("lang", "ru_ru");
        this.orderedModules = plugin.getConfig().getStringList("modules");
        this.enabledModules = new HashSet<>(this.orderedModules);
    }

    private void loadAsciiArt() {
        File asciiFile = new File(plugin.getDataFolder(), "ascii.yml");

        if (!asciiFile.exists()) {
            plugin.saveResource("ascii.yml", false);
        }

        FileConfiguration asciiConfig = YamlConfiguration.loadConfiguration(asciiFile);

        this.asciiArtLines = asciiConfig.getStringList("player");
        this.consoleAsciiArtLines = asciiConfig.getStringList("console");

        if (this.asciiArtLines.isEmpty() && this.consoleAsciiArtLines.isEmpty()) {
            plugin.getLogger().warning("Списки player и console в ascii.yml пусты или отсутствуют. Используется пустой арт.");
        } else {
            plugin.getLogger().info("Загружен ASCII-арт из ascii.yml");
        }
    }

    /**
     * Returns ASCII art lines.
     * @return List of strings.
     */
    public List<String> getAsciiArtLines() {
        return Collections.unmodifiableList(asciiArtLines);
    }

    /**
     * Returns ASCII art lines for console (doubled characters).
     * @return List of strings.
     */
    public List<String> getConsoleAsciiArtLines() {
        return Collections.unmodifiableList(consoleAsciiArtLines);
    }

    /**
     * Checks if a specific module is enabled in the configuration.
     * @param moduleName Module name.
     * @return true if the module is enabled.
     */
    public boolean isModuleEnabled(String moduleName) {
        return enabledModules.contains(moduleName);
    }

    /**
     * Returns an ordered list of enabled modules.
     * @return List of module names.
     */
    public List<String> getOrderedModules() {
        return Collections.unmodifiableList(orderedModules);
    }

    /**
     * Returns the language selected in the configuration.
     * @return String with the language name (e.g., "en_us").
     */
    public String getLang() {
        return lang;
    }
}
