package org.unitedlands.unitedlands.commands.handlers.settlement.trust;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.settlement.CmdSettlement;

@UnitedSubCommand(
    parent          = CmdSettlement.class,
    name            = "trust",
    description     = "Manages settlement trust",
    usage           = "/settlement trust <add|remove|list> [player]",
    playerOnly      = true
)
public class CmdSettlementTrust implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }

}
