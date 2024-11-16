package pl.drownek.util.data.step.impl;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import pl.drownek.util.TextUtil;
import pl.drownek.util.data.DataGatherer;
import pl.drownek.util.data.step.Step;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClickedBlockLocationStep extends Step<Location> {

    public ClickedBlockLocationStep(String info, Consumer<Location> consumer) {
        super(info, consumer);
    }

    @Override
    public Listener handle(Plugin plugin, DataGatherer dataGatherer, Player targetPlayer, BiConsumer<Listener, Location> callback) {
        return new Listener() {
            @EventHandler
            public void handle(PlayerInteractEvent event) {
                if (!event.getPlayer().equals(targetPlayer) || event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND) || event.getClickedBlock() == null) {
                    return;
                }

                Location location = event.getClickedBlock().getLocation();
                callback.accept(this, location);
            }
        };
    }

    @Override
    public String toString(Location value) {
        return TextUtil.prettyFormatLocation(value);
    }
}
