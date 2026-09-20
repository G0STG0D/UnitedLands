package org.unitedlands.unitedlands.utils;

import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.unitedlands.UnitedLib;
import org.unitedlands.services.UnitedLanguageService;
import org.unitedlands.utils.United;

public class UnitedLanguageServiceImplementation implements UnitedLanguageService {

    private final String LOCALE_KEY = "ul-locale";

    @Override
    public Locale getLocale(UUID playerId) {

        var player = Bukkit.getPlayer(playerId);

        if (player != null && player.getLastLogin() != 0) {

            United.logger().debug("getLocale");
            var pdc = player.getPersistentDataContainer();
            var languegString = pdc.get(getKey(), PersistentDataType.STRING);

            United.logger().debug(languegString);
            
            if (languegString != null) {
                try {
                    return Locale.of(languegString);
                } catch (Exception ignore) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void setLocale(UUID playerId, Locale locale) {
        var player = Bukkit.getPlayer(playerId);
        if (player != null && player.getLastLogin() != 0) {

            United.logger().debug("setLocale");

            var pdc = player.getPersistentDataContainer();
            pdc.set(getKey(), PersistentDataType.STRING, String.valueOf(locale.getLanguage()));
        }
    }

    private NamespacedKey getKey() {
        return new NamespacedKey(UnitedLib.getInstance(), LOCALE_KEY);
    }

}
