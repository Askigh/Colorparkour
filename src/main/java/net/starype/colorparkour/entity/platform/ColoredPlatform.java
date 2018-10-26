package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.collision.CollisionManager;

public class ColoredPlatform extends StandardPlatform {

    private PlatformColor color;

    public ColoredPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z, float posX,
                            float posY, float posZ, PlatformColor color) {
        this(manager, main, x, y, z, new Vector3f(posX, posY, posZ), color);
    }

    public ColoredPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z, Vector3f pos,
                           PlatformColor color) {
        super(manager, main, x, y, z, pos);
    }

    public enum PlatformColor {
        RED, YELLOW, GREEN, BLUE
    }
}
