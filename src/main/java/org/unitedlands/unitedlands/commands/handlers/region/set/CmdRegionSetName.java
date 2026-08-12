package org.unitedlands.unitedlands.commands.handlers.region.set;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.RegionCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
    parent          = CmdRegionSet.class,
    name            = "name",
    description     = "Set the name of a region",
    usage           = "/region set name <new_name>",
    playerOnly      = true
)
public class CmdRegionSetName extends RegionCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "region.setname");
        if (context == null)
            return;

        var oldname = context.region().getName();

        context.region().setName(args[0]);
        UnitedLandsDataManager.instance().updateRegionDbData(context.region());

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__REGION__SET_SETNAME__SUCCESS.path()),
                Map.of("oldname", oldname, "newname", context.region().getName()), MessageProvider.instance().get(Message.PREFIX.path()));

    }

}
