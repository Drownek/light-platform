package pl.drownek.util.data.step;


import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import pl.drownek.util.data.DataGatherer;
import pl.drownek.util.data.StepResult;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public abstract class Step<T> {

    private final String info;
    private final Consumer<T> consumer;
    private @Nullable Function<T, StepResult> resultHandler;
    private boolean displaySetValue = true;
    private boolean confirmAction = true;

    public Step(String info, Consumer<T> consumer) {
        this.info = info;
        this.consumer = consumer;
        this.resultHandler = null;
    }

    public Step(String info, Consumer<T> consumer, @Nullable Function<T, StepResult> resultHandler) {
        this.info = info;
        this.consumer = consumer;
        this.resultHandler = resultHandler;
    }

    public Step<T> resultHandler(Function<T, StepResult> function) {
        this.resultHandler = function;
        return this;
    }

    public Step<T> displaySetValue(boolean displaySetValue) {
        this.displaySetValue = displaySetValue;
        return this;
    }

    public Step<T> withoutConfirmAction() {
        this.confirmAction = false;
        return this;
    }

    public abstract Listener handle(Plugin plugin, DataGatherer dataGatherer, Player targetPlayer, BiConsumer<Listener, T> callback);

    public String toString(T value) {
        return null;
    }
}
