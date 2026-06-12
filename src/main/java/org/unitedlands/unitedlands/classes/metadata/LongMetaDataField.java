package org.unitedlands.unitedlands.classes.metadata;

public class LongMetaDataField extends MetaDataField<Long> {

    public LongMetaDataField(String key) {
        super(key);
        this.dataType = "LONG";
    }

    public LongMetaDataField(String key, Long value) {
        super(key, value);
        this.dataType = "LONG";
    }

    public LongMetaDataField(String key, Long value, String label) {
        super(key, value, label);
        this.dataType = "LONG";
    }

    public LongMetaDataField(String key, Long value, String label, boolean showInScreens) {
        super(key, value, label, showInScreens);
        this.dataType = "LONG";
    }

}
