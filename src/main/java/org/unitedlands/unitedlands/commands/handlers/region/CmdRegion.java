package org.unitedlands.unitedlands.commands.handlers.region;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedCommand(
        name = "region",
        aliases = { "r" },
        description = "Shows information about the current region",
        usage = "/region <region_name>",
        playerOnly = true
)

public class CmdRegion implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }

}
