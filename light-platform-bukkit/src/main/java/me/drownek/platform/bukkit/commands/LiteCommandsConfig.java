package me.drownek.platform.bukkit.commands;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import dev.rollczi.litecommands.handler.result.ResultHandleService;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.message.LiteMessages;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.Schematic;
import dev.rollczi.litecommands.time.DurationParser;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import me.drownek.util.TextUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LiteCommandsConfig extends OkaeriConfig {

    @Comment("Invalid usage (only one command available)")
    public List<String> invalidUsageOnlyFirst = Collections.singletonList("&cInvalid command usage! &7({COMMAND})");

    @Comment("Invalid usage (many commands available)")
    public List<String> header = List.of("&cInvalid command usage!", "{COMMANDS}");
    public String commandListFormat = "&8 » &7{COMMAND}";

    @Comment("All commands messages")
    public Messages messages = new Messages();

    public static class Messages extends OkaeriConfig {
        @Comment("Configuration for missing permissions message")
        public String missingPermissions = "You don't have permission to execute this command! (%s) (MISSING_PERMISSIONS)";

        @Comment("Configuration for invalid number message")
        public String invalidNumber = "'%s' is not a number! (INVALID_NUMBER)";

        @Comment("Configuration for instant invalid format message")
        public String instantInvalidFormat = "Invalid date format '%s'! Use: <yyyy-MM-dd> <HH:mm:ss> (INSTANT_INVALID_FORMAT)";

        @Comment("Configuration for command cooldown message")
        public String commandCooldown = "You are on cooldown! Remaining time: %s (COMMAND_COOLDOWN)";

        @Comment("Configuration for UUID invalid format message")
        public String uuidInvalidFormat = "Invalid UUID format '%s'! Use: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxx (dashes are optional) (UUID_INVALID_FORMAT)";

        @Comment("Configuration for world not exist message")
        public String worldNotExist = "&cWorld %s doesn't exist! (WORLD_NOT_EXIST)";

        @Comment("Configuration for world player only message")
        public String worldPlayerOnly = "&cOnly player can execute this command! (WORLD_PLAYER_ONLY)";

        @Comment("Configuration for location invalid format message")
        public String locationInvalidFormat = "&cInvalid location format '%s'! Use: <x> <y> <z> (LOCATION_INVALID_FORMAT)";

        @Comment("Configuration for location player only message")
        public String locationPlayerOnly = "&cOnly player can execute this command! (LOCATION_PLAYER_ONLY)";

        @Comment("Configuration for player not found message")
        public String playerNotFound = "&cPlayer %s not found! (PLAYER_NOT_FOUND)";

        @Comment("Configuration for player only message")
        public String playerOnly = "&cOnly player can execute this command! (PLAYER_ONLY)";

        public void applyMessages(LiteCommands<CommandSender> commands) {
            MessageRegistry<CommandSender> messageRegistry = commands.getInternal().getMessageRegistry();

            // LiteMessages
            messageRegistry.register(LiteMessages.MISSING_PERMISSIONS, (sender, missingPermissions) ->
                String.format(this.missingPermissions, missingPermissions.asJoinedText()));

            messageRegistry.register(LiteMessages.INVALID_NUMBER, (sender, input) ->
                String.format(this.invalidNumber, input));

            messageRegistry.register(LiteMessages.INSTANT_INVALID_FORMAT, (sender, input) ->
                String.format(this.instantInvalidFormat, input));

            messageRegistry.register(LiteMessages.COMMAND_COOLDOWN, (sender, state) ->
                String.format(this.commandCooldown, DurationParser.DATE_TIME_UNITS.format(state.getRemainingDuration())));

            messageRegistry.register(LiteMessages.UUID_INVALID_FORMAT, (sender, input) ->
                String.format(this.uuidInvalidFormat, input));

            // LiteBukkitMessages
            messageRegistry.register(LiteBukkitMessages.WORLD_NOT_EXIST, (sender, input) ->
                String.format(this.worldNotExist, input));

            messageRegistry.register(LiteBukkitMessages.WORLD_PLAYER_ONLY, (sender, unused) ->
                this.worldPlayerOnly);

            messageRegistry.register(LiteBukkitMessages.LOCATION_INVALID_FORMAT, (sender, input) ->
                String.format(this.locationInvalidFormat, input));

            messageRegistry.register(LiteBukkitMessages.LOCATION_PLAYER_ONLY, (sender, unused) ->
                this.locationPlayerOnly);

            messageRegistry.register(LiteBukkitMessages.PLAYER_NOT_FOUND, (sender, input) ->
                String.format(this.playerNotFound, input));

            messageRegistry.register(LiteBukkitMessages.PLAYER_ONLY, (sender, unused) ->
                this.playerOnly);
        }
    }

    public void apply(LiteCommands<CommandSender> commands) {
        this.messages.applyMessages(commands);

        ResultHandleService<CommandSender> resultService = commands.getInternal().getResultService();
        resultService.registerHandler(InvalidUsage.class, (invocation, result, chain) -> {
            CommandSender sender = invocation.sender();
            Schematic schematic = result.getSchematic();

            if (schematic.isOnlyFirst()) {
                List<String> messages = new ArrayList<>();
                for (String line : this.invalidUsageOnlyFirst) {
                    String processedLine = line.replace("{COMMAND}", schematic.first());
                    messages.add(processedLine);
                }
                TextUtil.message(sender, messages);
                return;
            }

            List<String> messages = new ArrayList<>();
            for (String line : this.header) {
                if (line.contains("{COMMANDS}")) {
                    for (String command : schematic.all()) {
                        String formattedCommand = this.commandListFormat.replace("{COMMAND}", command);
                        messages.add(formattedCommand);
                    }
                } else {
                    messages.add(line);
                }
            }

            TextUtil.message(sender, messages);
        });

    }
}
