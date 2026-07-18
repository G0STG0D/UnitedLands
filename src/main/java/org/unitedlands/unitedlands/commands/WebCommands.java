package org.unitedlands.unitedlands.commands;

import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.commands.handlers.web.WebLoginCommandHandler;

public class WebCommands extends BaseCommandExecutor<UnitedLands> {

    public WebCommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("login", new WebLoginCommandHandler(plugin, messageProvider));
    }

}
