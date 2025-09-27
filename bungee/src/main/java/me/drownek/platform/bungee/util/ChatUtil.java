package me.drownek.platform.bungee.util;

import net.md_5.bungee.api.ChatColor;

public final class ChatUtil {

    private ChatUtil() {}

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
