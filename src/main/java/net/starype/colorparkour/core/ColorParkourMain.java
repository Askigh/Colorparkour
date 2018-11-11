package net.starype.colorparkour.core;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.module.ModuleManager;
import net.starype.colorparkour.module.ModuleSY;
import net.starype.colorparkour.entity.player.Player;
import net.starype.colorparkour.entity.player.PlayerInventory;
import net.starype.colorparkour.settings.Setup;
import net.starype.colorparkour.utils.PlatformBuilder;
import net.starype.colorparkour.utils.Referential;
import net.starype.colorparkour.utils.TimerSY;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ColorParkourMain extends SimpleApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(SimpleApplication.class);
    public static final Vector3f GAME_GRAVITY = new Vector3f(0, -50f, 0);
    public static final Quaternion INITIAL_ROTATION = new Quaternion(0, 0.7f, 0, 0.7f);
    private CollisionManager collManager;
    private ModuleManager moduleManager;
    private Player player;
    private TimerSY gameTimer;
    private PlayerInventory inventory;
    public static final int WIDTH = 1500;
    public static final int HEIGHT = 500;

    private ColorParkourMain() {
        LOGGER.info("Game initialization...");

        //Creating settings configuration
        setSettings(new AppSettings(true));
        setShowSettings(false); // disables the default window that asks for settings

        //Settings
        settings.setTitle("ColOrParkOur");
        settings.setSamples(8);
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setResizable(true);
        super.setDisplayStatView(false);
        super.setDisplayFps(true);
        this.moduleManager = new ModuleManager(this);

        //Starting the window.
        LOGGER.info("Window is opening.");
        super.start();
    }

    public static void main(String[] args) { new ColorParkourMain(); }

    /**
     * The {@link ColorParkourMain#simpleInitApp()} init all the contents required for the game loop
     * Once all is set up, the {@link ColorParkourMain#simpleUpdate(float)} takes care of what
     * needs to be updated and resetted each frame
     */
    @Override
    public void simpleInitApp() {

        /***************************************************************
            Init the basics features, disables some of the default ones
         ***************************************************************/
        disableDefaultOptions();
        GuiGlobals.initialize(this);
        loadAudio();
        collManager = new CollisionManager(this);
        collManager.init();

        /*****************************************
            Creates the player and his inventory
         *****************************************/
        player = new Player(this, cam, collManager, moduleManager);
        inventory = new PlayerInventory(this);

        /************************
            Loads platforms
         ************************/
        PlatformBuilder builder = new PlatformBuilder(collManager, this);
        createModules(builder);

        /************************
            Creates the timer
         ************************/
        gameTimer = new TimerSY(guiFont, ColorRGBA.Blue, new Vector2f(0, 50), "Timer: ", "mm:ss",
                "firstLevel");
        guiNode.attachChild(gameTimer.getBitmapText());

        /****************************
            Activates the main menu
         ****************************/
        cam.setLocation(new Vector3f(0, 100, 0));
        inventory.show(0);

        /**********************************************************
            Init the lights, and the inputs using KeyboardManager
         **********************************************************/
        Setup.init(this);
    }

    public void startGame() {
        LOGGER.info("GAME HAS BEGUN");
        moduleManager.start();
        player.initialize();
        player.resetPosition(moduleManager.getCurrentModule());
        inputManager.setCursorVisible(false);
    }

    public void createModules(PlatformBuilder builder) {
        PhysicsSpace space = collManager.getAppState().getPhysicsSpace();
        new ModuleSY(this, space, this.getClass().getResource("/levels/firstLevel.json").getPath())
                .add(builder.ice(new float[]{5, 3f, 5}, new float[]{0f, -3f, 0f}, ColorRGBA.White, "0:0"),
                        builder.doubleJump(new float[]{5, 0.1f, 5}, new float[]{20, -1, 0}, ColorRGBA.Blue, "0:1"),
                        builder.colored(new float[]{5, 0.1f, 5}, new float[]{50, 1, 0}, ColorRGBA.Orange, "0:2"),
                        builder.sticky(new float[]{5, 0.1f, 5}, new Vector3f(65, 1, 30),
                                new Vector3f(65, 1, -30), 0.2f, ColorRGBA.Black, "0:3"),
                        builder.doubleJump(new float[]{2f, 0.3f, 2f}, new float[]{80, 0, -20f}, ColorRGBA.Red, "0:4"))
                .build(moduleManager);
        new ModuleSY(this, space, this.getClass().getResource("/levels/firstLevel.json").getPath())
                .add(builder.defaultPlatform())
                .add(builder.moving(new float[]{1.2f, 0.5f, 1.2f}, new Vector3f(5, 0, 10),
                        new Vector3f(-5, 0, 20), 0.1f, ColorRGBA.Green, "1:1"))
                .add(builder.moving(new float[]{2.5f, 1f, 2.5f}, new Vector3f(-5, 0, 25),
                        new Vector3f(5, 6, 30), 0.2f, ColorRGBA.Green, "1:2"))
                .add(builder.colored(new float[]{1, 1, 1}, new float[]{5, 6, 33}, ColorRGBA.Black, "1:3"))
                .build(moduleManager);
    }

    /**
     * This methods takes car of everything that needs to be updated constantly, such as
     * referentials, timer, platforms movement and end module check
     * @param tpf time per frame
     */
    @Override
    public void simpleUpdate(float tpf) {
        Referential.updateAll();
        if(!isPaused()) {
            gameTimer.updateTimer(tpf);
        }
        moduleManager.getCurrentModule().reversePlatforms();
        moduleManager.checkNext(player.getBody().getPhysicsLocation());
    }

    public void attachChilds(Spatial... spatials) { Arrays.asList(spatials).forEach(s -> rootNode.attachChild(s)); }
    public void attachLights(Light... lights) { Arrays.asList(lights).forEach(l -> rootNode.addLight(l)); }

    // Plays the lovely Opening from 'Sword Art Online: Alicization'
    private void loadAudio() {
        AudioNode node = new AudioNode(assetManager, "audio/sound.wav", AudioData.DataType.Stream);
        node.setVolume(0.01f);
        node.setLooping(true);
        node.setPositional(false);
        node.play();
        attachChilds(node);
    }
    private void disableDefaultOptions() {
        // disables FlyByCamera and mappings, replaced by CameraSY and Setup
        inputManager.clearMappings();
        guiNode.detachAllChildren();
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
    }
    public boolean isPaused() {
        return inventory.isGuiActive();
    }

    public Player getPlayer() { return player; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public CollisionManager getCollisionManager() { return collManager; }
    public PlayerInventory getPlayerInventory() { return inventory; }
}
