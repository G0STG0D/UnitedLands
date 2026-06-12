package org.unitedlands.unitedlands.classes.metadata;

public class StringMetaDataField extends MetaDataField<String> {

    public StringMetaDataField(String key) {
        super(key);
        this.dataType = "STRING";
    }

    public StringMetaDataField(String key, String value) {
        super(key, value);
        this.dataType = "STRING";
    }

    public StringMetaDataField(String key, String value, String label) {
        super(key, value, label);
        this.dataType = "STRING";
    }

    public StringMetaDataField(String key, String value, String label, boolean showInScreens) {
        super(key, value, label, showInScreens);
        this.dataType = "STRING";
    }


}
