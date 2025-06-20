package me.drownek.platform.bukkit.commands;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class OfflinePlayerArgument extends ArgumentResolver<CommandSender, OfflinePlayer> {

    @Override
    protected ParseResult<OfflinePlayer> parse(Invocation<CommandSender> invocation, Argument<OfflinePlayer> argument, String s) {
        return Arrays.stream(Bukkit.getOfflinePlayers())
            .filter(player -> Objects.equals(player.getName(), s))
            .findAny()
            .map(ParseResult::success)
            .orElse(ParseResult.failure("&cNie znaleziono gracza o nazwie: " + s));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<OfflinePlayer> argument, SuggestionContext context) {
        return SuggestionResult.of(Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).filter(Objects::nonNull).collect(Collectors.toList()));
    }
}
