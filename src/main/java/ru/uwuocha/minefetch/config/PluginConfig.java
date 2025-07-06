package ru.uwuocha.minefetch.config;

import net.kyori.adventure.text.Component;
import ru.uwuocha.minefetch.Minefetch;
import ru.uwuocha.minefetch.util.MessageUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Управляет конфигурацией плагина (config.yml) и загрузкой ASCII-арта.
 */
public class PluginConfig {

    private final Minefetch plugin;
    private List<String> asciiArtLines;

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
        return plugin.getConfig().getBoolean("modules." + moduleName, true);
    }

    /**
     * Получает сообщение из config.yml и форматирует его с плейсхолдерами.
     * @param key Ключ сообщения.
     * @param defaultValue Значение по умолчанию.
     * @param placeholders Заменители.
     * @return Отформатированный компонент Component.
     */
    public Component getMessage(String key, String defaultValue, Object... placeholders) {
        String messageTemplate = plugin.getConfig().getString("messages." + key, defaultValue);
        for (int i = 0; i < placeholders.length; i++) {
            messageTemplate = messageTemplate.replace("{" + i + "}", String.valueOf(placeholders[i]));
        }
        return MessageUtils.colorize(messageTemplate);
    }
}
