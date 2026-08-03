package org.unitedlands.unitedlands.commands.handlers.region;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.commandhandlers.RegionCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class RegionSetAdministratorCommand extends RegionCommandHandler {

    public RegionSetAdministratorCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

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

        if (args.length != 1) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__REGION__SETADMINISTRATOR__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var targetCitizen = UnitedLandsDataManager.instance().getCitizen(args[0]);
        if (targetCitizen == null) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.GENERAL_ERRORS__CITIZEN_NOT_FOUND.path()),
                    Map.of("citizen", args[0]), messageProvider.get(Message.PREFIX.path()));
            return;
        }

        context.region().setAdministrator(targetCitizen);
        UnitedLandsDataManager.instance().updateRegionDbData(context.region());
        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__REGION__SETADMINISTRATOR__SUCCESS.path()),
                Map.of("citizen", args[0], "region", context.region().getName()), messageProvider.get(Message.PREFIX.path()));

    }

}
