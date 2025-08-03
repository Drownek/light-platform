package me.drownek.platform.core.dependency;

import lombok.RequiredArgsConstructor;
import me.drownek.platform.core.LightPlatform;

import java.util.List;

@RequiredArgsConstructor
public class DependencyManager {
    private final LightPlatform platform;

    public List<String> getMissingDependencies() {
        return platform.getDependencies().stream()
            .filter(pluginName -> !platform.isPluginEnabled(pluginName))
            .toList();
    }
}
