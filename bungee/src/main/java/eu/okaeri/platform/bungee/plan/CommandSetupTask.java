package eu.okaeri.platform.bungee.plan;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bungee.LiteBungeeFactory;
import dev.rollczi.litecommands.bungee.LiteBungeeSettings;
import dev.rollczi.litecommands.message.LiteMessages;
import dev.rollczi.litecommands.schematic.Schematic;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import eu.okaeri.platform.bungee.OkaeriBungeePlugin;
import eu.okaeri.platform.bungee.util.TextUtil;
import eu.okaeri.platform.core.OkaeriPlatform;
import eu.okaeri.platform.core.plan.ExecutionTask;
import net.md_5.bungee.api.CommandSender;

import java.util.UUID;

public class CommandSetupTask implements ExecutionTask<OkaeriPlatform> {

    @Override
    public void execute(OkaeriPlatform platform) {
        var plugin = platform.getInjector().getOrThrow("plugin", OkaeriBungeePlugin.class);
        LiteCommandsBuilder<CommandSender, LiteBungeeSettings, ?> builder = LiteBungeeFactory.builder(plugin);

        // Example values for some argument suggesters
        builder.argumentSuggester(
            Integer.class,
            (invocation, argument, suggestionContext) ->
                SuggestionResult.of("<" + argument.getKeyName() + ">")
        );

        builder.argumentSuggester(
            int.class,
            (invocation, argument, suggestionContext) ->
                SuggestionResult.of("<" + argument.getKeyName() + ">")
        );

        builder.argumentSuggester(
            Float.class,
            (invocation, argument, suggestionContext) ->
                SuggestionResult.of("<" + argument.getKeyName() + ">")
        );

        builder.argumentSuggester(
            float.class,
            (invocation, argument, suggestionContext) ->
                SuggestionResult.of("<" + argument.getKeyName() + ">")
        );

        builder.argumentSuggester(UUID.class, (invocation, argument, suggestionContext) -> {
            return SuggestionResult.of("<" + argument.getName() + ">");
        });

        // Messages
        builder.message(LiteMessages.UUID_INVALID_FORMAT, (input) -> {
            return "&cZły format UUID &4'" + input + "'&c! Przykład: &4xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxx";
        });

        // Invalid usage
        builder.invalidUsage((invocation, result, chain) -> {
            CommandSender sender = invocation.sender();
            Schematic schematic = result.getSchematic();

            if (schematic.isOnlyFirst()) {
                sender.sendMessage(TextUtil.color("&cNiepoprawne użycie komendy! &7(" + schematic.first() + ")"));
                return;
            }

            sender.sendMessage(TextUtil.color("&cNiepoprawne użycie komendy!"));
            for (String scheme : schematic.all()) {
                sender.sendMessage(TextUtil.color("&8 - &7" + scheme));
            }
        });

        // No permission
        builder.message(LiteMessages.MISSING_PERMISSIONS, (invocation, missingPermissions) -> {
            return "&cNie masz uprawnień do wykonania tej czynności (" + missingPermissions.asJoinedText() + ")";
        });

        platform.getInjector().registerInjectable("commandsBuilder", builder);
    }
}
