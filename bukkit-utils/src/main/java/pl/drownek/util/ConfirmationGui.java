package pl.drownek.util;

import dev.triumphteam.gui.guis.Gui;
import lombok.Builder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static java.util.Optional.ofNullable;

@Builder
public class ConfirmationGui {

    public static final GuiItemInfo DEFAULT_YES = GuiItemInfo.of(11, Material.GREEN_WOOL, "&aTak");
    public static final GuiItemInfo DEFAULT_NO = GuiItemInfo.of(15, Material.RED_WOOL, "&cNie");
    private final @NonNull String action;
    private final @Nullable GuiItemInfo yesItem;
    private final @Nullable GuiItemInfo noItem;
    private final @Nullable GuiItemInfo confirmationItem;
    private final @Nullable Runnable successAction;
    private final @Nullable Runnable declineAction;
    private @Nullable List<String> info;

    public void open(Player player) {
        if (this.info == null) {
            this.info = List.of();
        }
        GuiItemInfo yes = requireNonNullElse(this.yesItem, DEFAULT_YES);
        GuiItemInfo no = requireNonNullElse(this.noItem, DEFAULT_NO);
        GuiItemInfo confirm = requireNonNullElseGet(this.confirmationItem,
            () -> GuiItemInfo.of(13, Material.PAPER, this.action, this.info.toArray(new String[0]))
        );

        Gui confirmationGui = Gui.gui().title(TextUtil.component(this.action)).rows(3).disableAllInteractions().create();
        confirmationGui.getFiller().fill(ItemStackBuilder.of(Material.GRAY_STAINED_GLASS_PANE).asGuiItem());
        confirmationGui.setItem(no.getPosition(), no.asGuiItem(clickEvent -> {
            if (this.declineAction != null) {
                this.declineAction.run();
            } else {
                confirmationGui.close(player);
            }
        }));
        confirmationGui.setItem(confirm.getPosition(), confirm.asGuiItem());
        confirmationGui.setItem(yes.getPosition(), yes.asGuiItem(clickEvent -> {
            if (this.successAction != null) {
                this.successAction.run();
            } else {
                confirmationGui.close(player);
            }
        }));
        confirmationGui.open(player);
    }
}
