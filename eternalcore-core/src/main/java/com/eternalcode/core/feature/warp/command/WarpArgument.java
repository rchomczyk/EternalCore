package com.eternalcode.core.feature.warp.command;

import com.eternalcode.core.bridge.litecommand.argument.AbstractViewerArgument;
import com.eternalcode.core.feature.warp.Warp;
import com.eternalcode.core.feature.warp.WarpService;
import com.eternalcode.core.injector.annotations.Inject;
import com.eternalcode.core.injector.annotations.lite.LiteArgument;
import com.eternalcode.core.translation.Translation;
import com.eternalcode.core.translation.TranslationManager;
import com.eternalcode.core.viewer.ViewerService;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import java.util.Optional;
import org.bukkit.command.CommandSender;

@LiteArgument(type = Warp.class)
class WarpArgument extends AbstractViewerArgument<Warp> {

    private final WarpService warpService;

    @Inject
    WarpArgument(WarpService warpService, TranslationManager translationManager, ViewerService viewerService) {
        super(viewerService, translationManager);
        this.warpService = warpService;
    }

    @Override
    public ParseResult<Warp> parse(Invocation<CommandSender> invocation, String argument, Translation translation) {
        Optional<Warp> warpOption = this.warpService.findWarp(argument);

        return warpOption.map(ParseResult::success)
            .orElseGet(() -> ParseResult.failure(translation.warp().notExist()));
    }

    @Override
    public SuggestionResult suggest(
        Invocation<CommandSender> invocation,
        Argument<Warp> argument,
        SuggestionContext context
    ) {
        return this.warpService.getNamesOfWarps().stream()
            .collect(SuggestionResult.collector());
    }
}

