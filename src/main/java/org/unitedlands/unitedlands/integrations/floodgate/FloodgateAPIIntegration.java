package org.unitedlands.unitedlands.integrations.floodgate;

import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.managers.ConfirmationManager;

public class FloodgateAPIIntegration {

    private final FloodgateApi instance;

    public FloodgateAPIIntegration() {
        this.instance = FloodgateApi.getInstance();
    }

    public FloodgateApi getInstance() {
        return instance;
    }

    public boolean isBedrockPlayer(Player player) {
        return instance.isFloodgatePlayer(player.getUniqueId());
    }

    public void sendConfirmationPanel(Player player, Confirmation confirmation) {
        
        var content = confirmation.getFilledTitle();

        FloodgatePlayer floodgateplayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgateplayer.sendForm(
                SimpleForm.builder()
                        .title("Confirmation")
                        .content(content)
                        .button("Confirm")
                        .validResultHandler(response -> handleClick(player, confirmation))
                        .closedOrInvalidResultHandler(response -> handleCancel(player, confirmation)));
    }

    private void handleClick(Player player, Confirmation confirmation) {
        ConfirmationManager.instance().executeConfirmation(confirmation);
    }

    private void handleCancel(Player player, Confirmation confirmation) {
        ConfirmationManager.instance().rejectConfirmation(confirmation);
    }
}
