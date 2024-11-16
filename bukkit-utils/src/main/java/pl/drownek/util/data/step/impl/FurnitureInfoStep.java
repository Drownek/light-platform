package pl.drownek.util.data.step.impl;

import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import pl.drownek.util.FurnitureInfo;
import pl.drownek.util.data.DataGatherer;
import pl.drownek.util.data.step.Step;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FurnitureInfoStep extends Step<FurnitureInfo> {

    public FurnitureInfoStep(String info, Consumer<FurnitureInfo> consumer) {
        super(info, consumer);
    }

    @Override
    public Listener handle(Plugin plugin, DataGatherer dataGatherer, Player targetPlayer, BiConsumer<Listener, FurnitureInfo> callback) {
        return new Listener() {
            @EventHandler
            public void handle(FurnitureInteractEvent event) {
                if (!event.getPlayer().equals(targetPlayer) || event.getFurniture() == null) {
                    return;
                }

                callback.accept(this, FurnitureInfo.of(event.getFurniture()));
            }
        };
    }
}
