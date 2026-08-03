package org.unitedlands.unitedlands.commands.handlers.country;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;

public class CountryRankSubcommands extends BaseSubcommandHandler<UnitedLands> {

    public CountryRankSubcommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("add",new CountryAddRankCommand(plugin, messageProvider));
        subHandlers.put("remove",new CountryRemoveRankCommand(plugin, messageProvider));
    }

}
