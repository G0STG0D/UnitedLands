package org.unitedlands.unitedlands.commands.handlers.country.diplomacy.alliance;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;

public class CountryDiplomacyAllySubcommands extends BaseSubcommandHandler<UnitedLands> {

    public CountryDiplomacyAllySubcommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("add", new CountryDiplomacyAddAllyCommand(plugin, messageProvider));
        subHandlers.put("remove", new CountryDiplomacyRemoveAllyCommand(plugin, messageProvider));
    }

}
