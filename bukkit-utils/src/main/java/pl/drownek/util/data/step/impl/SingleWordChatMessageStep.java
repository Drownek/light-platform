package pl.drownek.util.data.step.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import pl.drownek.util.data.DataGatherer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SingleWordChatMessageStep extends ChatMessageStep {

    private Predicate<String> invalidMessagePredicate;

    public SingleWordChatMessageStep(String info, Consumer<String> consumer) {
        super(info, consumer, s -> !s.contains(" "));
    }

    @Override
    public Listener handle(Plugin plugin, DataGatherer dataGatherer, Player targetPlayer, BiConsumer<Listener, String> callback) {
        return super.handle(plugin, dataGatherer, targetPlayer, callback);
    }
}
