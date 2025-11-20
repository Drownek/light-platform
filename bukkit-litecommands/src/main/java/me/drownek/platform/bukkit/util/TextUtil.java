package me.drownek.platform.bukkit.util;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class TextUtil {

    private static final JavaPlugin PLUGIN = JavaPlugin.getProvidingPlugin(TextUtil.class);
    public static BukkitAudiences adventure = BukkitAudiences.create(PLUGIN);
    public static MiniMessage miniMessage = MiniMessage.builder()
            .postProcessor(new LegacyPostProcessor())
            .preProcessor(new LegacyPreProcessor())
            .build();

    static {
        PLUGIN.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            void handle(PluginDisableEvent event) {
                if (event.getPlugin().equals(PLUGIN)) {
                    if (adventure != null) {
                        adventure.close();
                        adventure = null;
                    }
                }
            }
        }, PLUGIN);
    }

    private TextUtil() {
    }

    public static String color(final String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static Component component(final String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        return resetItalic(miniMessage.deserialize(text));
    }

    public static Component component(final List<String> text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        return Component.join(JoinConfiguration.newlines(), text.stream().map(TextUtil::component).toList());
    }

    public static Component component(final String... text) {
        return component(List.of(text));
    }

    public static List<String> color(final List<String> text) {
        return text.stream().map(TextUtil::color).toList();
    }

    public static void message(final CommandSender commandSender, final String text) {
        adventure.sender(commandSender).sendMessage(component(text));
    }

    public static void message(final CommandSender commandSender, final List<String> text) {
        text.forEach(value -> message(commandSender, value));
    }

    public static Component resetItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }
}
