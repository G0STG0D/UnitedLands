package org.unitedlands.unitedlands.commands.handlers.settlement.rank;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.settlement.CmdSettlement;

@UnitedSubCommand(
    parent          = CmdSettlement.class,
    name            = "rank",
    description     = "Manages settlement ranks",
    usage           = "/settlement rank <add|remove> <player> <rank>",
    playerOnly      = true
)
public class CmdSettlementRank implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
