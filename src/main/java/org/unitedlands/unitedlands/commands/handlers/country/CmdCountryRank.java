package org.unitedlands.unitedlands.commands.handlers.country;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;


@UnitedSubCommand(
    parent          = CmdCountry.class,
    name            = "rank",
    description     = "Manages country ranks",
    usage           = "/country rank <add|remove> <player> <rank>",
    playerOnly      = true
)
public class CmdCountryRank implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
