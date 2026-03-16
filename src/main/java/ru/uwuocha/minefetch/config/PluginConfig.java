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
 * Управляет конфигурацией плагина (config.yml) и загрузкой ASCII-арта.
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
     * Загружает или перезагружает конфигурацию и ASCII-арт.
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
     * Возвращает строки ASCII-арта.
     * @return Список строк.
     */
    public List<String> getAsciiArtLines() {
        return Collections.unmodifiableList(asciiArtLines);
    }

    /**
     * Возвращает строки ASCII-арта для консоли (удвоенные символы).
     * @return Список строк.
     */
    public List<String> getConsoleAsciiArtLines() {
        return Collections.unmodifiableList(consoleAsciiArtLines);
    }

    /**
     * Проверяет, включен ли определенный модуль в конфигурации.
     * @param moduleName Имя модуля.
     * @return true, если модуль включен.
     */
    public boolean isModuleEnabled(String moduleName) {
        return enabledModules.contains(moduleName);
    }

    /**
     * Возвращает упорядоченный список включенных модулей.
     * @return Список имен модулей.
     */
    public List<String> getOrderedModules() {
        return Collections.unmodifiableList(orderedModules);
    }

    /**
     * Возвращает язык, выбранный в конфигурации.
     * @return Строка с названием языка (например, "en_us").
     */
    public String getLang() {
        return lang;
    }
}
