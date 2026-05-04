package org.unitedlands.unitedlands.commands;

import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.commands.handlers.regionchunk.RegionChunkTransferCommand;

public class RegionChunkCommands extends BaseCommandExecutor<UnitedLands> {

    public RegionChunkCommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("transfer", new RegionChunkTransferCommand(plugin, messageProvider));
    }


    
}
