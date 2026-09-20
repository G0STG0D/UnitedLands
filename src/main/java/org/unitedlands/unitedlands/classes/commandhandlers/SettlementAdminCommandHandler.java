package org.unitedlands.unitedlands.classes.commandhandlers;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Settlement;

import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

public class SettlementAdminCommandHandler implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender arg0, String[] arg1) {

    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] arg1) {
        return null;
    }

    protected Settlement getSettlement(CommandSender player, String name) {
        var settlement = UnitedLandsDataManager.instance().getSettlement(name);
        if (settlement == null) {
            United.messenger().send(player, "general-errors.settlement-not-found", name);
            return null;
        }
        return settlement;
    }

    protected Citizen getCitizen(CommandSender player, String name) {
        var citizen = UnitedLandsDataManager.instance().getCitizen(name);
        if (citizen == null) {
            United.messenger().send(player, "general-errors.citizen-not-found", name);
            return null;
        }
        return citizen;
    }

}
