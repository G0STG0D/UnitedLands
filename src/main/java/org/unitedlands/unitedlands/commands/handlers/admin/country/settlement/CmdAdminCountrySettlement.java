package org.unitedlands.unitedlands.commands.handlers.admin.country.settlement;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.admin.country.CmdAdminCountry;

@UnitedSubCommand(
        parent = CmdAdminCountry.class,
        name = "settlement",
        description = "Admin country settlement commands",
        usage = "/ula country settlement <add|remove> <settlement_name>>"
)
public class CmdAdminCountrySettlement implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }

}
