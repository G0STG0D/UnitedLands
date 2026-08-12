package org.unitedlands.unitedlands.commands.handlers.admin.towny;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.admin.CmdAdmin;

@UnitedSubCommand(
        parent = CmdAdmin.class,
        name = "towns",
        description = "Admin towny commands",
        usage = "/ula towny <command>"
)
public class CmdAdminTowny implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
