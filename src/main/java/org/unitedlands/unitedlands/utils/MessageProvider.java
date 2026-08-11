package org.unitedlands.unitedlands.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.unitedlands.classes.AbstractConfigMessageProvider;

public class MessageProvider extends AbstractConfigMessageProvider {
    
    private static MessageProvider instance;
    public static MessageProvider instance() {
        return instance;
    }

    public MessageProvider(FileConfiguration cfg) {
         super(cfg);
         instance = this;
    }
}
