package org.unitedlands.unitedlands.classes.metadata;

public class FloatMetaDataField extends MetaDataField<Float> {

    public FloatMetaDataField(String key) {
        super(key);
        this.dataType = "FLOAT";
    }

    public FloatMetaDataField(String key, Float value) {
        super(key, value);
        this.dataType = "FLOAT";
    }

    public FloatMetaDataField(String key, Float value, String label) {
        super(key, value, label);
        this.dataType = "FLOAT";
    }

    public FloatMetaDataField(String key, Float value, String label, boolean showInScreens) {
        super(key, value, label, showInScreens);
        this.dataType = "FLOAT";
    }



}
