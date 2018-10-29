package net.starype.colorparkour.core;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.entity.platform.PlatformManager;
import net.starype.colorparkour.entity.player.Player;
import net.starype.colorparkour.settings.Setup;
import net.starype.colorparkour.utils.TimerSY;

import java.util.Arrays;

public class ColorParkourMain extends SimpleApplication {

    private CollisionManager collManager;
    private PlatformManager platformManager;
    private Player player;
    public static final Vector3f GAME_GRAVITY = new Vector3f(0, -40f, 0);
    public static final Vector3f LOW_GRAVITY = new Vector3f(0, -20f, 0);
    public static final Vector3f HIGH_GRAVITY = new Vector3f(0, -55f, 0);

    private TimerSY firstLevelTimer;

    public static void main(String[] args) { new ColorParkourMain(); }

    private ColorParkourMain() {

        setSettings(new AppSettings(true));
        // disables the default window that asks for settings
        setShowSettings(false);
        settings.setTitle("ColOrParkOur");
        settings.setSamples(8);
        settings.setWidth(1500);
        settings.setHeight(700);
        super.setDisplayStatView(false);
        super.setDisplayFps(true);
        super.start();
    }

    @Override
    public void simpleInitApp() {

        disableDefaultOptions();

        viewPort.setBackgroundColor(ColorRGBA.Cyan);
        collManager = new CollisionManager(this);
        collManager.init();

        platformManager = new PlatformManager(collManager, this);
        player = new Player(this, cam, collManager, platformManager);
        player.initialize();
        platformManager.attachBody(player.getBody());

        PhysicsSpace space = collManager.getAppState().getPhysicsSpace();
        Module firstMap = new Module(this, platformManager, space);
        firstMap.add(platformManager.ice(5, 3f, 5, 0, -3, 0, ColorRGBA.White),
                platformManager.doubleJump(5, 1f, 5, 20, -1, 0, ColorRGBA.Blue),
                platformManager.colored(5, 0.8f, 5, 50, 1, 0, ColorRGBA.Orange),
                platformManager.sticky(5, 0.5f, 5, new Vector3f(65, 1, 30),
                        new Vector3f(65, 1, -30), 0.1f, ColorRGBA.Black),
                platformManager.doubleJump(2f, 0.5f, 2f, 80, 0, -20f, ColorRGBA.Red));

        firstMap.setActive(true);

        Vector3f initial = new Vector3f(0,20,0);
        cam.setLocation(initial);
        player.setPosition(initial);

        firstLevelTimer = new TimerSY(guiFont, ColorRGBA.Blue, new Vector2f(0, 50), "Timer: ", "mm:ss");
        guiNode.attachChild(firstLevelTimer.getBitmapText());

        // Init keyboard inputs and light sources
        Setup.init(this);
    }

    @Override
    public void simpleUpdate(float tpf) {
        platformManager.reversePlatforms();
        firstLevelTimer.updateTimer(tpf);
    }

    public void attachChild(Spatial... spatials) { Arrays.asList(spatials).forEach(s -> rootNode.attachChild(s)); }
    public void attachLights(Light... lights) { Arrays.asList(lights).forEach(l -> rootNode.addLight(l)); }

    private void disableDefaultOptions(){
        // disables FlyByCamera, replaced by CameraSY
        guiNode.detachAllChildren();
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        inputManager.setCursorVisible(false);
    }

    public Player getPlayer(){ return player; }

    public PlatformManager getPlatformManager() { return platformManager; }
}
