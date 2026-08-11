package org.unitedlands.unitedlands.commands.handlers.region;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.commandhandlers.RegionCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
    parent          = CmdRegionSet.class,
    name            = "administrator",
    description     = "Sets the administratator of a region",
    usage           = "/region set administrator <citizen_name>",
    playerOnly      = true
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
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.GENERAL_ERRORS__CITIZEN_NOT_FOUND.path()),
                    Map.of("citizen", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        context.region().setAdministrator(targetCitizen);
        UnitedLandsDataManager.instance().updateRegionDbData(context.region());
        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__REGION__SETADMINISTRATOR__SUCCESS.path()),
                Map.of("citizen", args[0], "region", context.region().getName()), MessageProvider.instance().get(Message.PREFIX.path()));

    }

}
