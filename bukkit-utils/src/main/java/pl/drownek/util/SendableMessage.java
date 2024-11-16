package pl.drownek.util;

import eu.okaeri.commons.bukkit.UnsafeBukkitCommons;
import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@Getter
public class SendableMessage extends OkaeriConfig {

    private BukkitMessageTarget target;
    private String message;
    private transient Map<String, Object> fields;

    public static SendableMessage of(BukkitMessageTarget target, String message) {
        return new SendableMessage(target, message);
    }

    public static SendableMessage of(String message) {
        return new SendableMessage(BukkitMessageTarget.CHAT, message);
    }

    public SendableMessage(BukkitMessageTarget target, String message) {
        this.message = message;
        this.target = target;
    }

    public SendableMessage with(String key, Object value) {
        if (fields == null) {
            fields = new LinkedHashMap<>();
        }
        this.fields.put(key, value);
        return this;
    }

    public String format() {
        String formatted = this.message;
        if (this.fields != null) {
            for (Map.Entry<String, Object> entry : this.fields.entrySet()) {
                formatted = formatted.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }
        }
        return formatted;
    }

    public void sendTo(@NonNull CommandSender receiver) {
        switch (this.target) {
            case CHAT:
                TextUtil.message(receiver, this.format());
                break;
            case ACTION_BAR:
                if (receiver instanceof Player player) {
                    UnsafeBukkitCommons.sendMessage(player, this.format(), UnsafeBukkitCommons.ChatTarget.ACTION_BAR);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported target: " + this.target);
        }
    }

    public void announce() {
        Bukkit.getOnlinePlayers().forEach(this::sendTo);
    }
}
