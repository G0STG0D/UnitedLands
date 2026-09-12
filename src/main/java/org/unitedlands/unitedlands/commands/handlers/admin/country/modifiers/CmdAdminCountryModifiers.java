package org.unitedlands.unitedlands.commands.handlers.admin.country.modifiers;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.admin.country.CmdAdminCountry;

@UnitedSubCommand(
        parent = CmdAdminCountry.class,
        name = "modifiers",
        description = "Admin country modifier commands",
        usage = "/ula country modifiers <command> <argument>"
)
public class CmdAdminCountryModifiers implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
