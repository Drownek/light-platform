package pl.drownek.util.data.step.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import pl.drownek.util.TextUtil;
import pl.drownek.util.data.DataGatherer;
import pl.drownek.util.data.step.Step;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ChatMessageStep extends Step<String> {

    private Predicate<String> validMessagePredicate;

    public ChatMessageStep(String info, Consumer<String> consumer, Predicate<String> validMessagePredicate) {
        super(info, consumer);
        this.validMessagePredicate = validMessagePredicate;
    }

    public ChatMessageStep(String info, Consumer<String> consumer) {
        super(info, consumer);
    }

    @Override
    public Listener handle(Plugin plugin, DataGatherer dataGatherer, Player targetPlayer,
                           BiConsumer<Listener, String> callback) {

        return new Listener() {
            @EventHandler
            public void handle(AsyncPlayerChatEvent event) {
                String message = event.getMessage();
                if (!event.getPlayer().equals(targetPlayer) || message.startsWith(",")) {
                    return;
                }

                event.setCancelled(true);

                if (ChatMessageStep.this.validMessagePredicate != null && !ChatMessageStep.this.validMessagePredicate.test(
                    message)) {
                    TextUtil.message(targetPlayer, "&cZła wartość!");
                    return;
                }

                callback.accept(this, message);
            }
        };
    }
}
