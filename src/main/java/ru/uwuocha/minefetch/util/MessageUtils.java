package ru.uwuocha.minefetch.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Utility class for working with messages and colors using the Kyori Adventure API.
 */
public final class MessageUtils {

    // Serializer for converting old color codes (&c, &l, etc.) and HEX (&#RRGGBB)
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&') // Specify that '&' is the character for color codes
            .hexColors()    // Enable support for HEX colors (format &#RRGGBB)
            .build();

    // MiniMessage for more complex formats (if needed in the future)
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private MessageUtils() {
        // Private constructor for utility class
    }

    /**
     * Converts a string with legacy codes (&) and HEX codes (&#RRGGBB) to a Component.
     * @param text Text to convert.
     * @return Ready Component to send to the player.
     */
    public static Component colorize(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        // Now the serializer will correctly handle both '&' and '&#RRGGBB'
        return LEGACY_SERIALIZER.deserialize(text);
    }

    /**
     * Converts a string in MiniMessage format to a Component.
     * @param text Text in MiniMessage format.
     * @return Ready Component.
     */
    public static Component fromMiniMessage(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(text);
    }
}