package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import net.starype.colorparkour.collision.CollisionManager;

import java.util.ArrayList;
import java.util.List;

public class PlatformManager {

    private List<ColoredPlatform> platforms = new ArrayList<>();
    private CollisionManager manager;
    private SimpleApplication main;

    public PlatformManager(CollisionManager manager, SimpleApplication main) {
        this.manager = manager;
        this.main = main;
    }

    public PlatformManager addColored(float x, float y, float z, float posX, float posY, float posZ, ColorRGBA color) {
        platforms.add(new ColoredPlatform(manager, main, x, y, z, posX, posY, posZ, color));
        return this;
    }
    public PlatformManager addDoubleJump(float x, float y, float z, float posX, float posY, float posZ, ColorRGBA color) {
        platforms.add(new DoubleJumpPlatform(manager, main, x, y, z, posX, posY, posZ, color));
        return this;
    }
    public ColoredPlatform getPlatformByBody(RigidBodyControl body) {
        for(ColoredPlatform plat : platforms) {
            if(plat.getBody().equals(body))
                return plat;
        }
        return null;
    }
    public ColoredPlatform getPlatformBySpatial(Spatial spatial) {
        for(ColoredPlatform plat : platforms) {
            if(plat.getAppearance().equals(spatial))
                return plat;
        }
        return null;
    }
    public List<ColoredPlatform> getPlatforms() {return platforms; }
}
