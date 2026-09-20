package org.unitedlands.unitedlands.commands.handlers;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.utils.United;

@UnitedCommand(
        name = "language",
        aliases = {},
        description = "Changed the UL plugin display language",
        usage = "/language <language>",
        playerOnly = true
)
public class CmdLanguage implements UnitedCommandExecutor {

    Map<String, String> locales = Map.of(
            "Deutsch", "de",
            "English", "en",
            "Español", "es",
            "Français", "fr",
            "日本語", "ja");

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return locales.keySet().stream().toList();
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        if (!locales.keySet().contains(args[0])) {
            United.messenger().sendRaw(sender, "Unknown language: " + args[0]);
            return;
        }

        var player = (Player) sender;

        try {
            var locale = Locale.of(locales.get(args[0]));
            UnitedLands.instance().getLanguageProvider().setLocale(player.getUniqueId(), locale);
            United.messenger().sendRaw(player, "<aqua>Switching to language: " + args[0] + "</aqua>");
        } catch (Exception ex) {
            United.messenger().sendRaw(player, "<red>Error: " + ex.getMessage() + "</red>");
        }

    }

}
