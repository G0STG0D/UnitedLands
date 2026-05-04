package org.unitedlands.unitedlands.commands;

import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.commands.handlers.admin.AdminMapCommand;
import org.unitedlands.unitedlands.commands.handlers.admin.ReloadCommand;
import org.unitedlands.unitedlands.commands.handlers.admin.TestImportCommand;
import org.unitedlands.unitedlands.commands.handlers.admin.TestOutputCommand;

public class AdminCommands extends BaseCommandExecutor<UnitedLands> {

    public AdminCommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("testoutput", new TestOutputCommand(plugin, messageProvider));
        handlers.put("testimport", new TestImportCommand(plugin, messageProvider));
        handlers.put("reload", new ReloadCommand(plugin, messageProvider));
        handlers.put("map", new AdminMapCommand(plugin, messageProvider));
    }


    
}
