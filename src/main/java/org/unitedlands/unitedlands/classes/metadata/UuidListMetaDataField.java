package org.unitedlands.unitedlands.classes.metadata;

import java.util.List;
import java.util.UUID;

public class UuidListMetaDataField extends MetaDataField<List<UUID>> {

    public UuidListMetaDataField(String key) {
        super(key);
        this.dataType = "UUIDLIST";
    }

    public UuidListMetaDataField(String key, List<UUID> value) {
        super(key, value);
        this.dataType = "UUIDLIST";
    }

    public UuidListMetaDataField(String key, List<UUID> value, String label) {
        super(key, value, label);
        this.dataType = "UUIDLIST";
    }

    public UuidListMetaDataField(String key, List<UUID> value, String label, boolean showInScreens) {
        super(key, value, label, showInScreens);
        this.dataType = "UUIDLIST";
    }



}
