package pl.drownek.util;

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
@Getter
public class GuiItemInfo extends OkaeriConfig {
    private @Nullable String itemsAdderId;
    private int position;
    private Material material;
    private String name;
    private List<String> lore;

    public GuiItemInfo(int position, Material material, String name, List<String> lore) {
        this.itemsAdderId = null;
        this.position = position;
        this.material = material;
        this.name = name;
        this.lore = lore;
    }

    public GuiItemInfo(int position, Material material, String name, String... lore) {
        this(null, position, material, name, lore);
    }

    public GuiItemInfo(@Nullable String itemsAdderId, int position, Material material, String name, String... lore) {
        this.itemsAdderId = itemsAdderId;
        this.position = position;
        this.material = material;
        this.name = name;
        this.lore = new ArrayList<>(List.of(lore));
    }

    public GuiItemInfo(GuiItemInfo guiItemInfo) {
        this(guiItemInfo.itemsAdderId, guiItemInfo.position, guiItemInfo.material, guiItemInfo.name, guiItemInfo.getLore().toArray(new String[0]));
    }

    public static GuiItemInfo of(@Nullable String itemsAdderId, int position, Material material, String name, String... lore) {
        return new GuiItemInfo(itemsAdderId, position, material, name, lore);
    }

    public static GuiItemInfo of(int position, Material material, String name, String... lore) {
        return new GuiItemInfo(null, position, material, name, lore);
    }

    public static GuiItemInfo of(int position, String name, String... lore) {
        return new GuiItemInfo(null, position, Material.PAPER, name, lore);
    }

    public static GuiItemInfo of(int position, String name) {
        return new GuiItemInfo(null, position, Material.PAPER, name);
    }

    public GuiItemInfo with(String key, Object value) {
        String replacedName = name;
        List<String> replacedLore = new ArrayList<>(lore);

        String replacement = String.valueOf(value);

        replacedName = replacedName.replace(key, replacement);
        replacedLore = replacedLore.stream()
            .map(line -> line.replace(key, replacement))
            .toList();

        return new GuiItemInfo(this.position, this.material, replacedName, replacedLore);
    }

    public GuiItemInfo appendLore(List<String> lore) {
        GuiItemInfo i = new GuiItemInfo(this);
        i.lore.addAll(lore);
        return i;
    }

    public GuiItem asGuiItem() {
        if (itemsAdderId == null) {
            return ItemStackBuilder.of(material)
                .name(name)
                .lore(lore)
                .asGuiItem();
        }
        return ItemStackBuilder.furniture(itemsAdderId)
            .name(name)
            .lore(lore)
            .asGuiItem();
    }

    public GuiItem asGuiItem(GuiAction<InventoryClickEvent> event) {
        if (itemsAdderId == null) {
            return ItemStackBuilder.of(material)
                .name(name)
                .lore(lore)
                .asGuiItem(event);
        }
        return ItemStackBuilder.furniture(itemsAdderId)
            .name(name)
            .lore(lore)
            .asGuiItem(event);
    }

    public GuiItem asGuiItem(Map<String, Object> replacements) {
        String replacedName = name;
        List<String> replacedLore = new ArrayList<>(lore);

        for (Map.Entry<String, Object> entry : replacements.entrySet()) {
            String replacement = String.valueOf(entry.getValue());
            String key = entry.getKey();

            replacedName = replacedName.replace(key, replacement);
            replacedLore = replacedLore.stream()
                .map(line -> line.replace(key, replacement))
                .toList();
        }

        if (itemsAdderId == null) {
            return ItemStackBuilder.of(material)
                .name(replacedName)
                .lore(replacedLore)
                .asGuiItem();
        }
        return ItemStackBuilder.furniture(itemsAdderId)
            .name(replacedName)
            .lore(replacedLore)
            .asGuiItem();
    }

    public GuiItem asGuiItem(Map<String, Object> replacements, GuiAction<InventoryClickEvent> event) {
        String replacedName = name;
        List<String> replacedLore = new ArrayList<>(lore);

        for (Map.Entry<String, Object> entry : replacements.entrySet()) {
            String replacement = String.valueOf(entry.getValue());
            String key = entry.getKey();

            replacedName = replacedName.replace(key, replacement);
            replacedLore = replacedLore.stream()
                .map(line -> line.replace(key, replacement))
                .collect(Collectors.toList());
        }

        if (itemsAdderId == null) {
            return ItemStackBuilder.of(material)
                .name(replacedName)
                .lore(replacedLore)
                .asGuiItem(event);
        }
        return ItemStackBuilder.furniture(itemsAdderId)
            .name(replacedName)
            .lore(replacedLore)
            .asGuiItem(event);
    }

    public void setGuiItem(BaseGui gui) {
        gui.setItem(this.position, this.asGuiItem());
    }

    public void setGuiItem(BaseGui gui, GuiAction<InventoryClickEvent> event) {
        gui.setItem(this.position, this.asGuiItem(event));
    }

    public void setGuiItem(BaseGui gui, Map<String, Object> replacements) {
        gui.setItem(this.position, this.asGuiItem(replacements));
    }

    public void setGuiItem(BaseGui gui, Map<String, Object> replacements, GuiAction<InventoryClickEvent> event) {
        gui.setItem(this.position, this.asGuiItem(replacements, event));
    }
}
