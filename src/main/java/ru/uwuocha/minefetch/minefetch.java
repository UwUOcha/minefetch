package ru.uwuocha.minefetch;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class minefetch extends JavaPlugin {

    private static final String DEFAULT_ASCII_ART =
            "&#199341████&#000000████&#199341████\n" +
            "&#199341████&#000000████&#199341████\n" +
            "&#199341████&#000000████&#199341████\n" +
            "&#199341████&#000000████&#199341████\n" +
            "&#000000████&#199341████&#000000████\n" +
            "&#000000████&#199341████&#000000████\n" +
            "&#000000██&#199341████████&#000000██\n" +
            "&#000000██&#199341████████&#000000██\n" +
            "&#000000██&#199341████████&#000000██\n" +
            "&#000000██&#199341████████&#000000██\n" +
            "&#000000██&#199341██&#000000████&#199341██&#000000██\n" +
            "&#000000██&#199341██&#000000████&#199341██&#000000██\n";

    private String asciiArt;
    private long lastCpuTime = 0;
    private long lastSystemTime = 0;
    private long serverStartTime;

    // Паттерн для HEX цветов
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    @Override
    public void onEnable() {
        // Сохраняем время запуска
        serverStartTime = System.currentTimeMillis();

        // Создаем конфигурацию
        saveDefaultConfig();

        // Загружаем ASCII-арт
        loadAsciiArt();

        // Регистрируем команду программно
        registerCommand();

        getLogger().info("Minefetch плагин включен!");
    }

    private void registerCommand() {
        try {
            // Альтернативный способ регистрации команды для Paper
            org.bukkit.command.CommandMap commandMap = Bukkit.getCommandMap();

            // Создаем простую команду
            org.bukkit.command.Command command = new org.bukkit.command.Command("minefetch",
                    "Показать информацию о сервере",
                    "/minefetch [reload]",
                    java.util.Arrays.asList("mf", "fetch")) {

                @Override
                public boolean execute(org.bukkit.command.CommandSender sender, String commandLabel, String[] args) {
                    return onCommand(sender, this, commandLabel, args);
                }
            };

            command.setPermission("minefetch.use");
            commandMap.register("minefetch", command);

            getLogger().info("Команда /minefetch зарегистрирована успешно!");
        } catch (Exception e) {
            getLogger().severe("Не удалось зарегистрировать команду: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Minefetch плагин отключен!");
    }

    private void loadAsciiArt() {
        Path asciiFile = Paths.get(getDataFolder().getAbsolutePath(), "ascii.txt");

        if (Files.exists(asciiFile)) {
            try {
                String rawAscii = String.join("\n", Files.readAllLines(asciiFile));
                asciiArt = translateColors(rawAscii);
                getLogger().info("Загружен пользовательский ASCII-арт из ascii.txt");
            } catch (IOException e) {
                getLogger().warning("Не удалось загрузить ascii.txt, используется стандартный арт");
                asciiArt = translateColors(DEFAULT_ASCII_ART);
            }
        } else {
            asciiArt = translateColors(DEFAULT_ASCII_ART);
        }
    }

    /**
     * Переводит цветовые коды (&) и HEX цвета (&#RRGGBB) в ChatColor
     */
    private String translateColors(String text) {
        if (text == null) return "";

        // Сначала обрабатываем HEX цвета
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            try {
                ChatColor hexColor = ChatColor.of("#" + hexCode);
                text = text.replace("&#" + hexCode, hexColor.toString());
            } catch (Exception e) {
                // Если HEX код неверный, просто удаляем его
                text = text.replace("&#" + hexCode, "");
            }
        }

        // Затем обрабатываем обычные цветовые коды
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Получает сообщение из конфигурации с поддержкой цветов
     */
    private String getMessage(String key, String defaultValue) {
        String message = getConfig().getString("messages." + key, defaultValue);
        return translateColors(message);
    }

    /**
     * Получает сообщение из конфигурации с заменой плейсхолдеров
     */
    private String getMessage(String key, String defaultValue, Object... placeholders) {
        String message = getMessage(key, defaultValue);
        for (int i = 0; i < placeholders.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(placeholders[i]));
        }
        return message;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("minefetch")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("minefetch.reload")) {
                    reloadConfig();
                    loadAsciiArt();
                    sender.sendMessage(getMessage("reload-success", "&aКонфигурация Minefetch перезагружена!"));
                    return true;
                } else {
                    sender.sendMessage(getMessage("no-permission", "&cУ вас нет прав на перезагрузку конфигурации!"));
                    return true;
                }
            }

            // Показываем информацию о сервере
            showServerInfo(sender);
            return true;
        }
        return false;
    }

    private void showServerInfo(CommandSender sender) {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> info = new ArrayList<>();
                List<String> asciiLines = List.of(asciiArt.split("\n"));

                // Получаем информацию о сервере
                List<String> serverInfo = getServerInfo();

                // Объединяем ASCII-арт с информацией
                int maxLines = Math.max(asciiLines.size(), serverInfo.size());

                for (int i = 0; i < maxLines; i++) {
                    StringBuilder line = new StringBuilder();

                    // Добавляем ASCII-арт
                    if (i < asciiLines.size()) {
                        line.append(asciiLines.get(i)); // ASCII уже содержит цвета
                    } else {
                        line.append("              "); // Пустое место для выравнивания
                    }

                    // Добавляем информацию
                    if (i < serverInfo.size()) {
                        line.append("  ").append(serverInfo.get(i));
                    }

                    info.add(line.toString());
                }

                // Отправляем информацию в основном потоке
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (String line : info) {
                            sender.sendMessage(line);
                        }
                    }
                }.runTask(minefetch.this);
            }
        }.runTaskAsynchronously(this);
    }

    private List<String> serverInfo = new ArrayList<>();

    private List<String> getServerInfo() {
        serverInfo.clear();

        // Название сервера
        if (getConfig().getBoolean("modules.server-name", true)) {
            String serverName = getConfig().getString("server-name", "Minecraft Server");
            serverInfo.add(getMessage("server-name", "&#199341&l{0}", serverName));
            serverInfo.add(getMessage("separator", "&7{0}", "─".repeat(ChatColor.stripColor(serverName).length())));
        }

        // Версия Minecraft
        if (getConfig().getBoolean("modules.minecraft-version", true)) {
            String version = Bukkit.getVersion().split(" ")[2];
            serverInfo.add(getMessage("minecraft-version", "&#199341Minecraft&f: {0}", version));
        }

        // Версия сервера
        if (getConfig().getBoolean("modules.server-version", true)) {
            String serverVersion = Bukkit.getName() + " " + Bukkit.getBukkitVersion();
            serverInfo.add(getMessage("server-version", "&#199341Сервер&f: {0}", serverVersion));
        }

        // Игроки онлайн
        if (getConfig().getBoolean("modules.players", true)) {
            String players = Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers();
            serverInfo.add(getMessage("players", "&#199341Игроки&f: {0}", players));
        }

        // Плагины
        if (getConfig().getBoolean("modules.plugins", true)) {
            int pluginCount = Bukkit.getPluginManager().getPlugins().length;
            serverInfo.add(getMessage("plugins", "&#199341Плагины&f: {0}", pluginCount));
        }

        // Память
        if (getConfig().getBoolean("modules.memory", true)) {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory() / 1024 / 1024;
            long totalMemory = runtime.totalMemory() / 1024 / 1024;
            long freeMemory = runtime.freeMemory() / 1024 / 1024;
            long usedMemory = totalMemory - freeMemory;
            double memoryUsage = ((double) usedMemory / maxMemory) * 100;

            serverInfo.add(getMessage("memory", "&#199341Память&f: {0}MB / {1}MB &7({2}%)",
                    usedMemory, maxMemory, String.format("%.1f", memoryUsage)));
        }

        // CPU
        if (getConfig().getBoolean("modules.cpu", true)) {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            double cpuUsage = 0;

            // Пытаемся получить CPU usage через com.sun.management.OperatingSystemMXBean
            try {
                if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                    com.sun.management.OperatingSystemMXBean sunOsBean =
                            (com.sun.management.OperatingSystemMXBean) osBean;
                    cpuUsage = sunOsBean.getProcessCpuLoad() * 100;
                }
            } catch (Exception e) {
                // Если не удается получить CPU usage, используем приблизительное значение
                cpuUsage = 0;
            }

            if (cpuUsage < 0) {
                cpuUsage = 0; // Если информация недоступна
            }

            serverInfo.add(getMessage("cpu", "&#199341CPU&f: {0} &7({1}%)",
                    osBean.getArch(), String.format("%.1f", cpuUsage)));
        }

        // TPS
        if (getConfig().getBoolean("modules.tps", true)) {
            double tps = getTPS();
            String tpsColor = tps > 19.0 ? "&f" : tps > 15.0 ? "&e" : "&c";

            serverInfo.add(getMessage("tps", "&#199341TPS&f: {0}{1}",
                    translateColors(tpsColor), String.format("%.2f", tps)));
        }

        // Аптайм
        if (getConfig().getBoolean("modules.uptime", true)) {
            // Используем время запуска плагина как аптайм
            long uptime = System.currentTimeMillis() - serverStartTime;
            String uptimeStr = formatUptime(uptime);
            serverInfo.add(getMessage("uptime", "&#199341Аптайм&f: {0}", uptimeStr));
        }

        // Миры
        if (getConfig().getBoolean("modules.worlds", true)) {
            int worldCount = Bukkit.getWorlds().size();
            serverInfo.add(getMessage("worlds", "&#199341Миры&f: {0}", worldCount));
        }

        // Java версия
        if (getConfig().getBoolean("modules.java-version", true)) {
            String javaVersion = System.getProperty("java.version");
            serverInfo.add(getMessage("java-version", "&#199341Java&f: {0}", javaVersion));
        }

        return serverInfo;
    }

    private double getTPS() {
        try {
            // Используем Reflection для получения TPS из Paper
            Object server = Bukkit.getServer();
            Object minecraftServer = server.getClass().getMethod("getServer").invoke(server);
            double[] tps = (double[]) minecraftServer.getClass().getField("recentTps").get(minecraftServer);
            return tps[0];
        } catch (Exception e) {
            // Если не удается получить TPS, возвращаем приблизительное значение
            return 20.0;
        }
    }

    private String formatUptime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "д " + (hours % 24) + "ч " + (minutes % 60) + "м";
        } else if (hours > 0) {
            return hours + "ч " + (minutes % 60) + "м";
        } else if (minutes > 0) {
            return minutes + "м " + (seconds % 60) + "с";
        } else {
            return seconds + "с";
        }
    }
}