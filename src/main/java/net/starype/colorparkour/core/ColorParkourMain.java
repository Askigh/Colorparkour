package net.starype.colorparkour.core;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.player.Player;
import net.starype.colorparkour.settings.Setup;
import net.starype.colorparkour.utils.ShapesSY;
import net.starype.colorparkour.utils.VectorUtils;

import java.util.Arrays;

public class ColorParkourMain extends SimpleApplication {

    private CollisionManager collManager;
    private Player player;

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

        /*
            Init keyboard inputs and light sources
         */
        Setup.init(this);

        Spatial cube = ShapesSY.loadCube(assetManager);
        Spatial ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");

        /*
            Important !
            We must change these datas before creating collision objects
         */
        ninja.setLocalTranslation(5, 5, 5);
        ninja.setLocalScale(0.04f);

        /* TODO: Change CollisionShape by the appropriated shape for a cube
           We set the mass to 0 in order to create static physics objects
         */
        collManager.loadObject(CollisionShape.class, 0, cube);
        collManager.loadObject(CollisionShape.class, 0, ninja);

        attachChild(cube, ninja);
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
