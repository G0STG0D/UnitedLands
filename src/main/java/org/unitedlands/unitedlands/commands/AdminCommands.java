package org.unitedlands.unitedlands.commands;

import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.commands.handlers.admin.AdminMapCommand;
import org.unitedlands.unitedlands.commands.handlers.admin.ReloadCommand;
import org.unitedlands.unitedlands.commands.handlers.admin.TestImportCommand;
import org.unitedlands.unitedlands.commands.handlers.admin.country.AdminCountrySubcommands;
import org.unitedlands.unitedlands.commands.handlers.admin.settlement.AdminSettlementSubcommands;
import org.unitedlands.unitedlands.commands.handlers.admin.towny.AdminTownySubcommands;

public class AdminCommands extends BaseCommandExecutor<UnitedLands> {

    public AdminCommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {

        handlers.put("testimport", new TestImportCommand(plugin, messageProvider));

        var settlementSubCommands = new AdminSettlementSubcommands(plugin, messageProvider);
        handlers.put("s", settlementSubCommands);
        handlers.put("settlement", settlementSubCommands);

        var countrySubCommands = new AdminCountrySubcommands(plugin, messageProvider);
        handlers.put("c", countrySubCommands);
        handlers.put("country", countrySubCommands);

        handlers.put("reload", new ReloadCommand(plugin, messageProvider));
        handlers.put("map", new AdminMapCommand(plugin, messageProvider));

        if (plugin.getTownyProvider() != null) {
            handlers.put("towny", new AdminTownySubcommands(plugin, messageProvider));
        }
        
    }

}
