package org.unitedlands.unitedlands.commands.handlers.web;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedCommand(
        name = "ulweb",
        description = "United Lands web command",
        usage = "/ulweb <command>",
        playerOnly = true
)
public class CmdWeb implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

    }

}
