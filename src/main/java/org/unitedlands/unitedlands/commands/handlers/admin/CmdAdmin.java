package org.unitedlands.unitedlands.commands.handlers.admin;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedCommand(
        name = "uladmin",
        aliases = { "ula" },
        description = "UnitedLands Admin commands",
        usage = "/uladmin <command>",
        permission = "united.lands.admin"
)
public class CmdAdmin implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
