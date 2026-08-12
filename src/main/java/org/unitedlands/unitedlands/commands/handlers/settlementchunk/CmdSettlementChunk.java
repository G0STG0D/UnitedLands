package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedCommand(
        name = "settlementchunk",
        aliases = { "plot" },
        description = "Settlement chunk commands",
        usage = "/settlementchunk <command>"
)
public class CmdSettlementChunk implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }

}
