package net.starype.colorparkour.entity.player.gui;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.IconComponent;
import net.starype.colorparkour.core.ColorParkourMain;

import java.util.ArrayList;
import java.util.List;

import static net.starype.colorparkour.core.ColorParkourMain.*;

public class PlayerInventory {

    private ColorParkourMain main;
    private final List<ColorParkourGUI> GUIS = new ArrayList<>();
    private Container gameMenu;

    public PlayerInventory(ColorParkourMain main) {
        this.main = main;
        loadMenus();
    }
    public void showOnly(int guiID) {
        hideAll();
        show(guiID);
    }

    public void show(int guiID) {
        ColorParkourGUI gui = GUIS.get(guiID);
        main.getGuiNode().attachChild(gui.getGUI());
        gui.setActive(true);
        if(gui.hasCursorActions()) {
            LOGGER.info("GUI needs cursor events");
            GuiGlobals.getInstance().setCursorEventsEnabled(true);
        }
    }

    public void hideAll() {
        for(ColorParkourGUI gui : GUIS) {
            if(gui.isActive()) {
                main.getGuiNode().detachChild(gui.getGUI());
                gui.setActive(false);
            }
        }
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
    }
    public void hide(int index) {
        if(GUIS.get(index).isActive()) {
            main.getGuiNode().detachChild(GUIS.get(index).getGUI());
            GUIS.get(index).setActive(false);
        } else LOGGER.error("GUI IS ALREADY DISABLED");

        if(GUIS.get(index).hasCursorActions()) {
            GuiGlobals.getInstance().setCursorEventsEnabled(false);
        }
    }

    private void loadMenus() {

        Container gameMenu = new Container();
        gameMenu.setLocalTranslation((WIDTH-200) / 2, HEIGHT/2+50, 0);
        GUIS.add(new ColorParkourGUI(gameMenu).withInputActions(true));

        Button play = gameMenu.addChild(new Button("Play"));
        play.setColor(ColorRGBA.Orange);
        play.setSize(new Vector3f(300, 100, 0));
        play.addClickCommands((Command<Button>) source -> {
            main.startGame();
            activatePlayer();
        });
        play.scale(5);

        Container pauseMenu = new Container();
        pauseMenu.setLocalTranslation((WIDTH-400) / 2, HEIGHT/2+100, 0);

        Button quitButton = pauseMenu.addChild(new Button("Quit Game"));
        quitButton.setColor(ColorRGBA.Black);
        quitButton.addClickCommands((Command<Button>) source -> main.stop());
        quitButton.setLocalTranslation(0, 0, 0);

        Button restartLevel = pauseMenu.addChild(new Button("Restart level"));
        restartLevel.addClickCommands(source -> {
            main.getPlayer().resetPosition(main.getModuleManager().getCurrentModule());
            activatePlayer();
        });
        restartLevel.setColor(ColorRGBA.Green);
        restartLevel.setLocalTranslation(new Vector3f(50, 200, 0));
        GUIS.add(new ColorParkourGUI(pauseMenu).withInputActions(true));

        Container playerInventory = new Container();
        GUIS.add(new ColorParkourGUI(playerInventory));
        playerInventory.setPreferredSize(new Vector3f(500, 400, 0));
        playerInventory.setLocalTranslation(WIDTH - 90, 420, 0);
        //playerInventory.setBackground(new IconComponent("assets/Textures/slots/bar.png"));
        createInventoryBar(playerInventory, "assets/Textures/slots/",
                "red.png", "blue.png", "yellow.png", "green.png");
    }
    private void createInventoryBar(Container self, String folder, String... paths) {
        float x = 0;
        for(String path : paths) {
            IconComponent component = new IconComponent(folder+path);
            Label label = new Label(null);
            Vector2f size = component.getIconScale();
            label.setPreferredSize(new Vector3f(size.x * 20, size.y * 20, 0));
            self.addChild(label);
            label.setLocalTranslation(x, x, 0);
            x += 100 * component.getIconScale().x;
            label.setBackground(component);
        }
    }
    public void activatePlayer() {
        hideAll();
        showOnly(2);
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
        main.getCamera().setRotation(INITIAL_ROTATION);
        main.getCollisionManager().getAppState().getPhysicsSpace().add(main.getPlayer().getBody());
        main.getInputManager().setCursorVisible(false);
    }
    public boolean isGuiActive() {
        for(ColorParkourGUI gui : GUIS) {
            if(gui.isActive() && gui.hasCursorActions()) {
                return true;
            }
        }
        return false;
    }
    public boolean isGuiActive(int index) { return GUIS.get(index).isActive(); }
}