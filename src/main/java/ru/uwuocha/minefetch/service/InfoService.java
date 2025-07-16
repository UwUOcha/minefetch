package ru.uwuocha.minefetch.service;

import com.sun.management.OperatingSystemMXBean;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import ru.uwuocha.minefetch.Minefetch;
import ru.uwuocha.minefetch.config.PluginConfig;
import ru.uwuocha.minefetch.util.TimeFormatter;

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
    private final TimeFormatter timeFormatter;

    public InfoService(Minefetch plugin, PluginConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.serverStartTime = System.currentTimeMillis();
        this.timeFormatter = new TimeFormatter(plugin.getLang());
    }

    /**
     * Собирает полную информацию о сервере на основе конфигурации.
     * @return Список компонентов (Component) с информацией.
     */
    public List<Component> getServerInfo() {
        List<Component> info = new ArrayList<>();
        List<String> orderedModules = config.getOrderedModules();

        for (String moduleName : orderedModules) {
            switch (moduleName) {
                case "server-name":
                    String serverName = plugin.getConfig().getString("server-name", "Minecraft Server");
                    info.add(plugin.getLang().getMessage("server-name", serverName));
                    info.add(plugin.getLang().getMessage("separator", "─".repeat(serverName.length())));
                    break;

                case "minecraft-version":
                    info.add(plugin.getLang().getMessage("minecraft-version", Bukkit.getMinecraftVersion()));
                    break;

                case "server-version":
                    info.add(plugin.getLang().getMessage("server-version", Bukkit.getName() + " " + Bukkit.getBukkitVersion()));
                    break;

                case "players":
                    String players = Bukkit.getOnlinePlayers().size() + " / " + Bukkit.getMaxPlayers();
                    info.add(plugin.getLang().getMessage("players", players));
                    break;

                case "plugins":
                    info.add(plugin.getLang().getMessage("plugins", Bukkit.getPluginManager().getPlugins().length));
                    break;

                case "memory":
                    Runtime runtime = Runtime.getRuntime();
                    long maxMemory = runtime.maxMemory() / 1048576; // в MB
                    long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1048576; // в MB
                    long percent = maxMemory > 0 ? (usedMemory * 100) / maxMemory : 0;
                    info.add(plugin.getLang().getMessage("memory", usedMemory, maxMemory, percent));
                    break;

                case "cpu":
                    OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
                    double cpuLoad = osBean.getProcessCpuLoad() * 100;
                    if (cpuLoad < 0) cpuLoad = 0; // Иногда может вернуть -1
                    info.add(plugin.getLang().getMessage("cpu", osBean.getArch(), String.format("%.1f", cpuLoad)));
                    break;

                case "tps":
                    double tps = Bukkit.getTPS()[0];
                    TextColor tpsColor = tps > 18.0 ? NamedTextColor.WHITE : tps > 15.0 ? NamedTextColor.YELLOW : NamedTextColor.RED;
                    Component tpsComponent = Component.text(String.format("%.2f", tps), tpsColor);
                    info.add(plugin.getLang().getMessage("tps").append(tpsComponent));
                    break;

                case "uptime":
                    long uptimeMillis = System.currentTimeMillis() - serverStartTime;
                    info.add(plugin.getLang().getMessage("uptime", timeFormatter.formatUptime(uptimeMillis)));
                    break;

                case "worlds":
                    info.add(plugin.getLang().getMessage("worlds", Bukkit.getWorlds().size()));
                    break;

                case "java-version":
                    info.add(plugin.getLang().getMessage("java-version", System.getProperty("java.version")));
                    break;
            }
        }

        return info;
    }
}
