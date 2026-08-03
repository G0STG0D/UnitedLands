package org.unitedlands.unitedlands.commands;

import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.commands.handlers.region.RegionInfoCommand;
import org.unitedlands.unitedlands.commands.handlers.region.RegionPermissionCommand;
import org.unitedlands.unitedlands.commands.handlers.region.RegionSetAdministratorCommand;
import org.unitedlands.unitedlands.commands.handlers.region.RegionSetNameCommand;
import org.unitedlands.unitedlands.commands.handlers.region.RegionToggleCommand;

public class RegionCommands extends BaseCommandExecutor<UnitedLands> {

    public RegionCommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("info", new RegionInfoCommand(plugin, messageProvider));
        handlers.put("setadministrator", new RegionSetAdministratorCommand(plugin, messageProvider));
        handlers.put("setname", new RegionSetNameCommand(plugin, messageProvider));
        handlers.put("toggle", new RegionToggleCommand(plugin, messageProvider));
        handlers.put("permissions", new RegionPermissionCommand(plugin, messageProvider));
    }


    
}
