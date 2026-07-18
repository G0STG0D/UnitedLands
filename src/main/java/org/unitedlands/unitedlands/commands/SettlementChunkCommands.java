package org.unitedlands.unitedlands.commands;

import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.commands.handlers.settlementchunk.SettlementChunkAbandonCommand;
import org.unitedlands.unitedlands.commands.handlers.settlementchunk.SettlementChunkBuyCommand;
import org.unitedlands.unitedlands.commands.handlers.settlementchunk.SettlementChunkEvictCommand;
import org.unitedlands.unitedlands.commands.handlers.settlementchunk.SettlementChunkForSaleCommand;
import org.unitedlands.unitedlands.commands.handlers.settlementchunk.SettlementChunkInfoCommand;
import org.unitedlands.unitedlands.commands.handlers.settlementchunk.SettlementChunkPermissionCommand;
import org.unitedlands.unitedlands.commands.handlers.settlementchunk.SettlementChunkSetType;
import org.unitedlands.unitedlands.commands.handlers.settlementchunk.SettlementChunkToggleCommand;

public class SettlementChunkCommands extends BaseCommandExecutor<UnitedLands> {

    public SettlementChunkCommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("forsale", new SettlementChunkForSaleCommand(plugin, messageProvider));
        handlers.put("buy", new SettlementChunkBuyCommand(plugin, messageProvider));
        handlers.put("evict", new SettlementChunkEvictCommand(plugin, messageProvider));
        handlers.put("abandon", new SettlementChunkAbandonCommand(plugin, messageProvider));
        handlers.put("info", new SettlementChunkInfoCommand(plugin, messageProvider));
        handlers.put("permission", new SettlementChunkPermissionCommand(plugin, messageProvider));
        handlers.put("toggle", new SettlementChunkToggleCommand(plugin, messageProvider));
        handlers.put("settype", new SettlementChunkSetType(plugin, messageProvider));
    }

}
