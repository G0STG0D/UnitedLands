package org.unitedlands.unitedlands.commands.handlers.citizen;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.infoscreen.CitizenInfoScreen;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;

@UnitedSubCommand(
    parent          = CmdCitizen.class,
    name            = "info",
    description     = "Shows information about a citizen",
    usage           = "/citizen info <citizen_name>",
    playerOnly      = true
)
public class CmdCitizenInfo implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return UnitedLandsDataManager.instance().getCitizenNames();
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        Citizen citizen = null;
        if (args.length == 0) {
            citizen = UnitedLandsDataManager.instance().getCitizen((Player) sender);
            if (citizen == null)
                return;
        } else if (args.length >= 1) {
            citizen = UnitedLandsDataManager.instance().getCitizen(args[0]);
            if (citizen == null) {
                return;
            }
        }

        var screen = new CitizenInfoScreen(citizen);
        screen.send(sender);
    }

}
