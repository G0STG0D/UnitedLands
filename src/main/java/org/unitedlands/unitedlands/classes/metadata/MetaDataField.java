package org.unitedlands.unitedlands.classes.metadata;

public class MetaDataField<T> {

    private T value;

    private final String key;
    protected String dataType;
    protected String label;
    protected boolean showInScreens;

    public MetaDataField(String key) {
        this.key = key;
    }

    public MetaDataField(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public MetaDataField(String key, T value, String label) {
        this.key = key;
        this.value = value;
        this.label = label;
    }

    public MetaDataField(String key, T value, String label, boolean showInScreens) {
        this.key = key;
        this.value = value;
        this.label = label;
        this.showInScreens = showInScreens;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getDataType() {
        return dataType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean showInScreens() {
        return showInScreens;
    }

    public void setShowInScreens(boolean showInScreens) {
        this.showInScreens = showInScreens;
    }

    @Override
    public String toString() {
        return "{ key:" + key + ", value: " + value + ", label: " + label + ", showInScreens: " + showInScreens + "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MetaDataField<?> other = (MetaDataField<?>) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

}
