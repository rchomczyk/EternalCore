package com.eternalcode.core.configuration.implementation;

import com.eternalcode.core.configuration.ReloadableConfig;
import com.eternalcode.core.configuration.contextual.ConfigItem;
import com.eternalcode.core.feature.warp.config.WarpInventoryItem;
import com.eternalcode.core.injector.annotations.component.ConfigurationFile;

import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;
import org.bukkit.Material;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ConfigurationFile
public class WarpGuiConfiguration implements ReloadableConfig {

    public Map<String, WarpInventoryItem> warpItems = Map.of("example", WarpInventoryItem.builder()
        .withWarpName("example")
        .withWarpItem(ConfigItem.builder()
            .withName("&8» &Example: &fWarp")
            .withLore(Collections.singletonList("<gray>Example of lore!"))
            .withMaterial(Material.ENDER_PEARL)
            .withSlot(10)
            .withGlow(true)
            .build())
        .build());


    public int warpItemsRows = 4;


    public boolean borderEnabled = true;

    public Material borderMaterial = Material.GRAY_STAINED_GLASS_PANE;

    public FillType borderFillType = FillType.ALL;

    public String borderItemName = "";

    public List<String> borderItemLore = Collections.emptyList();

    public enum FillType {
        TOP, BOTTOM, BORDER, ALL
    }

    public List<ConfigItem> decorationItems = List.of();

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "warpGui.yml");
    }
}
