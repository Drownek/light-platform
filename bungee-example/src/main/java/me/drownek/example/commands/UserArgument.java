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
import me.drownek.platform.bungee.annotation.CommandArgument;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandArgument
public class UserArgument extends ArgumentResolver<CommandSender, User> {

    private @Inject UserRepository repository;
    private @Inject Messages messages;

    @Override
    protected ParseResult<User> parse(Invocation<CommandSender> invocation, Argument<User> argument, String s) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(s);
        if (player == null) {
            return ParseResult.failure(messages.playerNotFound);
        }
        return ParseResult.success(repository.getByPlayer(player));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<User> argument, SuggestionContext context) {
        return ProxyServer.getInstance().getPlayers().stream()
            .map(ProxiedPlayer::getName)
            .collect(SuggestionResult.collector());
    }
}
