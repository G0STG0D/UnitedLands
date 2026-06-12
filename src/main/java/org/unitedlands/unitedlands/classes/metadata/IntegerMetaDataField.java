package org.unitedlands.unitedlands.classes.metadata;

public class IntegerMetaDataField extends MetaDataField<Integer> {

    public IntegerMetaDataField(String key) {
        super(key);
        this.dataType = "INTEGER";
    }

    public IntegerMetaDataField(String key, Integer value) {
        super(key, value);
        this.dataType = "INTEGER";
    }

    public IntegerMetaDataField(String key, Integer value, String label) {
        super(key, value, label);
        this.dataType = "INTEGER";
    }

    public IntegerMetaDataField(String key, Integer value, String label, boolean showInScreens) {
        super(key, value, label, showInScreens);
        this.dataType = "INTEGER";
    }



}
