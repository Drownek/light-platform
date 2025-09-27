package me.drownek.example.config;

import eu.okaeri.configs.OkaeriConfig;
import me.drownek.platform.core.annotation.Configuration;

@SuppressWarnings("CanBeFinal")
@Configuration(path = "messages.{ext}")
public class Messages extends OkaeriConfig {
    public String playerNotFound = "Player not found.";
    public String configReloaded = "Config reloaded";
    public String configReloadFail = "Config failed to load!";
}
