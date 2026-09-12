package org.unitedlands.unitedlands.commands.handlers.admin.settlement.attributes;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.commands.handlers.admin.settlement.CmdAdminSettlement;

@UnitedSubCommand(
        parent = CmdAdminSettlement.class,
        name = "attributes",
        description = "Admin settlement attribute commands",
        usage = "/ula settlement attributes <command> <argument>"
)
public class CmdAdminSettlementAttributes implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) { }

}
