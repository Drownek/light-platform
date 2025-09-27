package me.drownek.example.service;

import eu.okaeri.injector.annotation.Inject;
import me.drownek.example.config.Messages;
import me.drownek.platform.core.annotation.Component;
import net.md_5.bungee.api.CommandSender;

@Component
public class ExampleService {

    private @Inject Messages messages;

    public void greet(CommandSender player) {
        player.sendMessage("Hi!");
    }
}
