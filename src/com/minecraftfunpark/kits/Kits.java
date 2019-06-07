package com.minecraftfunpark.kits;


import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecraftfunpark.kits.api.Kit;
import com.minecraftfunpark.kits.api.KitManager;
import com.minecraftfunpark.kits.autosave.AutoSave;
import com.minecraftfunpark.kits.commands.KitCommandExecutor;
import com.minecraftfunpark.kits.commands.KitsCommandExecutor;
import com.minecraftfunpark.kits.configuration.CollectionManager;
import com.minecraftfunpark.kits.configuration.Config;
import com.minecraftfunpark.kits.listeners.EventListener;
import com.minecraftfunpark.kits.util.DelayedPlayer;
import com.minecraftfunpark.kits.util.Time;

public class Kits extends JavaPlugin {

    private static Kits instance;

    private Config config;

    private KitManager kitManager;
    private CollectionManager collectionManager;

    @Override
    public void onDisable() {
        getCollectionManager().save();

        instance = null;
    }

    @Override
    public void onEnable() {
        registerConfig();
        registerManagers();
        registerEvents();
        registerCommands();
        registerConfigurationSerializables();
        registerAutoSave();

        reload();

        instance = this;
    }

    private void registerConfig() {
        config = new Config(this, "config");
        config.copyDefaults();
    }

    private void registerManagers() {
        kitManager = new KitManager(this);
        collectionManager = new CollectionManager(this);
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    private void registerCommands() {
        getCommand("kits").setExecutor(new KitsCommandExecutor(this));
        getCommand("kit").setExecutor(new KitCommandExecutor(this));
    }

    private void registerConfigurationSerializables() {
        ConfigurationSerialization.registerClass(Kit.class);
        ConfigurationSerialization.registerClass(DelayedPlayer.class);
    }


    private void registerAutoSave() {
        if (!config.getBoolean("autosave.enabled")) {
            return;
        }

        Time time = Time.fromExpression(config.getString("autosave.interval"));

        long interval = time.getTotalMilliseconds();

        new AutoSave(this).runTaskTimer(this, (interval * 20) / 1000, (interval * 20) / 1000);

        getLogger().info("AutoSave set to execute every " + time.toReadableFormat(false));
    }

    public void reload() {
        registerConfig();
        getCollectionManager().reload();
    }

    public String getPluginDetails() {
        return getPluginName() + " " + getPluginVersion();
    }

    public String getPluginName() {
        return getDescription().getName();
    }

    public String getPluginVersion() {
        return getDescription().getVersion();
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public static Kits getInstance() {
        return instance;
    }
}
