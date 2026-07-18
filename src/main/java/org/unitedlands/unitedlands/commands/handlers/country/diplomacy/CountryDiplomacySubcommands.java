package org.unitedlands.unitedlands.commands.handlers.country.diplomacy;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.commands.handlers.country.diplomacy.alliance.CountryDiplomacyAllySubcommands;
import org.unitedlands.unitedlands.commands.handlers.country.diplomacy.claimpermission.CountryDiplomacyClaimPermissionSubcommands;
import org.unitedlands.unitedlands.commands.handlers.country.diplomacy.release.CountryDiplomacyReleaseCommand;

public class CountryDiplomacySubcommands extends BaseSubcommandHandler<UnitedLands> {

    public CountryDiplomacySubcommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("ally", new CountryDiplomacyAllySubcommands(plugin, messageProvider));
        subHandlers.put("claimpermission", new CountryDiplomacyClaimPermissionSubcommands(plugin, messageProvider));
        subHandlers.put("release", new CountryDiplomacyReleaseCommand(plugin, messageProvider));
        subHandlers.put("info", new CountryDiplomacyInfoCommand(plugin, messageProvider));
    }

}
