package org.unitedlands.unitedlands.classes.metadata;

public class BooleanMetaDataField extends MetaDataField<Boolean> {

    public BooleanMetaDataField(String key) {
        super(key);
        this.dataType = "BOOLEAN";
    }

    public BooleanMetaDataField(String key, Boolean value) {
        super(key, value);
        this.dataType = "BOOLEAN";
    }

    public BooleanMetaDataField(String key, Boolean value, String label) {
        super(key, value, label);
        this.dataType = "BOOLEAN";
    }

    public BooleanMetaDataField(String key, Boolean value, String label, boolean showInScreens) {
        super(key, value, label, showInScreens);
        this.dataType = "BOOLEAN";
    }

}
