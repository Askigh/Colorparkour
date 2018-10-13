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
import net.starype.colorparkour.utils.VectorUtils;

import java.util.Arrays;

public class ColorParkourMain extends SimpleApplication {

    private CollisionManager collManager;
    private Player player;

   // public boolean left = false, right = false, forward = false, down = false;

    public static void main(String[] args) {

        new ColorParkourMain();
    }

    private ColorParkourMain() {

        collManager = new CollisionManager(this);
        setSettings(new AppSettings(true));
        setShowSettings(false);
        start();
    }

    @Override
    public void simpleInitApp() {

        disableDefaultOptions();
        collManager.init();

        player = new Player(this, cam, collManager);
        player.initialize();

        /*
            Init keyboard inputs and light sources
         */
        Setup setup = new Setup(this);
        setup.init();

        Spatial cube = loadCube();
        cube.setLocalTranslation(0,0,5);
        Spatial ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");

        /*
            Important !
            spatials' datas must be modified before the collision object is created
         */
        ninja.setLocalTranslation(5, 5, 5);
        ninja.setLocalScale(0.04f);

        collManager.loadObject(CollisionShape.class, 0, cube);
        collManager.loadObject(CollisionShape.class, 0, ninja);

        attachChild(cube, ninja);
    }


    private Spatial loadCube() {

        Box box = new Box(1, 1, 1);
        Geometry geometry = new Geometry("box", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture t = assetManager.loadTexture("/assets/Textures/logo.png");
        mat.setTexture("ColorMap", t);

        geometry.setMaterial(mat);

        return geometry;
    }


    @Override
    public void simpleUpdate(float tpf) {
    }

    public void attachChild(Spatial... spatials) {

        Arrays.asList(spatials).forEach(s -> rootNode.attachChild(s));
    }

    public void attachLights(Light... lights) {

        Arrays.asList(lights).forEach(l -> rootNode.addLight(l));
    }

    private void disableDefaultOptions(){

        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        inputManager.setCursorVisible(false);
    }

    public Player getPlayer(){ return player; }

}
