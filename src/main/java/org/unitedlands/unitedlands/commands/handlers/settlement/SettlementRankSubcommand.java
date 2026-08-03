package org.unitedlands.unitedlands.commands.handlers.settlement;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;

public class SettlementRankSubcommand extends BaseSubcommandHandler<UnitedLands> {

    public SettlementRankSubcommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("add", new SettlementAddRankCommand(plugin, messageProvider));
        subHandlers.put("remove", new SettlementRemoveRankCommand(plugin, messageProvider));
    }

}
