package net.starype.colorparkour.entity.player;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import net.starype.colorparkour.core.ColorParkourMain;

import java.util.ArrayList;
import java.util.List;

import static net.starype.colorparkour.core.ColorParkourMain.*;

public class PlayerInventory {

    private ColorParkourMain main;
    private final List<Container> GUIS = new ArrayList<>();
    private int currentGUI = -1;

    public PlayerInventory(ColorParkourMain main) {
        this.main = main;
        loadMenus();
    }
    public void show(int guiID) {
        hideCurrentGUI();
        main.getGuiNode().attachChild(GUIS.get(guiID));
        currentGUI = guiID;
    }

    public void hideCurrentGUI() {
        if(currentGUI != -1) {
            main.getGuiNode().detachChild(GUIS.get(currentGUI));
        }
        currentGUI = -1;
    }
    private void loadMenus() {

        Container gameMenu = new Container();
        gameMenu.setLocalTranslation((WIDTH-200) / 2, HEIGHT/2+50, 0);
        GUIS.add(gameMenu);

        Button play = gameMenu.addChild(new Button("Play"));
        play.setColor(ColorRGBA.Orange);
        play.setSize(new Vector3f(300, 100, 0));
        play.addClickCommands((Command<Button>) source -> {
            main.startGame();
            hideCurrentGUI();
        });
        play.scale(5);

        Container pauseMenu = new Container();
        pauseMenu.setLocalTranslation((WIDTH-400) / 2, HEIGHT/2+100, 0);

        Button quitButton = pauseMenu.addChild(new Button("Quit Game"));
        quitButton.setColor(ColorRGBA.Black);
        quitButton.setSize(new Vector3f(WIDTH, HEIGHT, 0));
        quitButton.addClickCommands((Command<Button>) source -> main.stop());
        quitButton.scale(3);
        quitButton.setLocalTranslation(0, 0, 0);

        Button restartLevel = pauseMenu.addChild(new Button("Restart level"));
        restartLevel.addClickCommands(source -> {
            main.getPlayer().resetPosition(main.getModuleManager().getCurrentModule());
            hideCurrentGUI();
            activatePlayer();
        });
        restartLevel.setColor(ColorRGBA.Green);
        restartLevel.setLocalTranslation(new Vector3f(50, 200, 0));
        restartLevel.scale(3);
        GUIS.add(pauseMenu);
    }
    public void activatePlayer() {
        hideCurrentGUI();
        main.getCamera().setRotation(INITIAL_ROTATION);
        main.getCollisionManager().getAppState().getPhysicsSpace().add(main.getPlayer().getBody());
        main.getInputManager().setCursorVisible(false);
    }
    public Container getPauseMenu() { return GUIS.get(1); }
    public boolean isGuiActive() { return currentGUI != -1; }
    public boolean isGuiActive(int index) { return currentGUI == index; }
}