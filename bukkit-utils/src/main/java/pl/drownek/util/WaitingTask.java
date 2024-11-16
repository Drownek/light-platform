package pl.drownek.util;

import lombok.Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WaitingTask extends BukkitRunnable implements Listener {

    private final Plugin plugin;
    private final String actionName;
    private final long duration;
    private final Runnable successAction;
    private final Runnable failAction;
    private final List<Listener> customListeners;

    private Listener listener;
    private long finishState;
    private Player player;

    @Builder
    public WaitingTask(Plugin plugin, String actionName, Duration duration, Runnable successAction, Runnable failAction, List<Listener> customListeners) {
        this.actionName = actionName;
        this.duration = duration.toSeconds();
        this.successAction = successAction;
        this.failAction = failAction;
        this.plugin = plugin;
        if (customListeners == null) {
            customListeners = new ArrayList<>();
        }
        this.customListeners = customListeners;
    }

    public void start(Player player) {
        this.player = player;
        this.finishState = 0;

        this.listener = new Listener() {
            @EventHandler
            public void handle(PlayerToggleSneakEvent event) {
                if (!event.getPlayer().equals(player)) {
                    return;
                }
                WaitingTask.this.cancel(false);
            }

            @EventHandler
            public void handle(PlayerMoveEvent event) {
                if (!event.getPlayer().equals(player)) {
                    return;
                }
                if (event.getTo() == null || LocationUtil.isSimilarExceptRotation(event.getFrom(), event.getTo())) {
                    return;
                }
                event.setCancelled(true);
            }
        };
        this.plugin.getServer().getPluginManager().registerEvents(this.listener, this.plugin);

        this.customListeners.forEach(it -> plugin.getServer().getPluginManager().registerEvents(it, this.plugin));

        this.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void run() {
        if (this.finishState >= 100) {
            this.cancel(true);
            if (this.successAction != null) {
                this.successAction.run();
            }
            return;
        }

        Component subtitle = Component.text(TextUtil.progressBar(this.finishState, 100, 50, '|', ChatColor.GREEN, ChatColor.GRAY));
        Title title = Title.title(Component.text(this.actionName), subtitle);
        TextUtil.adventure.sender(this.player).showTitle(title);

        TextComponent actionBar = Component.text("Naciśnij ").append(Component.keybind("key.sneak")).append(Component.text(" aby przerwać"));
        TextUtil.adventure.sender(this.player).sendActionBar(actionBar);

        this.finishState += 100 / this.duration;
    }

    public void cancel(boolean success) {
        if (!success && this.failAction != null) {
            this.failAction.run();
        }

        TextUtil.adventure.sender(this.player).showTitle(Title.title(Component.text(""), Component.text("")));
        TextUtil.adventure.sender(this.player).sendActionBar(Component.text(""));

        Bukkit.getScheduler().cancelTask(this.getTaskId());
        HandlerList.unregisterAll(this.listener);
        this.customListeners.forEach(it -> HandlerList.unregisterAll(it));

        this.cancel();
    }
}
