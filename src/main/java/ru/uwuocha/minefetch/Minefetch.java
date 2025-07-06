package ru.uwuocha.minefetch;

import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import ru.uwuocha.minefetch.command.MinefetchCommand;
import ru.uwuocha.minefetch.config.PluginConfig;
import ru.uwuocha.minefetch.service.InfoService;

import java.lang.reflect.Field;

/**
 * Главный класс плагина Minefetch.
 * Отвечает за инициализацию и отключение плагина, а также за управление его компонентами.
 */
public final class Minefetch extends JavaPlugin {

    private PluginConfig pluginConfig;
    private InfoService infoService;

    @Override
    public void onEnable() {
        // Инициализация менеджера конфигурации
        this.pluginConfig = new PluginConfig(this);
        pluginConfig.load(); // Загружаем конфигурацию и ascii.txt

        // Инициализация сервиса для сбора информации
        this.infoService = new InfoService(this, pluginConfig);

        // Регистрация команды программно
        registerCommand();

        getLogger().info("Плагин Minefetch успешно включен!");
    }

    /**
     * Регистрирует команду /minefetch программно, используя CommandMap.
     * Это правильный способ для плагинов Paper/Purpur.
     */
    private void registerCommand() {
        try {
            // Получаем доступ к CommandMap сервера через рефлексию
            final Field bukkitCommandMap = getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(getServer());

            // Создаем и регистрируем нашу команду
            commandMap.register(
                    "minefetch", // Имя команды, которое будет использоваться как префикс по умолчанию
                    new MinefetchCommand(this, pluginConfig, infoService)
            );
            getLogger().info("Команда /minefetch успешно зарегистрирована.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().severe("Не удалось зарегистрировать команду /minefetch! Плагин не будет работать корректно.");
            e.printStackTrace();
            // Отключаем плагин, если команда не может быть зарегистрирована
            getServer().getPluginManager().disablePlugin(this);
        }
    }


    @Override
    public void onDisable() {
        getLogger().info("Плагин Minefetch отключен!");
    }

    /**
     * Возвращает экземпляр менеджера конфигурации.
     * @return PluginConfig
     */
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    /**
     * Возвращает экземпляр сервиса сбора информации.
     * @return InfoService
     */
    public InfoService getInfoService() {
        return infoService;
    }
}
