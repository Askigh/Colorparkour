package net.starype.colorparkour.settings;

import com.jme3.input.KeyInput;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.player.PlayerPhysicSY;

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

    }
}