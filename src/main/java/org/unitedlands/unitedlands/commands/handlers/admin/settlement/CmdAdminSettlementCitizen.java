package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedSubCommand(
        parent = CmdAdminSettlement.class,
        name = "citizen",
        description = "Admin settlement citizen management commands",
        usage = "/ula settlement citizen <add|remove> <settlement_name> <player>"
)
public class CmdAdminSettlementCitizen implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
