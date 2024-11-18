package pl.drownek.util;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public final class GuiHelper {

    public static final ItemStack NEXT_ITEM;
    public static final ItemStack PREVIOUS_ITEM;
    public static final ItemStack BACK_ITEM;

    static {
        NEXT_ITEM = ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY3MWM0YzA0MzM3YzM4YTVjN2YzMWE1Yzc1MWY5OTFlOTZjMDNkZjczMGNkYmVlOTkzMjA2NTVjMTlkIn19fQ==").name(TextUtil.component("&aNastepna strona")).build();
        PREVIOUS_ITEM = ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM5NzExMjRiZTg5YWM3ZGM5YzkyOWZlOWI2ZWZhN2EwN2NlMzdjZTFkYTJkZjY5MWJmODY2MzQ2NzQ3N2M3In19fQ==").name(TextUtil.component("&cPoprzednia strona")).build();
        BACK_ITEM = ItemBuilder.skull().texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmM2OGEzNDJkZjZjOWU3MTFlYzNiYzEzOGY5MWNkYjFiMGZhNWU3MmY2NmU0MjUxODc1ZDhiZWRkMDU1M2ViNSJ9fX0=").name(TextUtil.component("&cPowrot")).build();
    }

    private GuiHelper() {
    }

    public static PaginatedGui defaultPaginatedGui(String title, Consumer<PaginatedGui> consumer) {
        return defaultPaginatedGui(title, () -> {}, consumer);
    }

    public static PaginatedGui defaultPaginatedGui(String title, Runnable backAction, Consumer<PaginatedGui> consumer) {
        PaginatedGui gui = Gui.paginated().rows(6).title(Component.empty()).pageSize(28).disableAllInteractions().create();

        gui.getFiller().fillBorder(new GuiItem(Material.GRAY_STAINED_GLASS_PANE));

        gui.setItem(6, 4, ItemStackBuilder.of(GuiHelper.PREVIOUS_ITEM).asGuiItem((inventoryClickEvent) -> {
            gui.previous();
            updatePageTitle(title, gui);
        }));
        gui.setItem(6, 5, ItemStackBuilder.of(Material.ARROW).asGuiItem((inventoryClickEvent) -> backAction.run()));
        gui.setItem(6, 6, ItemStackBuilder.of(GuiHelper.NEXT_ITEM).asGuiItem((inventoryClickEvent) -> {
            gui.next();
            updatePageTitle(title, gui);
        }));

        consumer.accept(gui);
        updatePageTitle(title, gui);

        return gui;
    }

    private static void updatePageTitle(String title, PaginatedGui gui) {
        gui.updateTitle(TextUtil.color(String.format(title + " (%s/%s)", gui.getCurrentPageNum(), gui.getPagesNum())));
    }
}
