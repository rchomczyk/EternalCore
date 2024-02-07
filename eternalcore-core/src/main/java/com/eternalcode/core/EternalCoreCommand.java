package com.eternalcode.core;

import com.eternalcode.annotations.scan.command.DescriptionDocs;
import com.eternalcode.core.configuration.ConfigurationManager;
import com.eternalcode.core.event.EventCaller;
import com.eternalcode.core.feature.reload.EternalCoreReloadEvent;
import com.eternalcode.core.injector.annotations.Inject;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Command(name = "eternalcore")
@Permission("eternalcore.eternalcore")
class EternalCoreCommand {

    private static final String RELOAD_MESSAGE =
        "<b><gradient:#29fbff:#38b3ff>EternalCore:</gradient></b> <green>Configuration has ben successfully reloaded in %d ms.</green>";

    private final ConfigurationManager configurationManager;
    private final MiniMessage miniMessage;
    private final EventCaller eventCaller;

    @Inject
    EternalCoreCommand(ConfigurationManager configurationManager, MiniMessage miniMessage, EventCaller eventCaller) {
        this.configurationManager = configurationManager;
        this.miniMessage = miniMessage;
        this.eventCaller = eventCaller;
    }

    @Async
    @Execute(name = "reload")
    @DescriptionDocs(description = "Reloads EternalCore configuration")
    void reload(@Context Audience audience) {
        long millis = this.reload();
        Component message = this.miniMessage.deserialize(RELOAD_MESSAGE.formatted(millis));

        audience.sendMessage(message);
    }

    private long reload() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        this.eventCaller.callEvent(new EternalCoreReloadEvent());
        this.configurationManager.reload();

        return stopwatch.elapsed(TimeUnit.MILLISECONDS);
    }
}
