package net.starype.colorparkour.entity.player.gui;

import com.jme3.input.KeyInput;
import com.simsilica.lemur.*;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.player.PlayerPhysicSY;
import net.starype.colorparkour.entity.player.gui.list.ColorSelector;
import net.starype.colorparkour.entity.player.gui.list.ControlSettings;
import net.starype.colorparkour.entity.player.gui.list.GameMenu;
import net.starype.colorparkour.entity.player.gui.list.PauseMenu;

import java.util.*;

import static net.starype.colorparkour.core.ColorParkourMain.*;

public class PlayerInventory {

    private ColorParkourMain main;
    private final List<ColorParkourGUI> GUIS = new ArrayList<>();
    private boolean changeFOV = true;
    private ControlSettings settings;
    private ColorSelector selector;

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

        settings = new ControlSettings(main, this);
        PlayerPhysicSY physicSY = main.getPlayer().getPhysicPlayer();
        settings.new Action("Forward", KeyInput.KEY_W, true) {
            @Override
            public void execute(boolean keyPressed) {
                physicSY.forward = keyPressed;
            }
        };
        settings.new Action("Backward", KeyInput.KEY_S, true) {
            @Override
            public void execute(boolean keyPressed) {
                physicSY.backward = keyPressed;
            }
        };
        settings.new Action("Left", KeyInput.KEY_A, true) {
            @Override
            public void execute(boolean keyPressed) {
                physicSY.left = keyPressed;
            }
        };
        settings.new Action("Right", KeyInput.KEY_D, true) {
            @Override
            public void execute(boolean keyPressed) {
                physicSY.right = keyPressed;
            }
        };
        settings.new Action("Jump", KeyInput.KEY_SPACE, false) {
            @Override
            public void execute(boolean keyPressed) {
                physicSY.jump();
            }
        };
        settings.new Action("Sprint", KeyInput.KEY_F, true) {
            @Override
            public void execute(boolean keyPressed) {
                if(keyPressed)
                    physicSY.sprint();
                else physicSY.walk();
            }
        };
        GUIS.add(new ColorParkourGUI("game_menu", new GameMenu(this)).withInputActions(true));
        GUIS.add(new ColorParkourGUI("pause_menu", new PauseMenu(this)).withInputActions(true));
        selector = new ColorSelector(this);
        GUIS.add(new ColorParkourGUI("color_selector", selector));
        GUIS.add(new ColorParkourGUI("color_highlighted", selector.getHighlighted()));
    }



    public void activatePlayer(boolean resetRotation) {
        hideAll();
        showOnly(3, 4);
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
        if(resetRotation) {
            LOGGER.info("RESETTING ROTATION");
            main.getCamera().setRotation(INITIAL_ROTATION);
        }
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
    public ColorParkourGUI findByName(String name) {
        for(ColorParkourGUI gui : GUIS) {
            if(gui.getName().equals(name)) {
                return gui;
            }
        }
        return null;
    }
    public ColorSelector getSelector() { return selector; }
    public boolean isGuiActive(int index) { return GUIS.get(index).isActive(); }
    public boolean isChangeFOVActive() { return changeFOV; }
    public void setChangeFOV(boolean b) { this.changeFOV = b; }
    public List<ColorParkourGUI> getGUIS() { return GUIS; }
    public ColorParkourMain getMainInstance() { return main; }
    public ControlSettings getSettings() { return settings; }

}