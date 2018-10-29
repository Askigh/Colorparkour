package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.collision.CollisionManager;

public class IcePlatform extends ColoredPlatform {

    public static final float FRICTION_COEFF = 3f;

    public IcePlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z, float posX,
                       float posY, float posZ, String platformID) {
        this(manager, main, x, y, z, posX, posY, posZ, ColorRGBA.White, platformID);
    }

    public IcePlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z, float posX,
                       float posY, float posZ, ColorRGBA color, String platformID) {
        super(manager, main, x, y, z, new Vector3f(posX, posY, posZ), color, platformID);
    }
}
