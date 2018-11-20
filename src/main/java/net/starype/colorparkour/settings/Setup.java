package net.starype.colorparkour.settings;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.GuiGlobals;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.player.CameraSY;
import net.starype.colorparkour.entity.player.gui.PlayerInventory;
import net.starype.colorparkour.entity.player.PlayerPhysicSY;
import static net.starype.colorparkour.core.ColorParkourMain.*;

public class Setup {

    public static void init(ColorParkourMain main) {
        setUpLight(main);
        initKeys(main);
    }
    private static void setUpLight(ColorParkourMain main) {

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.Yellow);
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));

        main.attachLights(sun);
    }

    private static void initKeys(ColorParkourMain main) {

        KeyboardManager kManager = new KeyboardManager(main);
        PlayerPhysicSY physics = main.getPlayer().getPhysicPlayer();

        // Movement controls
        kManager.addLinkedKeyAction("Left", KeyInput.KEY_A, new KeyboardManager.Action() {

            @Override
            public void execute(boolean keyPressed) {
                physics.left = keyPressed;
            }
        }.withReleaseActive(true));
        kManager.addLinkedKeyAction("Right", KeyInput.KEY_D, new KeyboardManager.Action() {

            @Override
            public void execute(boolean keyPressed) {
                physics.right = keyPressed;
            }
        }.withReleaseActive(true));
        kManager.addLinkedKeyAction("Forward", KeyInput.KEY_W, new KeyboardManager.Action() {

            @Override
            public void execute(boolean keyPressed) {
                physics.forward = keyPressed;
            }
        }.withReleaseActive(true));
        kManager.addLinkedKeyAction("Down", KeyInput.KEY_S, new KeyboardManager.Action() {

            @Override
            public void execute(boolean keyPressed) {
                physics.backward = keyPressed;
            }
        }.withReleaseActive(true));
        kManager.addLinkedKeyAction("Jump", KeyInput.KEY_SPACE, new KeyboardManager.Action() {
            @Override
            public void execute(boolean keyPressed) {
                physics.jump();
            }
        });
        kManager.addLinkedKeyAction("Sprint", KeyInput.KEY_LSHIFT, new KeyboardManager.Action() {
            @Override
            public void execute(boolean keyPressed) {
                if(keyPressed)
                    physics.sprint();
                else physics.walk();
            }
        }.withReleaseActive(true));

        // Menu opening
        kManager.addLinkedKeyAction("Escape", KeyInput.KEY_ESCAPE, new KeyboardManager.Action() {
            @Override
            public void execute(boolean keyPressed) {
                InputManager inputManager = main.getInputManager();
                PlayerInventory inventory = main.getPlayerInventory();
                if(inventory.isGuiActive(0)) {
                    return;
                }
                inputManager.getCursorPosition().set(WIDTH/2, HEIGHT/2);
                if(inventory.isGuiActive()) {
                    GuiGlobals.getInstance().setCursorEventsEnabled(false);
                    inventory.activatePlayer(false);

                } else {
                    GuiGlobals.getInstance().setCursorEventsEnabled(true);
                    inputManager.setCursorVisible(true);
                    //main.getCamera().setRotation(new Quaternion(0, 2, 0, 0));
                    main.getCollisionManager().getAppState().getPhysicsSpace().remove(main.getPlayer().getBody());
                    inventory.showOnly(1);
                }
            }
        });

        // Color switches
        kManager.addLinkedKeyAction("1", KeyInput.KEY_1, new KeyboardManager.Action() {
            @Override
            public void execute(boolean keyPressed) {
                main.getPlayerInventory().highlight("red", ColorRGBA.Red);
            }
        });
        kManager.addLinkedKeyAction("2", KeyInput.KEY_2, new KeyboardManager.Action() {
            @Override
            public void execute(boolean keyPressed) {
                main.getPlayerInventory().highlight("blue", ColorRGBA.Blue);
            }
        });
        kManager.addLinkedKeyAction("3", KeyInput.KEY_3, new KeyboardManager.Action() {
            @Override
            public void execute(boolean keyPressed) {
                main.getPlayerInventory().highlight("yellow", ColorRGBA.Yellow);
            }
        });
        kManager.addLinkedKeyAction("4", KeyInput.KEY_4, new KeyboardManager.Action() {
            @Override
            public void execute(boolean keyPressed) {
                main.getPlayerInventory().highlight("green", ColorRGBA.Green);
            }
        });

        // FOV changes
        kManager.addLinkedKeyAction("Increase FOV", KeyInput.KEY_RIGHT, new KeyboardManager.Action() {
            @Override
            public void execute(boolean keyPressed) {
                if(main.getPlayerInventory().isChangeFOVActive()) {
                    CameraSY camSY = main.getPlayer().getCamera();
                    camSY.setFov(camSY.getFov()+1);
                }
            }
        });
        kManager.addLinkedKeyAction("Lower FOV", KeyInput.KEY_LEFT, new KeyboardManager.Action() {
            @Override
            public void execute(boolean keyPressed) {
                if(main.getPlayerInventory().isChangeFOVActive()) {
                    CameraSY camSY = main.getPlayer().getCamera();
                    camSY.setFov(camSY.getFov()-1);
                }
            }
        });

    }
}