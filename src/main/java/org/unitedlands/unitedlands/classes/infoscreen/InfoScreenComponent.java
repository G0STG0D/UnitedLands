package org.unitedlands.unitedlands.classes.infoscreen;

import net.kyori.adventure.text.Component;

public class InfoScreenComponent {
    private String id;
    private Component content;

    public InfoScreenComponent(String id, Component content) {
        this.id = id;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Component getContent() {
        return content;
    }

    public void setContent(Component content) {
        this.content = content;
    }

}
