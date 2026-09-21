package org.unitedlands.unitedlands.commands.handlers.region.set;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.commandhandlers.RegionCommandHandler;

import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdRegionSet.class,
        name = "administrator",
        description = "Sets the administratator of a region",
        usage = "/region set administrator <citizen_name>",
        playerOnly = true
)
public class CmdRegionSetAdministrator extends RegionCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            var citizen = UnitedLandsDataManager.instance().getCitizen((Player) sender);
            if (citizen != null && citizen.getCountry() != null) {
                return citizen.getCountry().getCitizens().stream().map(Citizen::getName).toList();
            }
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "region.setadministrator");
        if (context == null)
            return;

        var targetCitizen = UnitedLandsDataManager.instance().getCitizen(args[0]);
        if (targetCitizen == null) {
            United.messenger().send(context.player(), "general-errors.citizen-not-found", args[0]);
            return;
        }

        context.region().setAdministrator(targetCitizen);
        context.region().saveAndRender();

        United.messenger().send(context.player(), "player.region.setadministrator.success", args[0], context.region().getName());

    }

}
