package me.drownek.platform.bukkit.util;

import org.bukkit.ChatColor;

public final class ChatUtil {

    private ChatUtil() {}

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
