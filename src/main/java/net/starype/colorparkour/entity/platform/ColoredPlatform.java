package net.starype.colorparkour.entity.platform;

import com.google.gson.annotations.SerializedName;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import net.starype.colorparkour.utils.CollisionManager;
import net.starype.colorparkour.entity.PhysicalEntity;
import net.starype.colorparkour.entity.platform.event.LoadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColoredPlatform extends PhysicalEntity {

    public static final Logger LOGGER = LoggerFactory.getLogger(ColoredPlatform.class);

    @SerializedName("rgba")
    private Vector3f position;
    private ColorRGBA color;
    private Material mat;
    @SerializedName("name")
    private final String platformID;
    @SerializedName("size")
    private Vector3f size;
    private String texture = "black.png";

    public ColoredPlatform(CollisionManager manager, SimpleApplication main, float[] size, float[] position, ColorRGBA color, String platformID) {
        this(manager, main, size, new Vector3f(position[0], position[1], position[2]), color, platformID);
    }

    public ColoredPlatform(CollisionManager manager, SimpleApplication main, float[] size, Vector3f pos,
                           ColorRGBA color, String platformID) {
        super(manager, main);
        this.position = pos;
        this.color = color;
        this.size = new Vector3f(size[0], size[1], size[2]);
        this.platformID = platformID;
    }

    /**
     * Default body of all platforms. When special modifications needs to be done, they must
     * be added in a {@link LoadEvent#load()} method.
     * @param sizeX the x size of the platform
     * @param sizeY the y size of the platform
     * @param sizeZ the z size of the platform
     * @param pos the default position of the platform. Constant if the platform is not a moving platform
     * @param color
     */
    public void loadBody(float sizeX, float sizeY, float sizeZ, Vector3f pos, ColorRGBA color) {

        Box box = new Box(sizeX, sizeY, sizeZ);
        Box hitBox = new Box(sizeX + 0.1f, sizeY + 1, sizeZ + 0.1f);
        appearance = new Geometry("box", box);
        mat = loadMaterial(color);
        appearance.setMaterial(mat);

        int mass = this instanceof MovingPlatform ? Integer.MAX_VALUE : 0;
        body = manager.loadObject(BoxCollisionShape.class, mass, false, new Geometry("hitbox", hitBox));

        appearance.setLocalTranslation(pos);
        body.setPhysicsLocation(pos.add(0, -1.1f, 0));

        super.addInPhysicsSpace();
        body.setGravity(new Vector3f());

        ParticleEmitter fire =
                new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 100);
        Material mat_red = new Material(main.getAssetManager(),
                "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", main.getAssetManager().loadTexture(
                "assets/particles/fire.png"));
        mat_red.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        fire.setMaterial(mat_red);
        fire.setImagesX(2);
        fire.setImagesY(2);
        fire.setStartSize(0.1f);
        fire.setEndSize(0.1f);
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 1, 0));
        fire.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f));
        fire.setStartColor(new ColorRGBA(1f, 0f, 0f, 1f));
        fire.setShape(new EmitterBoxShape(new Vector3f(-5f,0f,-5f),new Vector3f(5f,0f,5f)));
        fire.setLocalTranslation(pos.add(0, 0.7f, 0));
        fire.setRotateSpeed(4);
        fire.setGravity(0, 0, 0);
        fire.setLowLife(1f);
        fire.setHighLife(3f);
        fire.getParticleInfluencer().setVelocityVariation(0.6f);
        main.getRootNode().attachChild(fire);
    }

    /**
     * Method called when the body is currently loading, and when we want the color to change, for instance
     * when we make it brighter in order to show that the platform is not solid
     * @param color the color of the platform
     * @return the material created from the color, using Unshaded.j3md
     */
    private Material loadMaterial(ColorRGBA color) {
        Material mat = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        mat.setTexture("ColorMap", main.getAssetManager().loadTexture("assets/platforms/"+texture));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return mat;
    }

    /**
     * Displays the platform with full properties : appearance, movement if necessary and physics body
     */
    public void show() {
        main.getRootNode().attachChild(appearance);
        body.setEnabled(true);
        body.setGravity(new Vector3f());
        appearance.setMaterial(loadMaterial(color));
    }

    /**
     * Remove the solidity of the platform
     * @param invisible Whether the platform should be completely removed or not
     */
    public void hide(boolean invisible) {
        if (invisible) {
            // Called only when the module has been finished, the body will be totally invisible
            main.getRootNode().detachChild(appearance);
        } else {
            // Called when we just want to inform the player that the platform is not solid anymore
            appearance.setMaterial(loadMaterial(color.add(new ColorRGBA(0.9f, 0.9f, 0.9f, 0))));
        }
        // In any case we don't want the body to be solid anymore
        body.setEnabled(false);
    }

    @Override
    public Vector3f getPosition() { return position; }
    public String getPlatformID() { return platformID; }
    public ColorRGBA getColor() { return color; }
    public Vector3f getSize() { return size; }
    public String getTexture() { return texture; }
    public void setTexture(String texture) { this.texture = texture; }
}
