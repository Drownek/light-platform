package eu.okaeri.platform.bungee.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public final class TextUtil {

    private TextUtil() {}

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void message(CommandSender player, Object message) {
        player.sendMessage(TextComponent.fromLegacy(color(message.toString())));
    }

}
