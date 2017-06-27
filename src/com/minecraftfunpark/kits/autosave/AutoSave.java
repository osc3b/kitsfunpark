package com.minecraftfunpark.kits.autosave;

import org.bukkit.scheduler.BukkitRunnable;

import com.minecraftfunpark.kits.Kits;

public class AutoSave extends BukkitRunnable {

    private final Kits plugin;

    public AutoSave(Kits instance) {
        plugin = instance;
    }

    @Override
    public void run() {
        plugin.getLogger().info("AutoSaving Kits configurations");

        plugin.reload();

        plugin.getLogger().info("AutoSave complete");
    }

}
