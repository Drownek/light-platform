package pl.drownek.util.data.step.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;
import pl.drownek.util.TextUtil;
import pl.drownek.util.data.DataGatherer;
import pl.drownek.util.data.step.Step;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RightClickedPlayerStep extends Step<Player> {

    public RightClickedPlayerStep(String info, Consumer<Player> consumer) {
        super(info, consumer);
    }

    @Override
    public Listener handle(Plugin plugin, DataGatherer dataGatherer, Player targetPlayer,
                           BiConsumer<Listener, Player> callback) {

        return new Listener() {
            @EventHandler
            void handle(PlayerInteractEntityEvent event) {
                Player player = event.getPlayer();
                if (!player.equals(targetPlayer) || !(event.getRightClicked() instanceof Player clicked)) {
                    return;
                }
                callback.accept(this, clicked);
            }
        };
    }
}
