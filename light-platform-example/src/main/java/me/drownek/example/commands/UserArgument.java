package me.drownek.example.commands;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import eu.okaeri.injector.annotation.Inject;
import me.drownek.example.config.Messages;
import me.drownek.example.data.User;
import me.drownek.example.data.UserRepository;
import me.drownek.platform.bukkit.annotation.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

@CommandArgument
public class UserArgument extends ArgumentResolver<CommandSender, User> {

    private @Inject UserRepository repository;
    private @Inject Messages messages;

    @Override
    protected ParseResult<User> parse(Invocation<CommandSender> invocation, Argument<User> argument, String s) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
        if (!offlinePlayer.hasPlayedBefore()) {
            return ParseResult.failure(messages.playerNotFound);
        }
        return ParseResult.success(repository.getByPlayer(offlinePlayer));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<User> argument, SuggestionContext context) {
        return Arrays.stream(Bukkit.getOfflinePlayers())
            .map(OfflinePlayer::getName)
            .collect(SuggestionResult.collector());
    }
}
