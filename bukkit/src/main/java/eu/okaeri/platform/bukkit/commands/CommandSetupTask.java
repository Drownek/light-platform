package eu.okaeri.platform.bukkit.commands;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import dev.rollczi.litecommands.message.LiteMessages;
import dev.rollczi.litecommands.schematic.Schematic;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import eu.okaeri.platform.bukkit.util.ChatUtil;
import eu.okaeri.platform.core.OkaeriPlatform;
import eu.okaeri.platform.core.plan.ExecutionTask;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CommandSetupTask implements ExecutionTask<OkaeriPlatform> {

    @Override
    public void execute(OkaeriPlatform platform) {
        LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder = LiteBukkitFactory.builder();

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
                sender.sendMessage(ChatUtil.color("&cNiepoprawne użycie komendy! &7(" + schematic.first() + ")"));
                return;
            }

            sender.sendMessage(ChatUtil.color("&cNiepoprawne użycie komendy!"));
            for (String scheme : schematic.all()) {
                sender.sendMessage(ChatUtil.color("&8 - &7" + scheme));
            }
        });

        platform.getInjector().registerInjectable("commandsBuilder", builder);
    }
}
