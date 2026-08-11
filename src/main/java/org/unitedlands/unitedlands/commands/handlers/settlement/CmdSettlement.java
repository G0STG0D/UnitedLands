package org.unitedlands.unitedlands.commands.handlers.settlement;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedCommand(
    name            = "settlement",
    aliases         = { "s" },
    description     = "Player settlement commands",
    usage           = "/settlement",
    playerOnly      = true
)
public class CmdSettlement implements UnitedCommandExecutor {


    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
