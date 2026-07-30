package org.unitedlands.unitedlands.commands.handlers.admin.country;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;

public class AdminCountrySubcommands extends BaseSubcommandHandler<UnitedLands> {

    public AdminCountrySubcommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new AdminCountryCreateCommand(plugin, messageProvider));
        subHandlers.put("delete", new AdminCountryDeleteCommand(plugin, messageProvider));
        subHandlers.put("claim", new AdminCountryClaimCommand(plugin, messageProvider));
        subHandlers.put("setcolor", new AdminCountrySetColorCommand(plugin, messageProvider));
        subHandlers.put("addsettlement", new AdminCountryAddSettlementCommand(plugin, messageProvider));
        subHandlers.put("removesettlement", new AdminCountryRemoveSettlementCommand(plugin, messageProvider));
        subHandlers.put("setcapital", new AdminCountrySetCapitalCommand(plugin, messageProvider));
        subHandlers.put("addrank", new AdminCountryAddRankCommand(plugin, messageProvider));
        subHandlers.put("removerank", new AdminCountryRemoveRankCommand(plugin, messageProvider));
    }

}
