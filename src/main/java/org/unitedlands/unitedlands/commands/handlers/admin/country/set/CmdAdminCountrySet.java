package org.unitedlands.unitedlands.commands.handlers.admin.country.set;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.admin.country.CmdAdminCountry;

@UnitedSubCommand(
        parent = CmdAdminCountry.class,
        name = "set",
        description = "Admin country set commands",
        usage = "/ula country set <command> <argument>"
)
public class CmdAdminCountrySet implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
