/*
 * Copyright (c) 2022. EternalCode.pl
 */

package com.eternalcode.core.command.implementations;

import com.eternalcode.core.command.binds.MessageAction;
import com.eternalcode.core.configuration.MessagesConfiguration;
import com.eternalcode.core.utils.ChatUtils;
import dev.rollczi.litecommands.annotations.Arg;
import dev.rollczi.litecommands.annotations.Execute;
import dev.rollczi.litecommands.annotations.MinArgs;
import dev.rollczi.litecommands.annotations.Permission;
import dev.rollczi.litecommands.annotations.Section;
import dev.rollczi.litecommands.annotations.UsageMessage;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

@Section(route = "alert", aliases = {"broadcast", "bc"})
@Permission("eternalcore.command.alert")
@UsageMessage("&8» &cPoprawne użycie &7/alert <title/actionbar/chat> <text>")
public class AlertCommand {

    @Execute
    @MinArgs(2)
    public void execute(String[] args, @Arg(0) MessageAction messageAction, MessagesConfiguration message) {
        String text = StringUtils.join(args, " ", 1, args.length);
        Component component = ChatUtils.component(message.messagesSection.alertMessagePrefix)
            .replaceText(builder -> builder.match("\\{BROADCAST}").replacement(text));

        Bukkit.getOnlinePlayers().forEach(player -> messageAction.action(player, component));
    }
}
