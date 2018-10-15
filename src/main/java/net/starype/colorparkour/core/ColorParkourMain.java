package net.starype.colorparkour.core;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.light.Light;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.entity.platform.StandardPlatform;
import net.starype.colorparkour.entity.player.Player;
import net.starype.colorparkour.settings.Setup;
import net.starype.colorparkour.utils.ShapesSY;

import java.util.Arrays;

public class ColorParkourMain extends SimpleApplication {

    private CollisionManager collManager;
    private Player player;
    private StandardPlatform plat;
    public static final float GAME_GRAVITY = -400f;

    public static void main(String[] args) { new ColorParkourMain(); }

    private ColorParkourMain() {

        setSettings(new AppSettings(true));
        // disables the default window that asks for settings
        setShowSettings(false);
        super.start();
    }

    @Override
    public void simpleInitApp() {

        disableDefaultOptions();
        collManager = new CollisionManager(this);
        collManager.init();

        player = new Player(this, cam, collManager);
        player.initialize();

        plat = new StandardPlatform(collManager, this, 5,1,5, new Vector3f(7,-1,7));

        /*
            Init keyboard inputs and light sources
         */
        Setup.init(this);

        //Spatial cube = ShapesSY.loadCube(assetManager);

        /* TODO: Change CollisionShape by the appropriated shape for a cube
           We set the mass to 0 in order to create static physics objects
         */
        //collManager.loadObject(CollisionShape.class, 0, cube);

        //attachChild(cube);
        Vector3f initial = new Vector3f(7,10,7);
        cam.setLocation(initial);
        player.setPosition(initial);
    }

    public void attachChild(Spatial... spatials) { Arrays.asList(spatials).forEach(s -> rootNode.attachChild(s)); }

    public void attachLights(Light... lights) { Arrays.asList(lights).forEach(l -> rootNode.addLight(l)); }

    private void disableDefaultOptions(){

        // disable FlyByCamera, replaced by CameraSY
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        inputManager.setCursorVisible(false);
    }

    public Player getPlayer(){ return player; }
}
