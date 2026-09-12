package org.unitedlands.unitedlands.commands.handlers.admin.country.attributes;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.admin.country.CmdAdminCountry;

@UnitedSubCommand(
        parent = CmdAdminCountry.class,
        name = "attributes",
        description = "Admin country attribute commands",
        usage = "/ula country attributes <command> <argument>"
)
public class CmdAdminCountryAttributes implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
