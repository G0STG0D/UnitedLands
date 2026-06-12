package org.unitedlands.unitedlands.classes;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.unitedlands.unitedlands.managers.ConfirmationManager;
import org.unitedlands.utils.Messenger;

public class Confirmation {

    private Runnable runnable;
    private String title;
    private Map<String, String> replacements;
    private String key;
    private String acceptCommand;
    private String cancelCommand;
    private int timeoutSeconds = 30;
    private Player sender;
    private Player receiver;
    private String discriminator;

    private BukkitTask expirationTask;

    public Confirmation(String key) {
        this.key = key;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public Confirmation setRunnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Confirmation setTitle(String title) {
        this.title = title;
        return this;
    }

    public Map<String, String> getReplacements() {
        return replacements;
    }

    public Confirmation setReplacements(Map<String, String> replacements) {
        this.replacements = replacements;
        return this;
    }

    public String getKey() {
        return key;
    }

    public String getAcceptCommand() {
        return acceptCommand;
    }

    public Confirmation setAcceptCommand(String acceptCommand) {
        this.acceptCommand = acceptCommand;
        return this;
    }

    public String getCancelCommand() {
        return cancelCommand;
    }

    public Confirmation setCancelCommand(String cancelCommand) {
        this.cancelCommand = cancelCommand;
        return this;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public Confirmation setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        return this;
    }

    public Player getSender() {
        return sender;
    }

    public Confirmation setSender(Player sender) {
        this.sender = sender;
        return this;
    }

    public Player getReceiver() {
        return receiver;
    }

    public Confirmation setReceiver(Player receiver) {
        this.receiver = receiver;
        return this;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public Confirmation setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
        return this;
    }

    public BukkitTask getExpirationTask() {
        return expirationTask;
    }

    public void setExpirationTask(BukkitTask expirationTask) {
        this.expirationTask = expirationTask;
    }

    public void send() {
        if (sender == null || receiver == null)
            return;

        // TODO: move strings to config
        if (!sender.equals(receiver))
            Messenger.sendMessage(sender, "Your request has been sent.");

        Messenger.sendMessage(receiver,
                "<aqua>" + getTitle() + " [<yellow>" + getAcceptCommand() + "</yellow>]</aqua>", replacements);

        ConfirmationManager.instance().queueConfirmation(this);
    }

    public void notifyExpiration() {
        if (sender == null)
            return;
        Messenger.sendMessage(sender, "Confirmation expired");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((sender == null) ? 0 : sender.hashCode());
        result = prime * result + ((receiver == null) ? 0 : receiver.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Confirmation other = (Confirmation) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (sender == null) {
            if (other.sender != null)
                return false;
        } else if (!sender.equals(other.sender))
            return false;
        if (receiver == null) {
            if (other.receiver != null)
                return false;
        } else if (!receiver.equals(other.receiver))
            return false;
        return true;
    }

}
