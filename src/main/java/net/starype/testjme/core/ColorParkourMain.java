package net.starype.testjme.core;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import net.starype.testjme.collision.CollisionManager;
import net.starype.testjme.settings.Setup;

import java.util.Arrays;

public class ColorParkourMain extends SimpleApplication {

    private CollisionManager collManager;
    private Vector3f walkDirection;
    private Vector3f camFor;
    private Vector3f camLeft;
    private CharacterControl player;

    public boolean left = false, right = false, forward = false, down = false;

    public static void main(String[] args) {

        ColorParkourMain main = new ColorParkourMain();
        main.initSettings();
    }

    private ColorParkourMain() {

        collManager = new CollisionManager(this);
        walkDirection = new Vector3f();
        camFor = new Vector3f();
        camLeft = new Vector3f();
    }

    private void initSettings() {

        this.setSettings(new AppSettings(true));
        this.setShowSettings(false);
        this.start();
    }

    @Override
    public void simpleInitApp() {
        collManager.init();

        /*
            Init keyboard inputs and light sources
         */
        Setup setup = new Setup(this);
        setup.init();


        /*
            Set up the camera
         */
        flyCam.setMoveSpeed(50);
        Quaternion q = new Quaternion();
        q.set(0,0,0,1);
        cam.setRotation(q);

        Spatial cube = loadCube();
        Spatial ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");

        /*
            Important !
            spatials' data must be modified before the collision object is created
         */
        ninja.setLocalTranslation(5, 5, 5);
        ninja.setLocalScale(0.04f);

        player = collManager.createPlayer(1.5f,6,20,30);
        collManager.addCollisionShapeObject(cube);
        collManager.addCollisionShapeObject(ninja);

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

        camFor.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft().multLocal(0.4f));
        walkDirection.set(0, 0, 0);

        /*
            Here we set the value of walkDirection depending of the key pressed.
            if left, we use the left value, if right we inverse the left value, and so on
         */
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (forward) {
            walkDirection.addLocal(camFor);
        }
        if (down) {
            walkDirection.addLocal(camFor.negate());
        }

        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
        System.out.println(player.getPhysicsLocation());
    }

    public void attachChild(Spatial... spatials) {

        Arrays.asList(spatials).forEach(s -> rootNode.attachChild(s));
    }

    public void attachLights(Light... lights) {

        Arrays.asList(lights).forEach(l -> rootNode.addLight(l));
    }

    public CharacterControl getPlayer() { return player; }
}
