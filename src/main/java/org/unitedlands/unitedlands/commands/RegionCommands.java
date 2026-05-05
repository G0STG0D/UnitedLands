package org.unitedlands.unitedlands.commands;

import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.commands.handlers.region.RegionSetNameCommand;

public class RegionCommands extends BaseCommandExecutor<UnitedLands> {

    public RegionCommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("setname", new RegionSetNameCommand(plugin, messageProvider));
    }


    
}
