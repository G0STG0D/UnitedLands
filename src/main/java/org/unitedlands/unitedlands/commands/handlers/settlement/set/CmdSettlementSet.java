package org.unitedlands.unitedlands.commands.handlers.settlement.set;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.settlement.CmdSettlement;

@UnitedSubCommand(
    parent          = CmdSettlement.class,
    name            = "set",
    description     = "Misc settlement set commands",
    usage           = "/settlement set <command> <argument>",
    playerOnly      = true
)
public class CmdSettlementSet implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }

}
