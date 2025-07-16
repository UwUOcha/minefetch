package ru.uwuocha.minefetch.config;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.uwuocha.minefetch.Minefetch;
import ru.uwuocha.minefetch.util.MessageUtils;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Управляет загрузкой и доступом к языковым файлам.
 */
public class Lang {

    private final Minefetch plugin;
    private FileConfiguration langConfig;

    public Lang(Minefetch plugin) {
        this.plugin = plugin;
    }

    /**
     * Загружает языковой файл, указанный в config.yml.
     * Если файл не найден, используется en_us.yml по умолчанию.
     */
    public void load() {
        String langName = plugin.getPluginConfig().getLang();
        String langFileName = langName + ".yml";
        String langResourcePath = "lang/" + langFileName;

        // Проверяем, существует ли такой язык в ресурсах плагина
        if (plugin.getResource(langResourcePath) == null) {
            plugin.getLogger().warning("Language file '" + langFileName + "' not found in JAR. Defaulting to 'en_us.yml'.");
            langName = "en_us";
            langFileName = langName + ".yml";
            langResourcePath = "lang/" + langFileName;
        }

        File langFile = new File(plugin.getDataFolder(), langResourcePath);

        // Создаем директорию lang, если ее нет
        if (!langFile.getParentFile().exists()) {
            langFile.getParentFile().mkdirs();
        }

        // Сохраняем языковой файл из JAR, если он не существует в папке плагина
        if (!langFile.exists()) {
            plugin.saveResource(langResourcePath, false);
        }

        // Загружаем конфигурацию языка из файла
        langConfig = YamlConfiguration.loadConfiguration(langFile);

        // Устанавливаем значения по умолчанию из JAR, чтобы обеспечить наличие всех ключей
        try (InputStream defaultConfigStream = plugin.getResource(langResourcePath)) {
            if (defaultConfigStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream, StandardCharsets.UTF_8));
                langConfig.setDefaults(defaultConfig);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load default configuration for language: " + langFileName);
            e.printStackTrace();
        }
    }

    /**
     * Получает сообщение из языкового файла и форматирует его с плейсхолдерами.
     * @param key Ключ сообщения.
     * @param placeholders Заменители для плейсхолдеров {0}, {1}, и т.д.
     * @return Отформатированный Component для отправки игроку.
     */
    public Component getMessage(String key, Object... placeholders) {
        String messageTemplate = langConfig.getString(key, "§cMissing language key: " + key);
        for (int i = 0; i < placeholders.length; i++) {
            if (placeholders[i] != null) {
                messageTemplate = messageTemplate.replace("{" + i + "}", String.valueOf(placeholders[i]));
            }
        }
        return MessageUtils.colorize(messageTemplate);
    }

    /**
     * Получает доступ к конфигурации языка.
     * @return FileConfiguration языкового файла
     */
    public FileConfiguration getConfig() {
        return langConfig;
    }
}