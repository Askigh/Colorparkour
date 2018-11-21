package net.starype.colorparkour.entity.player.gui;

import com.simsilica.lemur.Panel;

public class ColorParkourGUI {

    private boolean active;
    private boolean inputActions = false;
    private Panel gui;
    private final String name;

    public ColorParkourGUI(String name, Panel gui) {
        this.gui = gui;
        this.name = name;
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean hasCursorActions() { return inputActions; }
    public ColorParkourGUI withInputActions(boolean inputActions) {
        this.inputActions = inputActions;
        return this;
    }

    public Panel getGUI() { return gui; }
    public String getName() { return name; }
}
