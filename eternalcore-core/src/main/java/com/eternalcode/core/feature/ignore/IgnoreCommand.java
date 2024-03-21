package com.eternalcode.core.feature.ignore;

import com.eternalcode.annotations.scan.command.DescriptionDocs;
import com.eternalcode.core.event.EventCaller;
import com.eternalcode.core.feature.ignore.event.IgnoreAllEvent;
import com.eternalcode.core.injector.annotations.Inject;
import com.eternalcode.core.notice.NoticeService;
import com.eternalcode.core.user.User;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command(name = "ignore")
@Permission("eternalcore.ignore")
class IgnoreCommand {

    private final IgnoreService repository;
    private final NoticeService noticeService;
    private final EventCaller eventCaller;
    private final Server server;

    @Inject
    IgnoreCommand(IgnoreService repository, NoticeService noticeService, EventCaller eventCaller, Server server) {
        this.repository = repository;
        this.noticeService = noticeService;
        this.eventCaller = eventCaller;
        this.server = server;
    }

    @Execute
    @DescriptionDocs(description = "Ignore specified player", arguments = "<player>")
    void ignore(@Context User sender, @Arg User target) {
        UUID senderUuid = sender.getUniqueId();
        UUID targetUuid = target.getUniqueId();

        if (sender.equals(target)) {
            this.noticeService.viewer(sender, translation -> translation.privateChat().cantIgnoreYourself());
            return;
        }

        this.repository.isIgnored(senderUuid, targetUuid).thenAccept(isIgnored -> {
            if (isIgnored) {
                this.noticeService.create()
                    .user(sender)
                    .placeholder("{PLAYER}", target.getName())
                    .notice(translation -> translation.privateChat().alreadyIgnorePlayer())
                    .send();

                return;
            }

            this.repository.ignore(senderUuid, targetUuid).thenAccept(unused -> this.noticeService.create()
                .player(senderUuid)
                .placeholder("{PLAYER}", target.getName())
                .notice(translation -> translation.privateChat().ignorePlayer())
                .send());
        });
    }

    @Execute(name = "-all", aliases = "*")
    @DescriptionDocs(description = "Ignore all players")
    void ignoreAll(@Context User sender) {
        UUID senderUuid = sender.getUniqueId();

        Player player = this.server.getPlayer(senderUuid);
        IgnoreAllEvent event = this.eventCaller.callEvent(new IgnoreAllEvent(player));

        if (event.isCancelled()) {
            return;
        }

        this.repository.ignoreAll(senderUuid).thenAccept(blank -> this.noticeService.create()
            .player(senderUuid)
            .notice(translation -> translation.privateChat().ignoreAll())
            .send());
    }

}
