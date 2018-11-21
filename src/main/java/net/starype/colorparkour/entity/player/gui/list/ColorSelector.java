package net.starype.colorparkour.entity.player.gui.list;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.IconComponent;
import net.starype.colorparkour.entity.player.gui.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

import static net.starype.colorparkour.core.ColorParkourMain.WIDTH;

public class ColorSelector extends Container {

    private final IconComponent[] slots = loadIcons("assets/slots/",
            "red.png", "blue.png", "yellow.png", "green.png");
    private Container highlighted;
    private PlayerInventory inventory;
    private List<Node> copy;

    public ColorSelector(PlayerInventory inventory) {
        this.inventory = inventory;
        loadButtons(inventory);
    }

    private void loadButtons(PlayerInventory inventory) {
        createInventoryBar( "red", "blue", "yellow", "green");
        copy = new ArrayList<>(getLayout().getChildren());
        detachChildAt(0);
        setPreferredSize(new Vector3f(500, 300, 0));
        setLocalTranslation(WIDTH - 90, 340, 0);
        highlighted = new Container();
        highlighted.setLocalTranslation(WIDTH - 200, 290, 0);
        highlighted.setPreferredSize(new Vector3f(300, 200, 0));
        highlighted.addChild(copy.get(0));
    }

    private void createInventoryBar(String... names) {

        int index = 0;
        for (String name : names) {
            ColorIcon label = new ColorIcon(name);
            label.setIcon(slots[index]);
            label.setPreferredSize(new Vector3f(100, 100, 0));
            addChild(label);
            index++;
        }
    }

    private IconComponent[] loadIcons(String folder, String... paths) {
        IconComponent[] icons = new IconComponent[4];
        int index = 0;
        for(String path : paths) {
            IconComponent component = new IconComponent(folder + path);
            icons[index] = component;
            index++;
        }
        return icons;
    }
    public void highlight(String colorName, ColorRGBA color) {
        if(!inventory.getMainInstance().hasStarted())
            return;
        // Only add physics properties to platforms having the right color
        inventory.getMainInstance().getModuleManager().getCurrentModule().showOnly(color);
        highlighted.detachAllChildren();
        Label slot = new Label(null);
        switch (colorName) {
            case "red":
                slot.setIcon(slots[0].clone());
                break;
            case "blue":
                slot.setIcon(slots[1].clone());
                break;
            case "yellow":
                slot.setIcon(slots[2].clone());
                break;
            case "green":
                slot.setIcon(slots[3].clone());
                break;
        }
        highlighted.addChild(slot);
        resize(colorName);
    }
    private void resize(String except) {
        for(Node node : copy) {
            if(node instanceof ColorIcon) {
                ColorIcon colorIcon = (ColorIcon) node;
                if(colorIcon.getIconColor().equals(except)) {
                    detachChild(node);
                } else {
                    addChild(colorIcon);
                }
            }
        }
    }

    public Container getHighlighted() { return highlighted; }

    private static class ColorIcon extends Label {
        private String color;

        private ColorIcon(String color) {
            super(null);
            this.color = color;
        }

        private String getIconColor() {
            return color;
        }
    }
}
