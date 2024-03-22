package com.eternalcode.core.feature.warp;

import com.eternalcode.commons.adventure.AdventureUtil;
import com.eternalcode.commons.bukkit.position.PositionAdapter;
import com.eternalcode.core.configuration.contextual.ConfigItem;
import com.eternalcode.core.feature.warp.config.WarpInventoryItem;
import com.eternalcode.core.injector.annotations.Inject;
import com.eternalcode.core.injector.annotations.component.Service;
import com.eternalcode.core.feature.language.Language;
import com.eternalcode.core.feature.teleport.TeleportTaskService;
import com.eternalcode.core.translation.Translation;
import com.eternalcode.core.translation.TranslationManager;
import dev.triumphteam.gui.builder.item.BaseItemBuilder;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class WarpInventory {

    private final TeleportTaskService teleportTaskService;
    private final TranslationManager translationManager;
    private final WarpManager warpManager;
    private final Server server;
    private final MiniMessage miniMessage;

    @Inject
    WarpInventory(TeleportTaskService teleportTaskService, TranslationManager translationManager, WarpManager warpManager, Server server, MiniMessage miniMessage) {
        this.teleportTaskService = teleportTaskService;
        this.translationManager = translationManager;
        this.warpManager = warpManager;
        this.server = server;
        this.miniMessage = miniMessage;
    }

    private Gui createInventory(Language language) {
        Translation translation = this.translationManager.getMessages(language);
        Translation.WarpSection.WarpInventorySection warpSection = translation.warp().warpInventory();

        Gui gui = Gui.gui()
            .title(this.miniMessage.deserialize(warpSection.title()))
            .rows(warpSection.rows())
            .disableAllInteractions()
            .create();

        warpSection.items().values().forEach(item -> {
            Optional<Warp> warpOptional = this.warpManager.findWarp(item.warpName());

            if (warpOptional.isEmpty()) {
                return;
            }

            Warp warp = warpOptional.get();
            ConfigItem warpItem = item.warpItem();

            BaseItemBuilder baseItemBuilder = this.createItem(warpItem);
            GuiItem guiItem = baseItemBuilder.asGuiItem();

            guiItem.setAction(event -> {
                Player player = (Player) event.getWhoClicked();

                player.closeInventory();

                if (player.hasPermission("eternalcore.warp.bypass")) {
                    this.teleportTaskService.createTeleport(player.getUniqueId(), PositionAdapter.convert(player.getLocation()), PositionAdapter.convert(warp.getLocation()), Duration.ZERO);
                    return;
                }

                this.teleportTaskService.createTeleport(
                    player.getUniqueId(),
                    PositionAdapter.convert(player.getLocation()),
                    PositionAdapter.convert(warp.getLocation()),
                    Duration.ofSeconds(5)
                );
            });

            gui.setItem(warpItem.slot(), guiItem);
        });

        if (warpSection.border().enabled()) {
            Translation.WarpSection.WarpInventorySection.BorderSection borderSection = warpSection.border();

            ItemBuilder borderItem = ItemBuilder.from(borderSection.material());

            if (!borderSection.name().equals("")) {
                borderItem.name(AdventureUtil.resetItalic(this.miniMessage.deserialize(borderSection.name())));
            }

            if (!borderSection.lore().isEmpty()) {
                borderItem.lore(borderSection.lore()
                    .stream()
                    .map(entry -> AdventureUtil.resetItalic(this.miniMessage.deserialize(entry)))
                    .collect(Collectors.toList()));
            }

            GuiItem guiItem = new GuiItem(borderItem.build());

            switch (borderSection.fillType()) {
                case BORDER -> gui.getFiller().fillBorder(guiItem);
                case ALL -> gui.getFiller().fill(guiItem);
                case TOP -> gui.getFiller().fillTop(guiItem);
                case BOTTOM -> gui.getFiller().fillBottom(guiItem);
                default -> throw new IllegalStateException("Unexpected value: " + borderSection.fillType());
            }
        }

        for (ConfigItem item : warpSection.decorationItems().items()) {
            BaseItemBuilder baseItemBuilder = this.createItem(item);
            GuiItem guiItem = baseItemBuilder.asGuiItem();

            guiItem.setAction(event -> {
                Player player = (Player) event.getWhoClicked();

                if (item.commands.isEmpty()) {
                    return;
                }

                for (String command : item.commands) {
                    this.server.dispatchCommand(player, command);
                }

                player.closeInventory();
            });

            gui.setItem(item.slot(), guiItem);
        }

        return gui;
    }

    private BaseItemBuilder createItem(ConfigItem item) {
        Component name = AdventureUtil.resetItalic(this.miniMessage.deserialize(item.name()));
        List<Component> lore = item.lore()
            .stream()
            .map(entry -> AdventureUtil.resetItalic(this.miniMessage.deserialize(entry)))
            .toList();

        if (item.material() == Material.PLAYER_HEAD && !item.texture().isEmpty()) {
            return ItemBuilder.skull()
                .name(name)
                .lore(lore)
                .texture(item.texture())
                .glow(item.glow());
        }

        return ItemBuilder.from(item.material())
            .name(name)
            .lore(lore)
            .glow(item.glow());
    }

    public void addItem(Warp warp) {

        Translation translationEN = this.translationManager.getMessages(Language.EN);
        Translation.WarpSection.WarpInventorySection warpSectionEN = translationEN.warp().warpInventory();

        Translation translationPL = this.translationManager.getMessages(Language.PL);
        Translation.WarpSection.WarpInventorySection warpSectionPL = translationPL.warp().warpInventory();

        int slotNumber = warpSectionEN.items().values().size() + 10;

        WarpInventoryItem warpInventoryItemEN = new WarpInventoryItem();

        warpInventoryItemEN.warpName = warp.getName();
        warpInventoryItemEN.warpItem = ConfigItem.builder()
            .withName("&6Warp: &f" + warp.getName())
            .withLore(Collections.singletonList("&7Click to teleport to warp"))
            .withMaterial(Material.PLAYER_HEAD)
            .withTexture("ewogICJ0aW1lc3RhbXAiIDogMTY2NDAzNTM1MjUyNCwKICAicHJvZmlsZUlkIiA6ICJjYjIzZWZhOWY1N2U0ZTQyOGE0MDU2OTM4NDlhODAxZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJWMUdHTyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MThhZjBiODNhZGZmNzM1MDA3ZmVkMjMwMTkxOWMwYjYzZWJmZTgwZTVkNjFiYTkzN2M5MmViMWVhY2Y2ZDI4IgogICAgfQogIH0KfQ==")
            .withSlot(slotNumber)
            .withGlow(true)
            .build();

        WarpInventoryItem warpInventoryItemPL = new WarpInventoryItem();

        warpInventoryItemPL.warpName = warp.getName();
        warpInventoryItemPL.warpItem = ConfigItem.builder()
            .withName("&6Warp: &f" + warp.getName())
            .withLore(Collections.singletonList("&7Click to teleport to warp"))
            .withMaterial(Material.PLAYER_HEAD)
            .withTexture("ewogICJ0aW1lc3RhbXAiIDogMTY2NDAzNTM1MjUyNCwKICAicHJvZmlsZUlkIiA6ICJjYjIzZWZhOWY1N2U0ZTQyOGE0MDU2OTM4NDlhODAxZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJWMUdHTyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MThhZjBiODNhZGZmNzM1MDA3ZmVkMjMwMTkxOWMwYjYzZWJmZTgwZTVkNjFiYTkzN2M5MmViMWVhY2Y2ZDI4IgogICAgfQogIH0KfQ==")
            .withSlot(slotNumber)
            .withGlow(true)
            .build();

        warpSectionEN.items().put(warp.getName(), warpInventoryItemEN);
        warpSectionPL.items().put(warp.getName(), warpInventoryItemPL);

    }

    public void openInventory(Player player, Language language) {
        this.createInventory(language).open(player);
    }
}
