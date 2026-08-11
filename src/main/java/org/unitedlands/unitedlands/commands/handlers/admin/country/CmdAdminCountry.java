package org.unitedlands.unitedlands.commands.handlers.admin.country;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.admin.CmdAdmin;

@UnitedSubCommand(
        parent = CmdAdmin.class,
        name = "country",
        aliases = { "c" },
        description = "Admin country commands",
        usage = "/ula country <command>"
)

public class CmdAdminCountry implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }

}
