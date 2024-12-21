package pl.drownek.util;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.builder.gui.SimpleBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import eu.okaeri.configs.OkaeriConfig;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class GuiSettings extends OkaeriConfig {

    public String title;
    public int rows;

    public boolean fillGui = false;
    public ItemStack filler = new ItemStack(XMaterial.GRAY_STAINED_GLASS_PANE.get());

    public GuiSettings(String title, int rows) {
        this.title = title;
        this.rows = rows;
    }

    public GuiSettings(String title, int rows, boolean fillGui) {
        this.title = title;
        this.rows = rows;
        this.fillGui = fillGui;
    }

    public SimpleBuilder toGuiBuilder() {
        return Gui.gui()
            .title(TextUtil.component(this.title))
            .rows(this.rows)
            .apply(gui -> {
                if (this.fillGui) {
                    gui.getFiller().fillBorder(new GuiItem(this.filler));
                }
            });
    }
}
