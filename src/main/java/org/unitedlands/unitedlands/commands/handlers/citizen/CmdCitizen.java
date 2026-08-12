package org.unitedlands.unitedlands.commands.handlers.citizen;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedCommand(
        name = "citizen",
        aliases = { "cit" },
        description = "Shows information about a citizen",
        usage = "/citizen <command>"
)
public class CmdCitizen implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }

}
