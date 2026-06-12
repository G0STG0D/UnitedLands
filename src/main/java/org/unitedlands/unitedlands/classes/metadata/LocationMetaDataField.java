package org.unitedlands.unitedlands.classes.metadata;

import org.bukkit.Location;

public class LocationMetaDataField extends MetaDataField<Location> {

    public LocationMetaDataField(String key) {
        super(key);
        this.dataType = "LOCATION";
    }

    public LocationMetaDataField(String key, Location value) {
        super(key, value);
        this.dataType = "LOCATION";
    }

    public LocationMetaDataField(String key, Location value, String label) {
        super(key, value, label);
        this.dataType = "LOCATION";
    }

    public LocationMetaDataField(String key, Location value, String label, boolean showInScreens) {
        super(key, value, label, showInScreens);
        this.dataType = "LOCATION";
    }



}
