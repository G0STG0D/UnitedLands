package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.classes.infoscreen.SettlementChunkInfoScreen;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementChunkInfoCommand extends SettlementChunkCommandHandler {

    public SettlementChunkInfoCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return GlobalDataManager.instance().getSettlementNames();
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;
        var settlementChunk = getSettlementChunk(player);
        if (settlementChunk == null)
            return;
        
        var screen = new SettlementChunkInfoScreen(plugin, messageProvider, settlementChunk);
        if (screen.getComponents().size() > 0) {
            for (var component : screen.getComponents()) {
                Messenger.send(player, component.getContent());
            }
        }
    }
}
