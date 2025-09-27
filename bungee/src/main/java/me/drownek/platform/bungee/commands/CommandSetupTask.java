package me.drownek.platform.bungee.commands;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bungee.LiteBungeeFactory;
import dev.rollczi.litecommands.bungee.LiteBungeeSettings;
import dev.rollczi.litecommands.message.LiteMessages;
import dev.rollczi.litecommands.schematic.Schematic;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import lombok.RequiredArgsConstructor;
import me.drownek.platform.bungee.util.ChatUtil;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.plan.ExecutionTask;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

@RequiredArgsConstructor
public class CommandSetupTask implements ExecutionTask<LightPlatform> {

    private final Plugin plugin;

    @Override
    public void execute(LightPlatform platform) {
        LiteCommandsBuilder<CommandSender, LiteBungeeSettings, ?> builder = LiteBungeeFactory.builder(plugin);

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
                sender.sendMessage(ChatUtil.color("&cInvalid command usage! &7(" + schematic.first() + ")"));
                return;
            }

            sender.sendMessage(ChatUtil.color("&cInvalid command usage!"));
            for (String scheme : schematic.all()) {
                sender.sendMessage(ChatUtil.color("&8 » &7" + scheme));
            }
        });

        // No permission
        builder.message(LiteMessages.MISSING_PERMISSIONS, (invocation, missingPermissions) -> {
            return "&cYou don't have permission to do this! (" + missingPermissions.asJoinedText() + ")";
        });

        platform.getInjector().registerExclusive("commandsBuilder", builder);
    }
}
