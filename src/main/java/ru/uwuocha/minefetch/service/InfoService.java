package ru.uwuocha.minefetch.service;

import com.sun.management.OperatingSystemMXBean;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import ru.uwuocha.minefetch.Minefetch;
import ru.uwuocha.minefetch.config.PluginConfig;
import ru.uwuocha.minefetch.util.MessageUtils;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для сбора и форматирования информации о сервере.
 */
public class InfoService {

    private final Minefetch plugin;
    private final PluginConfig config;
    private final long serverStartTime;

    public InfoService(Minefetch plugin, PluginConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.serverStartTime = System.currentTimeMillis();
    }

    /**
     * Собирает полную информацию о сервере на основе конфигурации.
     * @return Список компонентов (Component) с информацией.
     */
    public List<Component> getServerInfo() {
        List<Component> info = new ArrayList<>();

        // Название сервера
        if (config.isModuleEnabled("server-name")) {
            String serverName = plugin.getConfig().getString("server-name", "Minecraft Server");
            info.add(config.getMessage("server-name", "&#199341&l{0}", serverName));
            info.add(config.getMessage("separator", "&7{0}", "─".repeat(serverName.length())));
        }

        // Версия Minecraft
        if (config.isModuleEnabled("minecraft-version")) {
            info.add(config.getMessage("minecraft-version", "&#199341Minecraft&f: {0}", Bukkit.getMinecraftVersion()));
        }

        // Версия сервера (Paper, Spigot, etc.)
        if (config.isModuleEnabled("server-version")) {
            info.add(config.getMessage("server-version", "&#199341Сервер&f: {0}", Bukkit.getName() + " " + Bukkit.getBukkitVersion()));
        }

        // Игроки
        if (config.isModuleEnabled("players")) {
            String players = Bukkit.getOnlinePlayers().size() + " / " + Bukkit.getMaxPlayers();
            info.add(config.getMessage("players", "&#199341Игроки&f: {0}", players));
        }

        // Плагины
        if (config.isModuleEnabled("plugins")) {
            info.add(config.getMessage("plugins", "&#199341Плагины&f: {0}", Bukkit.getPluginManager().getPlugins().length));
        }

        // Память (RAM)
        if (config.isModuleEnabled("memory")) {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory() / 1048576; // в MB
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1048576; // в MB
            long percent = maxMemory > 0
                    ? (usedMemory * 100) / maxMemory
                    : 0;
            info.add(config.getMessage("memory", "&#199341Память&f: {0}MB / {1}MB &7({2}%)", usedMemory, maxMemory, percent));
        }

        // Нагрузка на CPU
        if (config.isModuleEnabled("cpu")) {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            double cpuLoad = osBean.getProcessCpuLoad() * 100;
            if (cpuLoad < 0) cpuLoad = 0; // Иногда может вернуть -1
            info.add(config.getMessage("cpu", "&#199341CPU&f: {0} &7({1}%)", osBean.getArch(), String.format("%.1f", cpuLoad)));
        }

        // TPS
        if (config.isModuleEnabled("tps")) {
            // Используем нативный метод Paper API
            double tps = Bukkit.getTPS()[0];
            TextColor tpsColor = tps > 18.0 ? NamedTextColor.WHITE : tps > 15.0 ? NamedTextColor.YELLOW : NamedTextColor.RED;
            Component tpsComponent = Component.text(String.format("%.2f", tps), tpsColor);
            info.add(config.getMessage("tps", "&#199341TPS&f: ").append(tpsComponent));
        }

        // Аптайм
        if (config.isModuleEnabled("uptime")) {
            long uptimeMillis = System.currentTimeMillis() - serverStartTime;
            info.add(config.getMessage("uptime", "&#199341Аптайм&f: {0}", formatUptime(uptimeMillis)));
        }

        // Миры
        if (config.isModuleEnabled("worlds")) {
            info.add(config.getMessage("worlds", "&#199341Миры&f: {0}", Bukkit.getWorlds().size()));
        }

        // Java версия
        if (config.isModuleEnabled("java-version")) {
            info.add(config.getMessage("java-version", "&#199341Java&f: {0}", System.getProperty("java.version")));
        }

        return info;
    }

    /**
     * Форматирует время в миллисекундах в читаемую строку (дни, часы, минуты).
     */
    private String formatUptime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return String.format("%dд %dч %dм", days, hours % 24, minutes % 60);
        } else if (hours > 0) {
            return String.format("%dч %dм", hours, minutes % 60);
        } else {
            return String.format("%dм", minutes);
        }
    }
}
