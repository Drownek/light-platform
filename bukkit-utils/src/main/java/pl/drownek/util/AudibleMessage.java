package pl.drownek.util;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("FieldMayBeFinal")
@Getter
public class AudibleMessage extends SendableMessage {

    private SoundDispatcher sound;

    public AudibleMessage(String message) {
        super(BukkitMessageTarget.CHAT, message);
        this.sound = SoundDispatcher.defaultSound();
    }

    public AudibleMessage(BukkitMessageTarget target, String message) {
        super(target, message);
        this.sound = SoundDispatcher.defaultSound();
    }

    @Override
    public void sendTo(@NonNull CommandSender receiver) {
        if (receiver instanceof Player player) {
            sound.play(player);
        }
        super.sendTo(receiver);
    }
}
