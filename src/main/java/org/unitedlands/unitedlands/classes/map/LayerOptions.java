package org.unitedlands.unitedlands.classes.map;

public class LayerOptions {

    private final String name;
    private final boolean showControls;
    private final boolean defaultHidden;
    private final int layerPriority;
    private final int zIndex;

    public LayerOptions(String name, boolean showControls, boolean defaultHidden, int layerPriority, int zIndex) {
        this.name = name;
        this.showControls = showControls;
        this.defaultHidden = defaultHidden;
        this.layerPriority = layerPriority;
        this.zIndex = zIndex;
    }

    public String getName() {
        return name;
    }

    public boolean showControls() {
        return showControls;
    }

    public boolean isDefaultHidden() {
        return defaultHidden;
    }

    public int getLayerPriority() {
        return layerPriority;
    }

    public int getZIndex() {
        return zIndex;
    }
}