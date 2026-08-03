package org.unitedlands.unitedlands.commands.handlers.region;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.RegionCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class RegionSetNameCommand extends RegionCommandHandler {

    public RegionSetNameCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "region.setname");
        if (context == null)
            return;

        if (args.length != 1) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__REGION__SETNAME__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var oldname = context.region().getName();

        context.region().setName(args[0]);
        UnitedLandsDataManager.instance().updateRegionDbData(context.region());

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__REGION__SET_SETNAME__SUCCESS.path()),
                Map.of("oldname", oldname, "newname", context.region().getName()), messageProvider.get(Message.PREFIX.path()));

    }

}
