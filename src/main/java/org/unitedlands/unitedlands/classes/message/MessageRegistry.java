package org.unitedlands.unitedlands.classes.message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.interfaces.MessageKey;
import org.unitedlands.utils.United;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class MessageRegistry {

    private String filePath;
    private File file;
    private YamlConfiguration config;

    public MessageRegistry(UnitedLands plugin, String filePath) {
        this.filePath = filePath;
        this.file = new File(plugin.getDataFolder(), filePath);
        if (!file.exists()) {
            plugin.saveResource(filePath, false);
        }

        this.config = new YamlConfiguration();
        try {
            this.config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public <E extends Enum<E> & MessageKey> void sync(Class<E> enumClass) {
        boolean changed = false;
        for (MessageKey key : (MessageKey[]) enumClass.getEnumConstants()) {
            if (!config.isSet(key.path())) {
                config.set(key.path(), key.defaultValue());
                changed = true;
            }
        }
        reportOrphans(enumClass);
        if (changed) {
            try {
                saveSorted();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private <E extends Enum<E> & MessageKey> void reportOrphans(Class<E> enumClass) {
        Set<String> valid = Arrays.stream((MessageKey[]) enumClass.getEnumConstants())
                .map(MessageKey::path)
                .collect(Collectors.toSet());
        Set<String> present = new HashSet<>();
        collectLeafPaths(config, "", present);
        present.removeAll(valid);
        present.forEach(orphan -> United.logger().warning("Orphaned message key (no enum matches it): " + orphan));
    }

    private void collectLeafPaths(ConfigurationSection section, String prefix, Set<String> out) {
        for (String key : section.getKeys(false)) {
            String full = prefix.isEmpty() ? key : prefix + "." + key;
            if (section.isConfigurationSection(key)) {
                collectLeafPaths(section.getConfigurationSection(key), full, out);
            } else {
                out.add(full);
            }
        }
    }

    private void saveSorted() throws IOException {
        Map<String, Object> sorted = toSortedMap(config);
        DumperOptions opts = new DumperOptions();
        opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        String yaml = new Yaml(opts).dump(sorted);
        Files.writeString(file.toPath(), yaml);
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> toSortedMap(ConfigurationSection section) {
        Map<String, Object> sorted = new TreeMap<>(); // natural String order = alphabetical
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            sorted.put(key, value instanceof ConfigurationSection sub ? toSortedMap(sub) : value);
        }
        return sorted;
    }

    public String getRaw(MessageKey key) {
        return config.getString(key.path(), key.defaultValue());
    }

    public String getFilePath() {
        return filePath;
    }
}