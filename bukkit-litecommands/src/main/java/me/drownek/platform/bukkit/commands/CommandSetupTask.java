package me.drownek.platform.bukkit.commands;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.adventure.bukkit.platform.LiteAdventurePlatformExtension;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import dev.rollczi.litecommands.message.LiteMessages;
import dev.rollczi.litecommands.schematic.Schematic;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import lombok.RequiredArgsConstructor;
import me.drownek.platform.bukkit.util.TextUtil;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.plan.ExecutionTask;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

@RequiredArgsConstructor
public class CommandSetupTask implements ExecutionTask<LightPlatform> {

    private final Plugin plugin;

    @Override
    public void execute(LightPlatform platform) {
        LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder = LiteBukkitFactory.builder();

        builder.extension(new LiteAdventurePlatformExtension<>(TextUtil.adventure), configuration -> configuration
            .miniMessage(true) // (<red>, <gradient:red:blue>, <#ff0000>, etc.)
            .legacyColor(true) // (&c, &a, etc.)
            .colorizeArgument(true) // colorize (@Arg Component)
            .serializer(TextUtil.miniMessage) // custom serializer
        );

        // Example values for some argument suggesters
        builder.argumentSuggester(
            Integer.class,
            (invocation, argument, suggestionContext) ->
                SuggestionResult.of("<" + argument.getName() + ">")
        );

        builder.argumentSuggester(
            int.class,
            (invocation, argument, suggestionContext) ->
                SuggestionResult.of("<" + argument.getName() + ">")
        );

        builder.argumentSuggester(
            Float.class,
            (invocation, argument, suggestionContext) ->
                SuggestionResult.of("<" + argument.getName() + ">")
        );

        builder.argumentSuggester(
            float.class,
            (invocation, argument, suggestionContext) ->
                SuggestionResult.of("<" + argument.getName() + ">")
        );

        builder.argumentSuggester(UUID.class, (invocation, argument, suggestionContext) -> {
            return SuggestionResult.of("<" + argument.getName() + ">");
        });

        // Invalid usage
        builder.invalidUsage((invocation, result, chain) -> {
            CommandSender sender = invocation.sender();
            Schematic schematic = result.getSchematic();

            if (schematic.isOnlyFirst()) {
                TextUtil.message(sender, "&cInvalid command usage! &7(" + schematic.first() + ")");
                return;
            }

            TextUtil.message(sender, "&cInvalid command usage!");
            for (String scheme : schematic.all()) {
                TextUtil.message(sender, "&8 » &7" + scheme);
            }
        });

        // No permission
        builder.message(LiteMessages.MISSING_PERMISSIONS, (invocation, missingPermissions) -> {
            return "&cYou don't have permission to do this! (" + missingPermissions.asJoinedText() + ")";
        });

        platform.getInjector().registerExclusive("commandsBuilder", builder);
    }
}
