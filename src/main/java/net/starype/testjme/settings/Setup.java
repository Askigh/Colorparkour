package net.starype.testjme.settings;

import com.jme3.input.KeyInput;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.testjme.core.ColorParkourMain;

public class Setup {

    private ColorParkourMain main;

    public Setup(ColorParkourMain main){ this.main = main; }

    public void init() {
        setUpLight();
        initKeys();
    }

    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));


        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());

        main.attachLights(al, dl);
    }

    private void initKeys() {

        KeyboardManager kManager = new KeyboardManager(main);

        kManager.addLinkedKeyAction("Left", KeyInput.KEY_A, new KeyboardManager.Action() {

            @Override
            public void execute(boolean keyPressed) {
                main.left = keyPressed;
            }
        }.withReleaseActive(true));
        kManager.addLinkedKeyAction("Right", KeyInput.KEY_D, new KeyboardManager.Action() {

            @Override
            public void execute(boolean keyPressed) {
                main.right = keyPressed;
            }
        }.withReleaseActive(true));
        kManager.addLinkedKeyAction("Forward", KeyInput.KEY_W, new KeyboardManager.Action() {

            @Override
            public void execute(boolean keyPressed) {
                main.forward = keyPressed;
            }
        }.withReleaseActive(true));
        kManager.addLinkedKeyAction("Down", KeyInput.KEY_S, new KeyboardManager.Action() {

            @Override
            public void execute(boolean keyPressed) {
                main.down = keyPressed;
            }
        }.withReleaseActive(true));

    }
}