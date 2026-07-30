package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;

public class AdminSettlementSubcommands extends BaseSubcommandHandler<UnitedLands> {

    public AdminSettlementSubcommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new AdminSettlementCreateCommand(plugin, messageProvider));
        subHandlers.put("claim", new AdminSettlementClaimCommand(plugin, messageProvider));
        subHandlers.put("unclaim", new AdminSettlementUnclaimCommand(plugin, messageProvider));
        subHandlers.put("addcitizen", new AdminSettlementAddCitizenCommand(plugin, messageProvider));
        subHandlers.put("removecitizen", new AdminSettlementRemoveCitizenCommand(plugin, messageProvider));
        subHandlers.put("setspawn", new AdminSettlementSetSpawnCommand(plugin, messageProvider));
        subHandlers.put("delete", new AdminSettlementDeleteCommand(plugin, messageProvider));
        subHandlers.put("addrank", new AdminSettlementRankAddCommand(plugin, messageProvider));
        subHandlers.put("removerank", new AdminSettlementRankRemoveCommand(plugin, messageProvider));
    }

}
