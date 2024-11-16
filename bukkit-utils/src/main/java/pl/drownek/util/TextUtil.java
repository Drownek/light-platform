package pl.drownek.util;

import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.IntStream;

public final class TextUtil {

    public static BukkitAudiences adventure;
    public static MiniMessage miniMessage;

    private TextUtil() {
    }

    public static String prettyFormatLocation(double x, double y, double z) {
        return String.format("x: %.2f, y: %.2f, z: %.2f", x, y, z);
    }

    public static String prettyFormatLocation(Location location) {
        if (location == null) {
            return "Brak.";
        }
        return prettyFormatLocation(location.getX(), location.getY(), location.getZ());
    }

    public static String prettyFormatItemStack(ItemStack item) {
        if (item == null) {
            return "Brak.";
        }

        ItemMeta meta = item.getItemMeta();
        String itemName;

        if (meta != null && meta.hasDisplayName()) {
            itemName = meta.getDisplayName();
        } else {
            itemName = item.getType().name();
        }

        return "%dx %s".formatted(item.getAmount(), itemName);
    }

    public static void init(Plugin plugin) {
        if (adventure == null) {
            adventure = BukkitAudiences.create(plugin);
        }
        if (miniMessage == null) {
            miniMessage = MiniMessage.miniMessage();
        }
    }

    public static void shutdown() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    public static String color(final String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static Component componentMiniMessage(@NonNull final List<String> text) {
        Component component = Component.empty();
        for (String t : text) {
            Component message = MiniMessage.miniMessage().deserialize(t);
            component = component.appendNewline().append(message);
        }
        return component;
    }

    public static Component component(final String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    public static List<Component> component(final List<String> text) {
        return text.stream().map(TextUtil::component).toList();
    }

    public static Component joinComponent(final List<String> text) {
        return Component.join(JoinConfiguration.newlines(), text.stream().map(TextUtil::component).toList());
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

    public static void correctUsage(final CommandSender commandSender, final String usage) {
        message(commandSender, "&8(&3&l!&8) &7Poprawne uzycie&8: &b{USAGE}".replace("{USAGE}", usage));
    }

    public static void insufficientPermission(final CommandSender commandSender, final String permission) {
        message(commandSender, "&cNie posiadasz uprawnien do wykonania tej czynnosci! &4({PERMISSION})".replace("{PERMISSION}", permission));
    }

    public static void sendEmptyMessage(final Player player, final int i) {
        IntStream.range(0, i).forEach(it -> message(player, ""));
    }

    public static void announce(final String text) {
        Bukkit.getOnlinePlayers().forEach(value -> message(value, text));
    }


    public static String progressBar(final long current, final long max, final int bars, final char symbol, final ChatColor completedColor, final ChatColor notCompletedColor) {
        final float percent = current / (float) max;
        final int progressBars = (int) (bars * percent);
        final int leftOver = bars - progressBars;
        final StringBuilder builder = new StringBuilder();
        if (current > max) {
            builder.append(completedColor);
            for (int i = 0; i < bars; ++i) {
                builder.append(symbol);
            }
            return builder.toString();
        }
        builder.append(completedColor.toString());
        for (int i = 0; i < progressBars; ++i) {
            builder.append(symbol);
        }
        builder.append(notCompletedColor.toString());
        for (int i = 0; i < leftOver; ++i) {
            builder.append(symbol);
        }
        return builder.toString();
    }

    public static String progressBar(final long current, final long max, final int bars, final char symbol, final String completedColor, final String notCompletedColor) {
        final float percent = current / (float) max;
        final int progressBars = (int) (bars * percent);
        final int leftOver = bars - progressBars;
        final StringBuilder builder = new StringBuilder();
        if (current > max) {
            builder.append(completedColor);
            for (int i = 0; i < bars; ++i) {
                builder.append(symbol);
            }
            return builder.toString();
        }
        builder.append(completedColor.toString());
        for (int i = 0; i < progressBars; ++i) {
            builder.append(symbol);
        }
        builder.append(notCompletedColor.toString());
        for (int i = 0; i < leftOver; ++i) {
            builder.append(symbol);
        }
        return builder.toString();
    }
}
