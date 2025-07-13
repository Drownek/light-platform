package me.drownek.platform.core.component.type;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.command.RootCommand;
import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentResolver;
import me.drownek.platform.core.component.manifest.BeanManifest;

import java.lang.reflect.Method;

public class CommandComponentResolver implements ComponentResolver {

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return type.getAnnotation(Command.class) != null || type.getAnnotation(RootCommand.class) != null;
    }

    @Override
    public boolean supports(@NonNull Method method) {
        return false;
    }

    @Inject
    private LiteCommandsBuilder commands;

    @Override
    public Object make(@NonNull ComponentCreator creator, @NonNull BeanManifest manifest, @NonNull Injector injector) {
        long start = System.currentTimeMillis();

        Object command = injector.createInstance(manifest.getType());

        commands.commands(command);

        long took = System.currentTimeMillis() - start;
        creator.debug(ComponentHelper.buildComponentMessage()
                .type("Added command")
                .name(command.getClass().getSimpleName())
                .took(took)
                .build());

        creator.increaseStatistics("commands", 1);

        return command;
    }
}
