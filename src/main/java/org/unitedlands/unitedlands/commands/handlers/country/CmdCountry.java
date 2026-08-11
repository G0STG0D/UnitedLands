package org.unitedlands.unitedlands.commands.handlers.country;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedCommand(
    name            = "country",
    aliases         = { "c" },
    description     = "Shows information about the country",
    usage           = "/country",
    playerOnly      = true
)
public class CmdCountry implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
