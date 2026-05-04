package org.unitedlands.unitedlands.commands;

import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.commands.handlers.country.CountryClaimCommand;
import org.unitedlands.unitedlands.commands.handlers.country.CountryCreateCommand;
import org.unitedlands.unitedlands.commands.handlers.country.CountryDeleteCommand;
import org.unitedlands.unitedlands.commands.handlers.country.CountrySetColorCommand;

public class CountryCommands extends BaseCommandExecutor<UnitedLands> {

    public CountryCommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("create", new CountryCreateCommand(plugin, messageProvider));
        handlers.put("claim", new CountryClaimCommand(plugin, messageProvider));
        handlers.put("setcolor", new CountrySetColorCommand(plugin, messageProvider));
        handlers.put("delete", new CountryDeleteCommand(plugin, messageProvider));
    }


    
}
