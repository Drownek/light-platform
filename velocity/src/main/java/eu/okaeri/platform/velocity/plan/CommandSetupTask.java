package eu.okaeri.platform.velocity.plan;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.message.LiteMessages;
import dev.rollczi.litecommands.schematic.Schematic;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import dev.rollczi.litecommands.velocity.LiteVelocityFactory;
import dev.rollczi.litecommands.velocity.LiteVelocitySettings;
import lombok.RequiredArgsConstructor;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.plan.ExecutionTask;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.UUID;

@RequiredArgsConstructor
public class CommandSetupTask implements ExecutionTask<LightPlatform> {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private final ProxyServer proxy;

    @Override
    public void execute(LightPlatform platform) {
        LiteCommandsBuilder<CommandSource, LiteVelocitySettings, ?> builder = LiteVelocityFactory.builder(this.proxy);

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
            CommandSource sender = invocation.sender();
            Schematic schematic = result.getSchematic();

            if (schematic.isOnlyFirst()) {
                sender.sendMessage(MINI_MESSAGE.deserialize("<red>Invalid command usage! <gray>(" + schematic.first() + ")"));
                return;
            }

            sender.sendMessage(MINI_MESSAGE.deserialize("<red>Invalid command usage!"));
            for (String scheme : schematic.all()) {
                sender.sendMessage(MINI_MESSAGE.deserialize("<dark_gray> » <gray>" + scheme));
            }
        });

        // No permission
        builder.message(LiteMessages.MISSING_PERMISSIONS, (invocation, missingPermissions) -> {
            return "&cYou don't have permission to do this! (" + missingPermissions.asJoinedText() + ")";
        });

        platform.getInjector().registerExclusive("commandsBuilder", builder);
    }
}
