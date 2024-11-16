package pl.drownek.util.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import pl.drownek.util.CommandUtil;
import pl.drownek.util.TextUtil;
import pl.drownek.util.data.step.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DataGatherer {

    public static final Component CONFIRM_MESSAGE = MiniMessage.miniMessage().deserialize(
        "Kliknij F aby potwierdzić " +
        "[" +
        "<click:run_command:/datagatherer-no><hover:show_text:Kliknij, aby ponownie ustawić>Powtórz krok</hover></click>" +
        "]"
    );
    public static final String CANCEL_MESSAGE = "<click:run_command:/datagatherer-exit><hover:show_text:Kliknij, by anulować><dark_green>[Anuluj]</hover></click>";

    private final Plugin plugin;

    private final List<Step<?>> steps;
    private final @Nullable Runnable startAction;
    private final @Nullable Runnable endAction;
    private final @Nullable Consumer<Integer> afterEachStepAction;
    private final List<Listener> registeredListeners = new ArrayList<>();
    private int currentStep;
    private final boolean confirmActions;
    private final @Nullable Runnable cancelAction;
    private final boolean displaySetValues;
    private final boolean displaySuccessMessage;

    DataGatherer(Plugin plugin,
                 List<Step<?>> steps,
                 @Nullable Runnable startAction,
                 @Nullable Runnable endAction,
                 @Nullable Consumer<Integer> afterEachStepAction,
                 boolean confirmActions,
                 @Nullable Runnable cancelAction,
                 boolean displaySetValues,
                 boolean displaySuccessMessage) {
        this.plugin = plugin;
        this.steps = steps;
        this.startAction = startAction;
        this.endAction = endAction;
        this.afterEachStepAction = afterEachStepAction;
        this.confirmActions = confirmActions;
        this.displaySetValues = displaySetValues;
        this.displaySuccessMessage = displaySuccessMessage;
        this.cancelAction = cancelAction;
    }

    public static DataGathererBuilder builder(Plugin plugin) {
        return new DataGathererBuilder(plugin);
    }

    public void start(Player player) {
        if (DataGathererManager.playersInDataGatherer.contains(player)) {
            TextUtil.message(player, "&cJesteś już w trakcie tworzenia!");
            return;
        }

        DataGathererManager.playersInDataGatherer.add(player);

        if (this.startAction != null) {
            this.startAction.run();
        }

        registerListener(new Listener() {
            @EventHandler
            public void onCommand(PlayerCommandPreprocessEvent event) {
                if (!event.getPlayer().equals(player)) {
                    return;
                }

                if (event.getMessage().equalsIgnoreCase("/datagatherer-exit")) {
                    event.setCancelled(true);

                    unregister();
                    TextUtil.message(player, "&cAnulowano!");
                    if (cancelAction != null) {
                        cancelAction.run();
                    }
                    DataGathererManager.playersInDataGatherer.remove(player);
                }
            }
        });

        this.handleCurrentStep(player);
    }

    private void unregister() {
        for (Listener registeredListener : registeredListeners) {
            HandlerList.unregisterAll(registeredListener);
        }
    }

    public void registerListener(Listener listener) {
        registeredListeners.add(listener);
        plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
    }

    public <T> void handleCurrentStep(Player player) {
        Step<T> step = (Step<T>) this.steps.get(this.currentStep);

        TextUtil.adventure.player(player).sendMessage(MiniMessage.miniMessage().deserialize("<green>" + step.getInfo() + " " + CANCEL_MESSAGE));

        BiConsumer<Listener, T> callback = (listener, value) -> {
            if (step.getResultHandler() != null) {
                StepResult result = step.getResultHandler().apply(value);
                if (!result.isSuccess()) {
                    HandlerList.unregisterAll(listener);
                    if (result.message() != null) {
                        TextUtil.message(player, result.message());
                    }
                    if (!result.isExit()) {
                        handleCurrentStep(player);
                    }
                    return;
                }
            }

            if (displaySetValues && step.isDisplaySetValue()) {
                TextUtil.message(player, "Ustawiono na: " + Optional.ofNullable(step.toString(value)).orElse(value.toString()));
            }

            if (confirmActions && step.isConfirmAction()) {
                TextUtil.adventure.player(player).sendMessage(CONFIRM_MESSAGE);
                registerListener(new Listener() {
                    @EventHandler
                    public void onCommand(PlayerCommandPreprocessEvent event) {
                        if (!event.getPlayer().equals(player)) {
                            return;
                        }

                        if (event.getMessage().equalsIgnoreCase("/datagatherer-no")) {
                            event.setCancelled(true);
                            handleCurrentStep(player);
                            HandlerList.unregisterAll(this);
                        }
                    }

                    @EventHandler
                    public void handle(PlayerSwapHandItemsEvent event) {
                        if (!event.getPlayer().equals(player)) {
                            return;
                        }
                        event.setCancelled(true);
                        confirmStep(step, value, player);
                        HandlerList.unregisterAll(this);
                    }
                });
            } else {
                confirmStep(step, value, player);
            }

            HandlerList.unregisterAll(listener);
        };
        Listener stepListener = step.handle(this.plugin, this, player, callback);
        registerListener(stepListener);
    }

    private <T> void confirmStep(Step<T> step, T value, Player player) {
        step.getConsumer().accept(value);

        if (afterEachStepAction != null) {
            afterEachStepAction.accept(currentStep);
        }
        if (++currentStep >= steps.size()) {
            unregister();
            if (endAction != null) {
                Bukkit.getScheduler().runTask(this.plugin, endAction);
            }
            if (displaySuccessMessage) {
                TextUtil.message(player, CommandUtil.SUCCESS_MESSAGE);
            }
            DataGathererManager.playersInDataGatherer.remove(player);
        } else {
            handleCurrentStep(player);
        }
    }

    public static class DataGathererBuilder {

        private final Plugin plugin;
        private List<Step<?>> steps;
        private Runnable startAction;
        private Runnable endAction;
        private Consumer<Integer> afterEachStepAction;
        private Runnable cancelAction;
        private boolean confirmActions = true;
        private boolean displaySetValues = true;
        private boolean displaySuccessMessage = true;

        DataGathererBuilder(Plugin plugin) {
            this.plugin = plugin;
        }

        public DataGathererBuilder withoutConfirm() {
            this.confirmActions = false;
            return this;
        }

        public DataGathererBuilder steps(List<Step<?>> steps) {
            this.steps = steps;
            return this;
        }

        public DataGathererBuilder steps(Step<?>... steps) {
            this.steps = List.of(steps);
            return this;
        }

        public DataGathererBuilder startAction(Runnable startAction) {
            this.startAction = startAction;
            return this;
        }

        public DataGathererBuilder endAction(Runnable endAction) {
            this.endAction = endAction;
            return this;
        }

        public DataGathererBuilder afterEachStepAction(Consumer<Integer> afterEachStepAction) {
            this.afterEachStepAction = afterEachStepAction;
            return this;
        }

        public DataGathererBuilder cancelAction(Runnable cancelAction) {
            this.cancelAction = cancelAction;
            return this;
        }

        public DataGathererBuilder withoutDisplaySetValues() {
            this.displaySetValues = false;
            return this;
        }

        public DataGathererBuilder withoutSuccessMessage() {
            this.displaySuccessMessage = false;
            return this;
        }

        public DataGatherer build() {
            return new DataGatherer(this.plugin,
                this.steps,
                this.startAction,
                this.endAction,
                this.afterEachStepAction,
                this.confirmActions,
                this.cancelAction,
                this.displaySetValues,
                this.displaySuccessMessage);
        }
    }
}
