package eu.okaeri.platform.bukkit.i18n;

import eu.okaeri.i18n.configs.LocaleConfig;
import eu.okaeri.i18n.configs.extended.MessageMEOCI18n;
import eu.okaeri.i18n.extended.MessageColors;
import eu.okaeri.i18n.message.Message;
import eu.okaeri.i18n.message.SimpleMessage;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.platform.bukkit.i18n.message.BukkitMessageDispatcher;
import eu.okaeri.platform.core.i18n.message.MessageAssembler;
import eu.okaeri.platform.core.placeholder.PlaceholdersFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.nio.file.Files;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class BI18n extends PrefixMessageMEOCI18n {

    private static final Pattern ALT_COLOR_PATTERN = Pattern.compile("&[0-9A-Fa-fK-Ok-oRXrx]");

    private final @Getter I18nColorsConfig colorsConfig;
    private final @Getter PlaceholdersFactory placeholdersFactory;

    private @Getter @Setter MessageAssembler messageAssembler = SimpleMessage::of;

    public BI18n(@NonNull I18nColorsConfig colorsConfig, @NonNull PlaceholdersFactory placeholdersFactory) {
        this.colorsConfig = colorsConfig;
        this.placeholdersFactory = placeholdersFactory;
    }

    @Override
    public Message assembleMessage(Placeholders placeholders, @NonNull CompiledMessage compiled) {
        return this.messageAssembler.assemble(placeholders, compiled);
    }

    @Override
    public BukkitMessageDispatcher get(@NonNull String key) {
        return new BukkitMessageDispatcher(this, key, this.getPlaceholdersFactory());
    }

    @Override
    public Message get(@NonNull Object entity, @NonNull String key) {
        Message message = super.get(entity, key);
        this.getPlaceholdersFactory().provide(entity).forEach(message::with);
        return message;
    }

    @Override
    public void load() {

        if ((this.getColorsConfig().getBindFile() != null) && Files.exists(this.getColorsConfig().getBindFile())) {
            this.getColorsConfig().load();
        }

        for (Map.Entry<Locale, LocaleConfig> entry : this.getConfigs().entrySet()) {
            super.registerConfig(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean hasColors(@NonNull String text) {
        return ALT_COLOR_PATTERN.matcher(text).find();
    }

    @Override
    public String color(@NonNull String source) {
        return ChatColor.translateAlternateColorCodes('&', source);
    }

    @Override
    protected Optional<MessageColors> matchColors(@NonNull String fieldName) {
        return this.getColorsConfig().getMatchers().stream()
            .filter(matcher -> matcher.getPattern().matcher(fieldName).matches())
            .map(matcher -> MessageColors.of(String.valueOf(matcher.getMessageColor()), String.valueOf(matcher.getFieldsColor())))
            .findAny();
    }

    @Override
    protected Optional<String> matchPrefix(@NonNull String fieldName) {
        return this.getColorsConfig().getMatchers().stream()
            // Make it lower case because it wouldn't match
            .filter(matcher -> matcher.getPattern().matcher(fieldName.toLowerCase()).matches())
            .map(I18nColorMatcher::getPrefix)
            .findAny();
    }
}
