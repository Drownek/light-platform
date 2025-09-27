package me.drownek.example.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.injector.OkaeriInjector;
import eu.okaeri.injector.annotation.Inject;
import me.drownek.example.config.Messages;
import me.drownek.example.config.PluginConfig;
import me.drownek.example.config.polymorphic.computer.Laptop;
import me.drownek.example.config.polymorphic.computer.Server;
import me.drownek.example.data.User;
import me.drownek.example.service.ExampleService;
import me.drownek.platform.bungee.util.ChatUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Command(name = "example")
@Permission("example")
public class ExampleCommand {

    private @Inject ExampleService exampleService;
    private @Inject OkaeriInjector injector;
    private @Inject Messages messages;
    private @Inject Plugin plugin;
    private @Inject PluginConfig config;

    @Execute(name = "polymorphic computer")
    void polymorphicComputer(@Context CommandSender commandSender) {
        config.computers.add(new Laptop("Apple", "MacBook Pro", 2499.99, 16, 1.4, 12, 512));
        config.computers.add(new Server("Dell", "PowerEdge R750", 4999.99, 64, 24, true));
        config.save();
        config.load();
        commandSender.sendMessage(
            config.computers.stream()
                .map(computer -> computer.getClass().getSimpleName())
                .collect(Collectors.joining(", "))
        );
    }

    @Async
    @Execute(name = "set-balance")
    void setBalance(@Context CommandSender player, @Arg User target, @Arg BigDecimal balance) {
        target.setBalance(balance);
        target.save();
        player.sendMessage("Balance set " + balance);
    }

    @Async
    @Execute(name = "get-balance")
    void getBalance(@Context CommandSender sender, @Arg User target) {
        sender.sendMessage("Balance: " + target.getBalance());
    }

    @Execute(name = "greeting")
    void execute(@Context CommandSender player) {
        exampleService.greet(player);
    }

    @Execute(name = "reload")
    void reload(@Context CommandSender player) {
        try {
            injector.streamOf(OkaeriConfig.class).forEach(OkaeriConfig::load);
            player.sendMessage(ChatUtil.color(messages.configReloaded));
        } catch (Exception e) {
            player.sendMessage(ChatUtil.color(messages.configReloadFail));
            plugin.getLogger().log(Level.SEVERE, "Failed to reload config", e);
        }
    }
}
