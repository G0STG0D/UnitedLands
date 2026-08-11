package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.admin.CmdAdmin;



@UnitedSubCommand(
        parent = CmdAdmin.class,
        name = "settlement",
        aliases = { "s" },
        description = "Admin settlement commands",
        usage = "/ula settlement <command>"
)

public class CmdAdminSettlement implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
