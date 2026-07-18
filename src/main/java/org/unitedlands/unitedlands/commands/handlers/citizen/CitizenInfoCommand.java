package org.unitedlands.unitedlands.commands.handlers.citizen;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.infoscreen.CitizenInfoScreen;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class CitizenInfoCommand extends BaseCommandHandler<UnitedLands> {

    public CitizenInfoCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

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

        var screen = new CitizenInfoScreen(plugin, messageProvider, citizen);
        if (screen.getComponents().size() > 0) {
            for (var component : screen.getComponents()) {
                Messenger.send(sender, component.getContent());
            }
        }
    }

}
