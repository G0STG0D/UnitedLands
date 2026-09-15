package org.unitedlands.unitedlands.commands.handlers.country.titles;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.country.CmdCountry;

@UnitedSubCommand(
    parent          = CmdCountry.class,
    name            = "title",
    description     = "Manages country titles",
    usage           = "/country title <command> <argument>",
    playerOnly      = true
)
public class CmdCountryTitle implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

    }

}
