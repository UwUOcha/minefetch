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
 * Manages loading and providing access to language files.
 */
public class Lang {

    private final Minefetch plugin;
    private FileConfiguration langConfig;

    public Lang(Minefetch plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads the language file specified in config.yml.
     * If the file is not found, en_us.yml is used by default.
     */
    public void load() {
        String langName = plugin.getPluginConfig().getLang();
        String langFileName = langName + ".yml";
        String langResourcePath = "lang/" + langFileName;

        // Check if such language exists in the plugin resources
        if (plugin.getResource(langResourcePath) == null) {
            plugin.getLogger().warning("Language file '" + langFileName + "' not found in JAR. Defaulting to 'en_us.yml'.");
            langName = "en_us";
            langFileName = langName + ".yml";
            langResourcePath = "lang/" + langFileName;
        }

        File langFile = new File(plugin.getDataFolder(), langResourcePath);

        // Create the lang directory if it doesn't exist
        if (!langFile.getParentFile().exists()) {
            langFile.getParentFile().mkdirs();
        }

        // Save the language file from the JAR if it doesn't exist in the plugin folder
        if (!langFile.exists()) {
            plugin.saveResource(langResourcePath, false);
        }

        // Load language configuration from the file
        langConfig = YamlConfiguration.loadConfiguration(langFile);

        // Set default values from the JAR to ensure all keys are present
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
     * Gets a message from the language file and formats it with placeholders.
     * @param key The message key.
     * @param placeholders Replacements for placeholders {0}, {1}, etc.
     * @return Formatted Component to send to the player.
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
     * Gets access to the language configuration.
     * @return FileConfiguration of the language file
     */
    public FileConfiguration getConfig() {
        return langConfig;
    }
}