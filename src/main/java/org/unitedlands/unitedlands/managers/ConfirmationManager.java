package org.unitedlands.unitedlands.managers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

public class ConfirmationManager {

    private static ConfirmationManager instance;
    public static ConfirmationManager instance() {
        return instance;
    }

    private final UnitedLands plugin;

    private Set<Confirmation> confirmations = new HashSet<>();

    public ConfirmationManager(UnitedLands plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public void queueConfirmation(Confirmation confirmation) {

        var key = confirmation.getKey();
        if (key == null || key.isBlank() || key.isEmpty()) {
            Logger.logError("Cannot process confirmation without key.", "UnitedLands");
            return;
        }

        confirmations.add(confirmation);

        confirmation.setExpirationTask(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (confirmation.getSender() != null)
                Messenger.sendMessage(confirmation.getSender(), "<yellow>Your request has expired.</yellow>");
            removeConfirmation(confirmation);
        }, confirmation.getTimeoutSeconds() * 20));

    }

    public Confirmation getConfirmation(String key, Player sender, Player receiver) {
        return confirmations.stream()
                .filter(c -> c.getKey().equals(key) && c.getSender().equals(sender) && c.getReceiver().equals(receiver))
                .findFirst()
                .orElse(null);
    }

    public List<Confirmation> getReceiverConfirmations(Player receiver) {
        return confirmations.stream()
                .filter(c -> c.getReceiver().equals(receiver))
                .collect(Collectors.toList());
    }

    public List<Confirmation> getReceiverConfirmations(String key, Player receiver) {
        return confirmations.stream()
                .filter(c -> c.getKey().equals(key) && c.getReceiver().equals(receiver))
                .collect(Collectors.toList());
    }

    public void executeConfirmation(Confirmation confirmation) {
        var key = confirmation.getKey();
        if (key == null || key.isBlank() || key.isEmpty()) {
            Logger.logError("Cannot process confirmation without key.", "UnitedLands");
            return;
        }

        confirmation.getRunnable().run();
        confirmation.getExpirationTask().cancel();

        removeConfirmation(confirmation);
    }

    public void removeConfirmation(Confirmation confirmation) {
        if (confirmation == null)
            return;
        confirmations.remove(confirmation);
    }

}
