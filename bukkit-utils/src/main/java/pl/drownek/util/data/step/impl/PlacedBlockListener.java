package pl.drownek.util.data.step.impl;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import pl.drownek.util.data.DataGatherer;
import pl.drownek.util.data.step.Step;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PlacedBlockListener extends Step<Block> {

    private @Nullable ItemStack itemStack;

    public PlacedBlockListener(String info, Consumer<Block> consumer, @Nullable ItemStack itemStack) {
        super(info, consumer);
        this.itemStack = itemStack;
        this.displaySetValue(false);
    }

    public PlacedBlockListener(String info, Consumer<Block> consumer) {
        super(info, consumer);
    }

    @Override
    public Listener handle(Plugin plugin, DataGatherer dataGatherer, Player targetPlayer, BiConsumer<Listener, Block> callback) {
        return new Listener() {

            {
                if (itemStack != null) {
                    targetPlayer.getInventory().addItem(itemStack);
                }
            }

            @EventHandler
            void handle(BlockPlaceEvent event) {
                if (event.getPlayer().equals(targetPlayer)) {
                    callback.accept(this, event.getBlock());
                }
            }
        };
    }
}
