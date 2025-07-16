package ru.uwuocha.minefetch.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.uwuocha.minefetch.Minefetch;
import ru.uwuocha.minefetch.util.MessageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Обработчик команды /minefetch, наследующийся от класса Command.
 */
public class MinefetchCommand extends Command {

    private final Minefetch plugin;

    public MinefetchCommand(Minefetch plugin) {
        // Конструктор суперкласса для определения свойств команды
        super("minefetch",
                "Показать информацию о сервере.",
                "/minefetch [reload]",
                List.of("mf", "fetch"));

        this.plugin = plugin;

        // Устанавливаем права доступа для команды
        setPermission("minefetch.use");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender)) {
            // Сообщение об отсутствии прав отправится автоматически, если testPermission вернет false
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            handleReload(sender);
            return true;
        }

        displayServerInfo(sender);
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("minefetch.reload")) {
            // Предлагаем 'reload' как вариант автодополнения
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }

    /**
     * Обрабатывает подкоманду 'reload'.
     */
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("minefetch.reload")) {
            sender.sendMessage(plugin.getLang().getMessage("no-permission"));
            return;
        }

        plugin.reload();
        sender.sendMessage(plugin.getLang().getMessage("reload-success"));
    }

    /**
     * Асинхронно собирает и отображает информацию о сервере.
     */
    private void displayServerInfo(CommandSender sender) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Асинхронно собираем данные
            final List<Component> asciiComponents = plugin.getPluginConfig().getAsciiArtLines().stream()
                    .map(MessageUtils::colorize)
                    .toList();
            final List<Component> infoComponents = plugin.getInfoService().getServerInfo();

            // Формируем финальное сообщение
            List<Component> finalMessage = new ArrayList<>();
            int maxLines = Math.max(asciiComponents.size(), infoComponents.size());

            for (int i = 0; i < maxLines; i++) {
                TextComponent.Builder lineBuilder = Component.text();

                if (i < asciiComponents.size()) {
                    lineBuilder.append(asciiComponents.get(i));
                } else {
                    // Добавляем отступ, если ASCII-арт короче, чем инфо-блок
                    lineBuilder.append(Component.text("              "));
                }

                if (i < infoComponents.size()) {
                    lineBuilder.append(Component.text("  ")).append(infoComponents.get(i));
                }
                finalMessage.add(lineBuilder.build());
            }

            // Синхронно отправляем сообщение игроку
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (Component line : finalMessage) {
                    sender.sendMessage(line);
                }
            });
        });
    }
}
