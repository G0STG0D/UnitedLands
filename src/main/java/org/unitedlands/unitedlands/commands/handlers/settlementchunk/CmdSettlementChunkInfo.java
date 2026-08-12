package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.classes.infoscreen.SettlementChunkInfoScreen;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;

@UnitedSubCommand(
        parent = CmdSettlementChunk.class,
        name = "info",
        description = "Shows information about a settlement chunk",
        usage = "/settlementchunk info",
        playerOnly = true
)
public class CmdSettlementChunkInfo extends SettlementChunkCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return UnitedLandsDataManager.instance().getSettlementNames();
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, null);
        if (context == null)
            return;

        var screen = new SettlementChunkInfoScreen(context.settlementChunk());
        screen.send(context.player());

    }
}
