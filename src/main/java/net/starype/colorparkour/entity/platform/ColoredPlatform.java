package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.entity.PhysicEntity;

public class ColoredPlatform extends PhysicEntity {

    private ColorRGBA color;
    private Material mat;

    public ColoredPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z, float posX,
                            float posY, float posZ, ColorRGBA color) {
        this(manager, main, x, y, z, new Vector3f(posX, posY, posZ), color);
    }

    public ColoredPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z, Vector3f pos,
                           ColorRGBA color) {
        super(manager, main);
        this.color = color;
        loadBody(x, y, z, pos, color);
    }
    protected void loadBody(float x, float y, float z, Vector3f pos, ColorRGBA color) {

        Box box = new Box(x,y,z);
        appearance = new Geometry("box", box);
        mat = loadMaterial(color);
        appearance.setMaterial(mat);
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
        return mat;
    }

    public ColorRGBA getColor() {
        return color;
    }
    public void setColor(ColorRGBA color) {
        this.color = color;
        mat.setColor("Color", color);
    }
}
