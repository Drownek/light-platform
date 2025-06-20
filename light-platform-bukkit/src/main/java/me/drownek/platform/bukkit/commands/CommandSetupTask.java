package me.drownek.platform.bukkit.commands;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import dev.rollczi.litecommands.message.LiteMessages;
import dev.rollczi.litecommands.schematic.Schematic;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import me.drownek.platform.bukkit.util.ChatUtil;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.plan.ExecutionTask;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CommandSetupTask implements ExecutionTask<LightPlatform> {

    @Override
    public void execute(LightPlatform platform) {
        LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder = LiteBukkitFactory.builder();

        // Arguments
        builder.argument(OfflinePlayer.class, new OfflinePlayerArgument());

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
