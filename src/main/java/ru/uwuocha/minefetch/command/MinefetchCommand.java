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
 * Handler for the /minefetch command, inheriting from the Command class.
 */
public class MinefetchCommand extends Command {

    private final Minefetch plugin;

    public MinefetchCommand(Minefetch plugin) {
        // Superclass constructor to define command properties
        super("minefetch",
                "Show server information.",
                "/minefetch [reload]",
                List.of("mf", "fetch"));

        this.plugin = plugin;

        // Set access permissions for the command
        setPermission("minefetch.use");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender)) {
            // The no-permission message will be sent automatically if testPermission returns false
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
            // Suggest 'reload' as a tab completion option
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }

    /**
     * Handles the 'reload' subcommand.
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
     * Asynchronously gathers and displays server info.
     */
    private void displayServerInfo(CommandSender sender) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Asynchronously gather data
            boolean isPlayer = sender instanceof org.bukkit.entity.Player;
            List<String> rawAscii = isPlayer ? plugin.getPluginConfig().getAsciiArtLines() : plugin.getPluginConfig().getConsoleAsciiArtLines();
            
            final List<Component> asciiComponents = rawAscii.stream()
                    .map(MessageUtils::colorize)
                    .toList();
            final List<Component> infoComponents = plugin.getInfoService().getServerInfo();

            // Form the final message
            List<Component> finalMessage = new ArrayList<>();
            int maxLines = Math.max(asciiComponents.size(), infoComponents.size());

            String padding = isPlayer ? "              " : "                            "; // 14 spaces or 28 spaces

            for (int i = 0; i < maxLines; i++) {
                TextComponent.Builder lineBuilder = Component.text();

                if (i < asciiComponents.size()) {
                    lineBuilder.append(asciiComponents.get(i));
                } else {
                    // Add padding if ASCII art is shorter than the info block
                    lineBuilder.append(Component.text(padding));
                }

                if (i < infoComponents.size()) {
                    lineBuilder.append(Component.text("  ")).append(infoComponents.get(i));
                }
                finalMessage.add(lineBuilder.build());
            }

            // Synchronously send the message to the player
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (Component line : finalMessage) {
                    sender.sendMessage(line);
                }
            });
        });
    }
}
