package org.unitedlands.unitedlands.commands.handlers.admin.towny;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;

public class AdminTownySubcommands extends BaseSubcommandHandler<UnitedLands> {

    public AdminTownySubcommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("import", new AdminTownyImportCommand(plugin, messageProvider));
    }

}
