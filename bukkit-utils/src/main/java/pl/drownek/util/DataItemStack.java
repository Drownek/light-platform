package pl.drownek.util;

import eu.okaeri.configs.OkaeriConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@Getter
public class DataItemStack extends OkaeriConfig {

    private Material material;
    private String name;
    private List<String> lore;
    private @Nullable String itemsAdderId;

    public DataItemStack(Material material, String name, List<String> lore, @Nullable String itemsAdderId) {
        this.material = material;
        this.name = TextUtil.color(name);
        this.lore = TextUtil.color(lore);
        this.itemsAdderId = itemsAdderId;
    }

    public static DataItemStack of(Material material, String name, List<String> lore, String itemsAdderId) {
        return new DataItemStack(material, name, lore, itemsAdderId);
    }

    public static DataItemStack of(Material material, String name, List<String> lore) {
        return new DataItemStack(material, name, lore, null);
    }

    public static DataItemStack of(String name, List<String> lore) {
        return new DataItemStack(Material.PAPER, name, lore, null);
    }

    public static DataItemStack of(String name) {
        return new DataItemStack(Material.PAPER, name, List.of(), null);
    }

    public ItemStack getStack() {
        if (itemsAdderId == null) {
            return ItemStackBuilder.of(material)
                .name(name)
                .lore(lore)
                .asItemStack();
        }
        return ItemStackBuilder.furniture(itemsAdderId)
            .name(name)
            .lore(lore)
            .asItemStack();
    }

    public boolean isSimilar(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (itemStack.getType() != material) {
            return false;
        }

        if (!itemStack.hasItemMeta()) {
            return false;
        }

        if (!itemStack.getItemMeta().hasDisplayName()) {
            return false;
        }

        if (!itemStack.getItemMeta().getDisplayName().equals(name)) {
            return false;
        }

        return true;
    }

    @ApiStatus.Experimental
    public String extractPlaceholder(ItemStack itemStack, String placeholder) {
        if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) {
            return null;
        }

        String regexPlaceholder = "\\{" + placeholder.substring(1, placeholder.length() - 1) + "}";

        for (String template : this.lore) {
            for (String input : itemStack.getItemMeta().getLore()) {
                String regex = template.replaceAll(regexPlaceholder, "(.*)");

                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(input);

                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        return null;
    }

    public ItemStack getStack(Map<String, Object> replacements) {
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
                .asItemStack();
        }
        return ItemStackBuilder.furniture(itemsAdderId)
            .name(replacedName)
            .lore(replacedLore)
            .asItemStack();
    }
}
