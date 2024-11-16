package pl.drownek.util.data.step.impl;

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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RightClickedBlockStep extends Step<Block> {

    public RightClickedBlockStep(String info, Consumer<Block> consumer) {
        super(info, consumer);
    }

    @Override
    public Listener handle(Plugin plugin, DataGatherer dataGatherer, Player targetPlayer, BiConsumer<Listener, Block> callback) {
        return new Listener() {
            @EventHandler
            public void handle(PlayerInteractEvent event) {
                if (!event.getPlayer().equals(targetPlayer) || event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND) || event.getClickedBlock() == null) {
                    return;
                }

                callback.accept(this, event.getClickedBlock());
            }
        };
    }

    @Override
    public String toString(Block value) {
        return TextUtil.prettyFormatLocation(value.getLocation());
    }
}
