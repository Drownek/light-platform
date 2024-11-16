package pl.drownek.util.data.step.impl;

import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.Plugin;
import pl.drownek.util.TextUtil;
import pl.drownek.util.data.DataGatherer;
import pl.drownek.util.data.step.Step;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ProtectedPolygonalRegionStep extends Step<ProtectedPolygonalRegion> {

    private final String id;

    public ProtectedPolygonalRegionStep(String info, String id, Consumer<ProtectedPolygonalRegion> consumer) {
        super(info, consumer);
        this.id = id;
        this.withoutConfirmAction();
    }

    @Override
    public Listener handle(Plugin plugin, DataGatherer dataGatherer, Player targetPlayer, BiConsumer<Listener, ProtectedPolygonalRegion> callback) {
        return new Listener() {

            @EventHandler
            public void handle(PlayerSwapHandItemsEvent event) {
                if (!event.getPlayer().equals(targetPlayer)) {
                    return;
                }

                event.setCancelled(true);

                Optional<Polygonal2DRegion> regionOptional = PolygonalCuboidStep.getRegion(event.getPlayer());
                if (regionOptional.isPresent()) {
                    Polygonal2DRegion region = regionOptional.get();
                    callback.accept(this, new ProtectedPolygonalRegion(
                        id.toLowerCase(),
                        region.getPoints(),
                        region.getMinimumY(),
                        region.getMaximumY()
                    ));
                } else {
                    TextUtil.message(targetPlayer, "&cNie zaznaczyłeś terenu!");
                }
            }
        };
    }
}
