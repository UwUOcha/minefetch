package ru.uwuocha.minefetch.util;

import ru.uwuocha.minefetch.config.Lang;

/**
 * Utility class for time formatting with localization support.
 */
public class TimeFormatter {

    private final Lang lang;

    public TimeFormatter(Lang lang) {
        this.lang = lang;
    }

    /**
     * Formats time in milliseconds into a readable string considering localization.
     * @param millis Time in milliseconds
     * @return Formatted time string
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
        } else if (minutes > 0) {
            return formatTimeWithUnits(minutes, 0, 0, "minutes", null, null);
        } else {
            return seconds + getTimeUnit("seconds");
        }
    }

    /**
     * Formats time considering units from the language file.
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
     * Gets the abbreviated form of a time unit from the language file.
     */
    private String getTimeUnit(String unit) {
        return lang.getConfig().getString("time-units." + unit, unit.substring(0, 1));
    }
}
