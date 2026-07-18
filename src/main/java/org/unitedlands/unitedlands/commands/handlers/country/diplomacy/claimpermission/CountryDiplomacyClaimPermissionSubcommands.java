package org.unitedlands.unitedlands.commands.handlers.country.diplomacy.claimpermission;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;

public class CountryDiplomacyClaimPermissionSubcommands extends BaseSubcommandHandler<UnitedLands> {

    public CountryDiplomacyClaimPermissionSubcommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("add", new CountryDiplomacyAddClaimPermissionCommand(plugin, messageProvider));
        subHandlers.put("remove", new CountryDiplomacyRemoveClaimPermissionCommand(plugin, messageProvider));
    }

}
