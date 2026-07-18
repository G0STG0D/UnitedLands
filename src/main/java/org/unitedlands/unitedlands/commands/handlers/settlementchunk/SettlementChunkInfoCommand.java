package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.classes.infoscreen.SettlementChunkInfoScreen;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;

public class SettlementChunkInfoCommand extends SettlementChunkCommandHandler {

    public SettlementChunkInfoCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

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

        var screen = new SettlementChunkInfoScreen(plugin, messageProvider, context.settlementChunk());
        screen.send(context.player());

    }
}
