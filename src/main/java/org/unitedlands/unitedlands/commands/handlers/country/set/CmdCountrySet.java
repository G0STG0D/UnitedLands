package org.unitedlands.unitedlands.commands.handlers.country.set;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.country.CmdCountry;

@UnitedSubCommand(
    parent          = CmdCountry.class,
    name            = "set",
    description     = "Country set commands",
    usage           = "/country set <option> <argument>",
    playerOnly      = true
)
public class CmdCountrySet implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
