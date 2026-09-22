package org.unitedlands.unitedlands.commands.handlers.admin.country.rank;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.admin.country.CmdAdminCountry;

@UnitedSubCommand(
        parent = CmdAdminCountry.class,
        name = "rank",
        description = "Admin country rank commands",
        usage = "/ula country rank <add|remove> <player> <rank>"
)
public class CmdAdminCountryRank implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }

}
