package eu.okaeri.platform.bukkit.i18n;

import eu.okaeri.i18n.message.Message;
import eu.okaeri.i18n.message.SimpleMessage;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.CompiledMessage;
import lombok.NonNull;

public class PrefixMessageMEOCI18n extends PrefixCustomMEOCI18n<Message> {

    @Override
    public Message assembleMessage(Placeholders placeholders, @NonNull CompiledMessage compiled) {
        return SimpleMessage.of(placeholders, compiled);
    }
}
