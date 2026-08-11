package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedSubCommand(
        parent = CmdAdminSettlement.class,
        name = "rank",
        description = "Admin settlement rank management commands",
        usage = "/ula settlement rank <add|remove> <settlement_name> <player> <rank>"
)
public class CmdAdminSettlementRank implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }

}
