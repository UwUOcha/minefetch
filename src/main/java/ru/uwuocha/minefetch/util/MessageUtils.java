package ru.uwuocha.minefetch.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Утилитарный класс для работы с сообщениями и цветами, используя API Kyori Adventure.
 */
public final class MessageUtils {

    // Serializer для преобразования старых цветовых кодов (&c, &l, и т.д.) и HEX (&#RRGGBB)
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&') // Указываем, что '&' является символом для цветовых кодов
            .hexColors()    // Включаем поддержку HEX-цветов (формат &#RRGGBB)
            .build();

    // MiniMessage для более сложных форматов (если понадобится в будущем)
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private MessageUtils() {
        // Приватный конструктор для утилитарного класса
    }

    /**
     * Преобразует строку с legacy-кодами (&) и HEX-кодами (&#RRGGBB) в Component.
     * @param text Текст для преобразования.
     * @return Готовый Component для отправки игроку.
     */
    public static Component colorize(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        // Теперь сериализатор правильно обработает и '&' и '&#RRGGBB'
        return LEGACY_SERIALIZER.deserialize(text);
    }

    /**
     * Преобразует строку в формате MiniMessage в Component.
     * @param text Текст в формате MiniMessage.
     * @return Готовый Component.
     */
    public static Component fromMiniMessage(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(text);
    }
}