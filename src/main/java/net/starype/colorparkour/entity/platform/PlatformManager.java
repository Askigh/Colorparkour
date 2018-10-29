package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import net.starype.colorparkour.collision.CollisionManager;

import java.util.ArrayList;
import java.util.List;

public class PlatformManager {

    private List<ColoredPlatform> platforms = new ArrayList<>();
    private CollisionManager manager;
    private SimpleApplication main;
    private RigidBodyControl body;

    public PlatformManager(CollisionManager manager, SimpleApplication main) {
        this.manager = manager;
        this.main = main;
    }

    public void attachBody(RigidBodyControl body) { this.body = body; }

    public ColoredPlatform colored(float x, float y, float z, float posX, float posY, float posZ, ColorRGBA color) {
        return new ColoredPlatform(manager, main, x, y, z, posX, posY, posZ, color);
    }

    public ColoredPlatform ice(float x, float y, float z, float posX, float posY, float posZ, ColorRGBA color) {
        return new IcePlatform(manager, main, x, y, z, posX, posY, posZ, color);
    }
    public ColoredPlatform doubleJump(float x, float y, float z, float posX, float posY, float posZ, ColorRGBA color) {
        return new DoubleJumpPlatform(manager, main, x, y, z, posX, posY, posZ, color);
    }
    public ColoredPlatform moving(float x, float y, float z, ColorRGBA color, Vector3f departure, Vector3f arrival, float speed) {
        return new MovingPlatform(manager, main, x, y, z, color, departure, arrival, speed);
    }
    public ColoredPlatform sticky(float x, float y, float z, Vector3f departure, Vector3f arrival, float speed,
                                  ColorRGBA color) {
        return new StickyMovingPlatform(manager, main, x, y, z, color, departure, arrival, speed);
    }
    public ColoredPlatform getPlatformBySpatial(Spatial spatial) {
        for(ColoredPlatform plat : platforms) {
            if(plat.getAppearance().equals(spatial))
                return plat;
        }
        return null;
    }
    public void reversePlatforms() {
        for(ColoredPlatform plat : platforms) {
            if(plat instanceof MovingPlatform) {
                MovingPlatform movPlat = (MovingPlatform) plat;
                Vector3f position = movPlat.getPosition();
                float distanceDep = position.add(movPlat.getDeparture().mult(-1)).length();
                float distanceArr = position.add(movPlat.getArrival().mult(-1)).length();

                boolean closeToArr = distanceArr < 0.5f && movPlat.getDirection().equals(movPlat.getInitialDirection());
                boolean closeToDep = distanceDep < 0.5f && movPlat.getDirection().mult(-1)
                        .equals(movPlat.getInitialDirection());
                if(closeToArr || closeToDep) {
                    movPlat.setDirection(movPlat.getDirection().mult(-1));
                    movPlat.resetMovement();
                }
            }
        }
    }
    public List<ColoredPlatform> getPlatforms() {return platforms; }
}
