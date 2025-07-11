package me.drownek.example;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;

@Command(name = "example")
public class TestCommand {
    @Execute
    void execute(@Context CommandSource source) {
        source.sendPlainMessage("Hello!");
    }
}
