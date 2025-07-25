package me.drownek.platform.bukkit;

import java.util.List;

public class HookManager {

    private final LightBukkitPlugin plugin;

    public HookManager(LightBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    public List<String> getMissingDependencies() {
        return plugin.getDependencies().stream()
            .filter(pluginName -> plugin.getServer().getPluginManager().getPlugin(pluginName) == null)
            .toList();
    }
}
