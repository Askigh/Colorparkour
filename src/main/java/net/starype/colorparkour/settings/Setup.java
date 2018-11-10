package net.starype.colorparkour.settings;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.platform.ColoredPlatform;
import net.starype.colorparkour.entity.player.PlayerInventory;
import net.starype.colorparkour.entity.player.PlayerPhysicSY;
import static net.starype.colorparkour.core.ColorParkourMain.*;

public class Setup {

    public static void init(ColorParkourMain main) {
        setUpLight(main);
        initKeys(main);
    }
    private static void setUpLight(ColorParkourMain main) {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));


        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());

        main.attachLights(al, dl);
    }

    private static void initKeys(ColorParkourMain main) {

        KeyboardManager kManager = new KeyboardManager(main);
        PlayerPhysicSY physics = main.getPlayer().getPhysicPlayer();

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
        kManager.addLinkedKeyAction("Sprint", KeyInput.KEY_F, new KeyboardManager.Action() {
            @Override
            public void execute(boolean keyPressed) {
                if(keyPressed)
                    physics.sprint();
                else physics.walk();
            }
        }.withReleaseActive(true));
        kManager.addLinkedKeyAction("SuperColor", KeyInput.KEY_TAB, new KeyboardManager.Action() {
            @Override
            public void execute(boolean keyPressed) {
                for(ColoredPlatform plat : main.getModuleManager().getCurrentModule().getPlatforms())
                    plat.setColor(ColorRGBA.randomColor());
            }
        });
        kManager.addLinkedKeyAction("No cursor", KeyInput.KEY_ESCAPE, new KeyboardManager.Action() {
            @Override
            public void execute(boolean keyPressed) {

                InputManager inputManager = main.getInputManager();
                inputManager.getCursorPosition().set(WIDTH/2, HEIGHT/2);
                if(inputManager.isCursorVisible()) {
                    inputManager.setCursorVisible(false);
                    main.getCamera().setRotation(INITIAL_ROTATION);
                    main.getStateManager().getState(PlayerInventory.class).hide();
                } else {
                    inputManager.setCursorVisible(true);
                    main.getCamera().setRotation(new Quaternion(0, 2, 0, 0));
                    main.getStateManager().getState(PlayerInventory.class).show();
                }
            }
        });

    }
}