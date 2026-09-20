package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;

import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdSettlementChunk.class,
        name = "evict",
        description = "Evicts the current owner of a settlement chunk",
        usage = "/settlementchunk evict",
        playerOnly = true
)
public class CmdSettlementChunkEvict extends SettlementChunkCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "settlement.plot.evict");
        if (context == null)
            return;
        
        if (!context.settlementChunk().hasOwner()) {
            United.messenger().send(context.player(), "player.settlementchunk.evict.not-owner");
            return;
        }

        context.settlementChunk().removeOwner();
        UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());

        United.messenger().send(context.player(), "player.settlementchunk.evict.success");

    }

}
