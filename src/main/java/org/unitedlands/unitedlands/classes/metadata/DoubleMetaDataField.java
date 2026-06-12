package org.unitedlands.unitedlands.classes.metadata;

public class DoubleMetaDataField extends MetaDataField<Double> {

    public DoubleMetaDataField(String key) {
        super(key);
        this.dataType = "DOUBLE";
    }

    public DoubleMetaDataField(String key, Double value) {
        super(key, value);
        this.dataType = "DOUBLE";
    }

    public DoubleMetaDataField(String key, Double value, String label) {
        super(key, value, label);
        this.dataType = "DOUBLE";
    }

    public DoubleMetaDataField(String key, Double value, String label, boolean showInScreens) {
        super(key, value, label, showInScreens);
        this.dataType = "DOUBLE";
    }



}
