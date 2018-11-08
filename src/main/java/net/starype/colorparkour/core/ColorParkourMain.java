package net.starype.colorparkour.core;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.core.module.ModuleManager;
import net.starype.colorparkour.core.module.ModuleSY;
import net.starype.colorparkour.entity.player.Player;
import net.starype.colorparkour.settings.Setup;
import net.starype.colorparkour.utils.PlatformBuilder;
import net.starype.colorparkour.utils.Referential;
import net.starype.colorparkour.utils.TimerSY;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ColorParkourMain extends SimpleApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(SimpleApplication.class);
    public static final Vector3f GAME_GRAVITY = new Vector3f(0, -40f, 0);
    public static final Vector3f LOW_GRAVITY = new Vector3f(0, -20f, 0);
    public static final Vector3f HIGH_GRAVITY = new Vector3f(0, -55f, 0);
    private CollisionManager collManager;
    private ModuleManager moduleManager;
    private Player player;
    private TimerSY firstLevelTimer;

    private ColorParkourMain() {
        LOGGER.info("Game initialization...");

        //Creating settings configuration
        setSettings(new AppSettings(true));
        setShowSettings(false); // disables the default window that asks for settings

        //Settings
        settings.setTitle("ColOrParkOur");
        settings.setSamples(8);
        settings.setWidth(1500);
        settings.setHeight(500);
        super.setDisplayStatView(false);
        super.setDisplayFps(true);
        this.moduleManager = new ModuleManager(this);

        //Starting the window.
        LOGGER.info("Window is opening.");
        super.start();
    }

    public static void main(String[] args) {
        new ColorParkourMain();
    }

    @Override
    public void simpleInitApp() {

        disableDefaultOptions();

        viewPort.setBackgroundColor(ColorRGBA.randomColor());

        collManager = new CollisionManager(this);
        collManager.init();

        PlatformBuilder builder = new PlatformBuilder(collManager, this);

        LOGGER.info("Initializing collisions");
        player = new Player(this, cam, collManager, moduleManager);
        player.initialize();
        moduleManager.attachPlayer(player);

        PhysicsSpace space = collManager.getAppState().getPhysicsSpace();
        ModuleSY firstMap = new ModuleSY(this, space, this.getClass().getResource("/levels/firstLevel.json").getPath())
                .add(builder.ice(new float[]{5, 3f, 5}, new float[]{0f, -3f, 0f}, ColorRGBA.White, "0:0"),
                        builder.doubleJump(new float[]{5, 0.1f, 5}, new float[]{20, -1, 0}, ColorRGBA.Blue, "0:1"),
                        builder.colored(new float[]{5, 0.1f, 5}, new float[]{50, 1, 0}, ColorRGBA.Orange, "0:2"),
                        builder.sticky(new float[]{5, 0.1f, 5}, new Vector3f(65, 1, 30),
                                new Vector3f(65, 1, -30), 0.2f, ColorRGBA.Black, "0:3"),
                        builder.doubleJump(new float[]{2f, 0.3f, 2f}, new float[]{80, 0, -20f}, ColorRGBA.Red, "0:4"))
                .build();

        moduleManager.add(firstMap);
        moduleManager.start();
        Vector3f initial = moduleManager.first().getPosition().add(0, 2, 0);
        cam.setLocation(initial);
        cam.setRotation(new Quaternion(0, 0.7f, 0, 0.7f));
        player.setPosition(initial);

        firstLevelTimer = new TimerSY(guiFont, ColorRGBA.Blue, new Vector2f(0, 50), "Timer: ", "mm:ss", "firstLevel");
        guiNode.attachChild(firstLevelTimer.getBitmapText());

        // Init keyboard inputs and light sources
        Setup.init(this);
        //collManager.getAppState().setDebugEnabled(true);
    }

    @Override
    public void simpleUpdate(float tpf) {
        Referential.updateAll();
        firstLevelTimer.updateTimer(tpf);
        moduleManager.getCurrentModule().reversePlatforms();
        moduleManager.checkNext(player.getBody().getPhysicsLocation());
    }

    public void attachChilds(Spatial... spatials) {
        Arrays.asList(spatials).forEach(s -> rootNode.attachChild(s));
    }

    public void attachLights(Light... lights) {
        Arrays.asList(lights).forEach(l -> rootNode.addLight(l));
    }

    private void disableDefaultOptions() {
        // disables FlyByCamera, replaced by CameraSY
        guiNode.detachAllChildren();
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        inputManager.setCursorVisible(false);
    }

    public Player getPlayer() {
        return player;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CollisionManager getCollisionManager() {
        return collManager;
    }
}
