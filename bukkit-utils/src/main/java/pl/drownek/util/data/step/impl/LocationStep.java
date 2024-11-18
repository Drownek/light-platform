package pl.drownek.util.data.step.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.Plugin;
import pl.drownek.util.TextUtil;
import pl.drownek.util.data.DataGatherer;
import pl.drownek.util.data.step.Step;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LocationStep extends Step<Location> {

    public LocationStep(String info, Consumer<Location> consumer) {
        super(info, consumer);
    }

    @Override
    public Listener handle(Plugin plugin, DataGatherer dataGatherer, Player targetPlayer, BiConsumer<Listener, Location> callback) {
        return new Listener() {
            @EventHandler
            public void handle(PlayerSwapHandItemsEvent event) {
                if (!event.getPlayer().equals(targetPlayer)) {
                    return;
                }
                event.setCancelled(true);

                callback.accept(this, targetPlayer.getLocation());
            }
        };
    }

    @Override
    public String toString(Location value) {
        return TextUtil.prettyFormatLocation(value);
    }
}
