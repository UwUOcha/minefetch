package ru.uwuocha.minefetch.config;

import ru.uwuocha.minefetch.Minefetch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private List<String> orderedModules;
    private Set<String> enabledModules;
    private String lang;

    private static final List<String> DEFAULT_ASCII_ART = List.of(
            "&#199341████&#000000████&#199341████",
            "&#199341████&#000000████&#199341████",
            "&#199341████&#000000████&#199341████",
            "&#199341████&#000000████&#199341████",
            "&#000000████&#199341████&#000000████",
            "&#000000████&#199341████&#000000████",
            "&#000000██&#199341████████&#000000██",
            "&#000000██&#199341████████&#000000██",
            "&#000000██&#199341████████&#000000██",
            "&#000000██&#199341████████&#000000██",
            "&#000000██&#199341██&#000000████&#199341██&#000000██",
            "&#000000██&#199341██&#000000████&#199341██&#000000██"
    );

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

    /**
     * Загружает ASCII-арт из файла ascii.txt или использует стандартный.
     */
    private void loadAsciiArt() {
        Path asciiFile = plugin.getDataFolder().toPath().resolve("ascii.txt");

        if (Files.exists(asciiFile)) {
            try {
                this.asciiArtLines = Files.readAllLines(asciiFile);
                plugin.getLogger().info("Загружен пользовательский ASCII-арт из ascii.txt");
            } catch (IOException e) {
                plugin.getLogger().warning("Не удалось прочитать ascii.txt: " + e.getMessage() + ". Используется стандартный арт.");
                this.asciiArtLines = DEFAULT_ASCII_ART;
            }
        } else {
            this.asciiArtLines = DEFAULT_ASCII_ART;
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
