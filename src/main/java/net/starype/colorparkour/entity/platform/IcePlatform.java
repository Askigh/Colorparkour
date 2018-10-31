package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import net.starype.colorparkour.collision.CollisionManager;

public class IcePlatform extends ColoredPlatform {

    public static final float FRICTION_COEFF = 3f;

    public IcePlatform(CollisionManager manager, SimpleApplication main, float[] size, float[] position, ColorRGBA color, String platformID) {
        super(manager, main, size, position, color, platformID);
    }
}
