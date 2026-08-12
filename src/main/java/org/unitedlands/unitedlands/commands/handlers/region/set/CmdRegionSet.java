package org.unitedlands.unitedlands.commands.handlers.region.set;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.region.CmdRegion;


@UnitedSubCommand(
    parent          = CmdRegion.class,
    name            = "set",
    description     = "Region set commands",
    usage           = "/region set <administrator|name>",
    playerOnly      = true
)
public class CmdRegionSet implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
