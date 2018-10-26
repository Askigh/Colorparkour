package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.entity.PhysicEntity;

public class StandardPlatform extends PhysicEntity {

    public StandardPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z, float posX,
                            float posY, float posZ) {
        this(manager, main, x, y, z, new Vector3f(posX, posY, posZ));
    }
    public StandardPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z, Vector3f pos) {
        this(manager, main, x, y, z, pos, ColorRGBA.White);
    }
    protected StandardPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z,
                            Vector3f pos, ColorRGBA color) {
        super(manager, main);
        loadBody(x,y,z,pos, color);
    }

    protected void loadBody(float x, float y, float z, Vector3f pos, ColorRGBA color) {

        Box box = new Box(x,y,z);
        appearance = new Geometry("box", box);
        appearance.setMaterial(loadMaterial(color));
        ((Geometry) appearance).getMesh().scaleTextureCoordinates(new Vector2f(x,z));
        main.getRootNode().attachChild(appearance);
        body = manager.loadObject(BoxCollisionShape.class, 0, appearance);

        appearance.setLocalTranslation(pos);
        body.setPhysicsLocation(pos);
        body.setRestitution(0);
        super.addInPhysicsSpace();
    }

    private Material loadMaterial(ColorRGBA color) {
        Material mat = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        Texture t = main.getAssetManager().loadTexture("/assets/Textures/logo.png");
        t.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", t);
        return mat;
    }
}
