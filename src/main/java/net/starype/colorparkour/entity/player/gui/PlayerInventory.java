package net.starype.colorparkour.entity.player.gui;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.IconComponent;
import net.starype.colorparkour.core.ColorParkourMain;

import java.util.*;

import static net.starype.colorparkour.core.ColorParkourMain.*;

public class PlayerInventory {

    private ColorParkourMain main;
    private final List<ColorParkourGUI> GUIS = new ArrayList<>();
    private boolean changeFOV = true;
    private Container playerInventory;
    private Container highlighted;
    private List<Node> copy;
    private final IconComponent[] slots = loadIcons("assets/slots/",
            "red.png", "blue.png", "yellow.png", "green.png");

    public PlayerInventory(ColorParkourMain main) {
        this.main = main;
        loadMenus();
    }

    public void showOnly(int... guiIDS) {
        hideAll();
        for (Integer i : guiIDS)
            show(i);
    }

    public void show(int guiID) {
        ColorParkourGUI gui = GUIS.get(guiID);
        main.getGuiNode().attachChild(gui.getGUI());
        gui.setActive(true);
        if (gui.hasCursorActions()) {
            LOGGER.info("GUI needs cursor events");
            GuiGlobals.getInstance().setCursorEventsEnabled(true);
        }
    }

    public void hideAll() {
        for (ColorParkourGUI gui : GUIS) {
            if (gui.isActive()) {
                main.getGuiNode().detachChild(gui.getGUI());
                gui.setActive(false);
            }
        }
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
    }

    public void hide(int index) {
        if (GUIS.get(index).isActive()) {
            main.getGuiNode().detachChild(GUIS.get(index).getGUI());
            GUIS.get(index).setActive(false);
        } else LOGGER.error("GUI IS ALREADY DISABLED");

        if (GUIS.get(index).hasCursorActions()) {
            GuiGlobals.getInstance().setCursorEventsEnabled(false);
        }
    }

    private void loadMenus() {

        Container gameMenu = new Container();
        gameMenu.setLocalTranslation((WIDTH - 400) / 2, HEIGHT / 2 + 100, 0);
        GUIS.add(new ColorParkourGUI(gameMenu).withInputActions(true));

        Button play = gameMenu.addChild(new Button(null));
        play.setBackground(new IconComponent("assets/icons/play.png"));
        play.setColor(ColorRGBA.Orange);
        play.addClickCommands((Command<Button>) source -> {
            main.startGame();
            activatePlayer();
        });

        Container pauseMenu = new Container();
        pauseMenu.setLocalTranslation((WIDTH - 400) / 2, HEIGHT / 2 + 100, 0);

        Button quitButton = pauseMenu.addChild(new Button("Quit Game"));
        quitButton.setColor(ColorRGBA.Black);
        quitButton.addClickCommands((Command<Button>) source -> main.stop());
        quitButton.setLocalTranslation(0, 0, 0);

        Button restartLevel = pauseMenu.addChild(new Button("Restart level"));
        restartLevel.addClickCommands(source -> {
            main.getPlayer().resetPosition(main.getModuleManager().getCurrentModule());
            activatePlayer();
        });
        restartLevel.setColor(ColorRGBA.Black);
        restartLevel.setLocalTranslation(new Vector3f(50, 200, 0));

        Button changeFOV = pauseMenu.addChild(new Button("Enable FOV Change using arrows"));
        changeFOV.setColor(ColorRGBA.Green);
        changeFOV.addClickCommands(source -> {
            this.changeFOV = !this.changeFOV;
            changeFOV.setColor(changeFOV.getColor().equals(ColorRGBA.Black) ? ColorRGBA.Green : ColorRGBA.Black);
        });
        Button backToMenu = pauseMenu.addChild(new Button("Back to the menu"));
        backToMenu.setColor(ColorRGBA.Black);
        backToMenu.addClickCommands(source -> showOnly(0));

        GUIS.add(new ColorParkourGUI(pauseMenu).withInputActions(true));

        playerInventory = new Container();
        GUIS.add(new ColorParkourGUI(playerInventory));

        createInventoryBar(playerInventory, "red", "blue", "yellow", "green");
        copy = new ArrayList<>(playerInventory.getLayout().getChildren());
        playerInventory.detachChildAt(0);
        playerInventory.setPreferredSize(new Vector3f(500, 300, 0));
        playerInventory.setLocalTranslation(WIDTH - 90, 340, 0);
        highlighted = new Container();
        highlighted.setLocalTranslation(WIDTH - 200, 290, 0);
        highlighted.setPreferredSize(new Vector3f(300, 200, 0));
        highlighted.addChild(copy.get(0));
        GUIS.add(new ColorParkourGUI(highlighted));
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
    private void createInventoryBar(Container self, String... names) {

        int index = 0;
        for (String name : names) {
            ColorIcon label = new ColorIcon(name);
            label.setIcon(slots[index]);
            label.setPreferredSize(new Vector3f(100, 100, 0));
            self.addChild(label);
            index++;
        }
    }

    public void highlight(String colorName, ColorRGBA color) {
        // Only add physics properties to platforms having the right color
        main.getModuleManager().getCurrentModule().showOnly(color);
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
                    playerInventory.detachChild(node);
                } else {
                    playerInventory.addChild(colorIcon);
                }
            }
        }
    }

    public void activatePlayer() {
        hideAll();
        showOnly(2, 3);
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
        main.getCamera().setRotation(INITIAL_ROTATION);
        main.getCollisionManager().getAppState().getPhysicsSpace().add(main.getPlayer().getBody());
        main.getInputManager().setCursorVisible(false);
    }

    public boolean isGuiActive() {
        for (ColorParkourGUI gui : GUIS) {
            if (gui.isActive() && gui.hasCursorActions()) {
                return true;
            }
        }
        return false;
    }

    public boolean isGuiActive(int index) {
        return GUIS.get(index).isActive();
    }
    public boolean isChangeFOVActive() { return changeFOV; }

    public void setChangeFOV(boolean b) { this.changeFOV = b; }

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