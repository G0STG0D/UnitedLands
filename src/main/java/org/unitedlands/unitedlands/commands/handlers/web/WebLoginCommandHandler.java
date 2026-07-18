package org.unitedlands.unitedlands.commands.handlers.web;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;

public class WebLoginCommandHandler extends BaseCommandHandler<UnitedLands> {

    public WebLoginCommandHandler(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length == 0)
            // TODO: usage
            return;

        var code = args[0];

        var loginChallenge = UnitedLandsDataManager.instance().getDatabaseManager().getLoginChallengeService()
                .getByCode(code);
        if (loginChallenge == null) {
            // TODO: Errors
            return;
        }

        if (System.currentTimeMillis() > loginChallenge.getExpiresAt()) {
            // TODO: Errors
            return;
        }

        Player player = (Player) sender;

        loginChallenge.setMcUUID(player.getUniqueId());
        loginChallenge.setMcUsername(player.getName());
        loginChallenge.setStatus("completed");

        UnitedLandsDataManager.instance().getDatabaseManager().getLoginChallengeService()
                .updateAsync(loginChallenge);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}
