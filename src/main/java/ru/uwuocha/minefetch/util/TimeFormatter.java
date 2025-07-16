package ru.uwuocha.minefetch.util;

import ru.uwuocha.minefetch.config.Lang;

/**
 * Утилитарный класс для форматирования времени с поддержкой локализации.
 */
public class TimeFormatter {

    private final Lang lang;

    public TimeFormatter(Lang lang) {
        this.lang = lang;
    }

    /**
     * Форматирует время в миллисекундах в читаемую строку с учетом локализации.
     * @param millis Время в миллисекундах
     * @return Отформатированная строка времени
     */
    public String formatUptime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return formatTimeWithUnits(days, hours % 24, minutes % 60, "days", "hours", "minutes");
        } else if (hours > 0) {
            return formatTimeWithUnits(hours, minutes % 60, 0, "hours", "minutes", null);
        } else {
            return formatTimeWithUnits(minutes, 0, 0, "minutes", null, null);
        }
    }

    /**
     * Форматирует время с учетом единиц измерения из языкового файла.
     */
    private String formatTimeWithUnits(long value1, long value2, long value3, String unit1, String unit2, String unit3) {
        StringBuilder result = new StringBuilder();

        if (value1 > 0) {
            result.append(value1).append(getTimeUnit(unit1));
        }

        if (value2 > 0) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(value2).append(getTimeUnit(unit2));
        }

        if (value3 > 0 && unit3 != null) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(value3).append(getTimeUnit(unit3));
        }

        return result.toString();
    }

    /**
     * Получает сокращенную форму единицы времени из языкового файла.
     */
    private String getTimeUnit(String unit) {
        return lang.getConfig().getString("time-units." + unit, unit.substring(0, 1));
    }
}
