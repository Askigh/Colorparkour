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
import com.jme3.util.SkyFactory;
import com.simsilica.lemur.GuiGlobals;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.starype.colorparkour.rpc.DiscordRPCManager;
import net.starype.colorparkour.utils.CollisionManager;
import net.starype.colorparkour.module.ModuleManager;
import net.starype.colorparkour.module.ModuleSY;
import net.starype.colorparkour.entity.player.Player;
import net.starype.colorparkour.entity.player.gui.PlayerInventory;
import net.starype.colorparkour.settings.Setup;
import net.starype.colorparkour.utils.PlatformBuilder;
import net.starype.colorparkour.utils.Referential;
import net.starype.colorparkour.utils.TimerSY;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ColorParkourMain extends SimpleApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(SimpleApplication.class);
    public static final Vector3f GAME_GRAVITY = new Vector3f(0, -60f, 0);
    public static final Quaternion INITIAL_ROTATION = new Quaternion(0, 0.7f, 0, 0.7f);
    private Spatial sky;
    private CollisionManager collManager;
    private ModuleManager moduleManager;
    private Player player;
    private TimerSY gameTimer;
    private PlayerInventory inventory;
    public static final int WIDTH = 1500;
    public static final int HEIGHT = 800;
    private boolean raceFinished = false;
    private boolean started = false;
    private DiscordRPCManager rpcManager;
    public static final String VERSION = "ALPHA";

    private ColorParkourMain() {
        LOGGER.info("Game initialization...");

        //Creating settings configuration
        setSettings(new AppSettings(true));
        setShowSettings(false); // disables the default window that asks for settings

        //Settings
        settings.setTitle("ColorParkour");
        settings.setSamples(8);
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setResizable(false);
        loadIcon();
        super.setDisplayStatView(false);
        super.setDisplayFps(true);
        this.moduleManager = new ModuleManager(this);
        this.rpcManager = new DiscordRPCManager(this);
        this.rpcManager.init();

        //Starting the window.
        LOGGER.info("Window is opening.");
        super.start();
    }

    public static void main(String[] args) { new ColorParkourMain(); }

    /**
     * The simpleInitApp method inits all the contents required for the game loop
     * Once all is set up, the {@link ColorParkourMain#simpleUpdate(float)} takes care of what
     * needs to be updated and resetted each frame
     */
    @Override
    public void simpleInitApp() {

        /***************************************************************
            Inits the basics features, disables some of the default ones
         ***************************************************************/
        disableDefaultOptions();
        GuiGlobals.initialize(this);
        GuiGlobals.getInstance().requestFocus(null);
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
        inventory.showOnly(1);

        /**********************************************************
         Init the lights, and the inputs using KeyboardManager
         **********************************************************/
        Setup.init(this);

        /***********************************************************
          Creates the sky
         ************************************************************/
        sky = SkyFactory.createSky(getAssetManager(), "assets/sky/Skysphere.jpg",
                SkyFactory.EnvMapType.EquirectMap);
        rootNode.attachChild(sky);
       this.rpcManager.update(new DiscordRichPresence
               .Builder("Waiting ....")
               .setBigImage("green_-_1024", "ColorParkour " + VERSION)
               .build()
       );
    }

    @Override
    public void stop() {
        super.stop();
        this.rpcManager.stop();
    }

    public void startGame() {
        LOGGER.info("GAME HAS BEGUN");
        moduleManager.start();
        player.initialize();
        player.resetPosition(moduleManager.getCurrentModule());
        started = true;
        this.rpcManager.setStartTimestamps(System.currentTimeMillis());
        this.rpcManager.update(new DiscordRichPresence
                .Builder("Level: " + getModuleManager().getCurrentModule().getLevelName())
                .setDetails("In Game")
                .setStartTimestamps(this.rpcManager.getStartTimestamps())
                .setBigImage("green_-_1024", "ColorParkour " + VERSION)
                .build()
        );
    }

    // This needs to be replaced by the json loader
    public void createModules(PlatformBuilder builder) {
        PhysicsSpace space = collManager.getAppState().getPhysicsSpace();

        new ModuleSY(this, space, this.getClass().getResource("/levels/firstLevel.json").getPath())
                .add(builder.ice(new float[]{5, 3f, 5}, new float[]{0f, -3f, 0f}, ColorRGBA.White, "0:0"),
                        builder.doubleJump(new float[]{5, 0.1f, 5}, new float[]{20, -1, 0}, ColorRGBA.Yellow, "0:1"),
                        builder.colored(new float[]{5, 0.1f, 5}, new float[]{44, 1, 0}, ColorRGBA.Green, "0:2"),
                        builder.moving(new float[]{5, 0.1f, 5}, new Vector3f(54, 1, 30),
                                new Vector3f(65, 1, -30), 0.13f, ColorRGBA.White, "0:3"),
                        builder.doubleJump(new float[]{2f, 0.3f, 2f}, new float[]{74, 0, -20f}, ColorRGBA.Red, "0:4"))
                .build(moduleManager);
        new ModuleSY(this, space, this.getClass().getResource("/levels/firstLevel.json").getPath())
                .add(builder.defaultPlatform())
                .add(builder.moving(new float[]{1.2f, 0.5f, 1.2f}, new Vector3f(5, 0, 15),
                        new Vector3f(-5, 0, 25), 0.1f, ColorRGBA.Green, "1:1"))
                .add(builder.moving(new float[]{2.5f, 1f, 2.5f}, new Vector3f(-5, 0, 30),
                        new Vector3f(5, 6, 35), 0.2f, ColorRGBA.Green, "1:2"))
                .add(builder.colored(new float[]{1, 1, 1}, new float[]{5, 6, 38}, ColorRGBA.White, "1:3"))
                .build(moduleManager);
        new ModuleSY(this, space, this.getClass().getResource("/levels/firstLevel.json").getPath())
                .add(builder.defaultPlatform())
                .add(builder.colored(new float[]{0.3f, 5, 5}, new float[]{9, 8, 0}, ColorRGBA.Yellow, ""))
                .add(builder.colored(new float[]{2, 1, 2}, new float[]{13, 0, 0}, ColorRGBA.Yellow, ""))
                .build(moduleManager);
        new ModuleSY(this, space, this.getClass().getResource("/levels/firstLevel.json").getPath())
                .add()
                .add()
                .add();
                //.build(moduleManager);
    }

    /**
     * This method takes care of everything that needs to be updated constantly, such as
     * referentials, timer, platforms movement and end module check
     * @param tpf time per frame
     */
    @Override
    public void simpleUpdate(float tpf) {
        /*
            Due to collision detection problem using ray casting,
            referentials are not used since it's impossible to play with sticky platforms
         */
        Referential.updateAll();
        if(!isPaused() && !raceFinished) {
            gameTimer.updateTimer(tpf);
        }
        moduleManager.getCurrentModule().reversePlatforms();
        moduleManager.checkNext(player.getBody().getPhysicsLocation());
    }

    private void loadAudio() {
        AudioNode node = new AudioNode(assetManager, "audio/sds_remix.wav", AudioData.DataType.Stream);
        /*
            When you run 100 times a day your game, it may sometimes be a little bit annoying to hear
            the same ten seconds of the same music constantly. Therefore the volume is set to 0
         */
        node.setVolume(0f);
        node.setLooping(true);
        node.setPositional(false);
        node.play();
        attachChilds(node);
    }
    private void disableDefaultOptions() {
        // disables FlyByCamera and mappings, replacing them by CameraSY and Setup
        inputManager.clearMappings();
        guiNode.detachAllChildren();
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
    }
    private void loadIcon() {
        BufferedImage[] images = new BufferedImage[2];
        try {
            images[0] = ImageIO.read(new File("src/main/resources/assets/icons/icon16.png"));
            images[1] = ImageIO.read(new File("src/main/resources/assets/icons/icon32.png"));
            settings.setIcons(images);
        } catch (IOException e) {
            LOGGER.error("Cannot load icon");
        }
    }
    public void setSkyEnabled(boolean enabled) {
        if(enabled) {
            rootNode.attachChild(sky);
        } else {
            rootNode.detachChild(sky);
            viewPort.setBackgroundColor(new ColorRGBA(0.5f, 0.6f, 0.7f, 1.0f));
        }
    }

    public void attachChilds(Spatial... spatials) { Arrays.asList(spatials).forEach(s -> rootNode.attachChild(s)); }
    public void attachLights(Light... lights) { Arrays.asList(lights).forEach(l -> rootNode.addLight(l)); }

    private boolean isPaused() {
        return inventory.isGuiActive();
    }
    public boolean hasStarted() { return started; }
    public void end() { raceFinished = true; }
    public Player getPlayer() { return player; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public CollisionManager getCollisionManager() { return collManager; }
    public PlayerInventory getPlayerInventory() { return inventory; }

    public DiscordRPCManager getRpcManager() {
        return rpcManager;
    }
}
