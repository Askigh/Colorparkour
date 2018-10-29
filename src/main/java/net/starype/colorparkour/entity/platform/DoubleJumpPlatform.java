package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import net.starype.colorparkour.collision.CollisionManager;

public class DoubleJumpPlatform extends ColoredPlatform {

    public DoubleJumpPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z,
                              float posX, float posY, float posZ, String platformID) {
        this(manager, main, x, y, z, posX, posY, posZ, ColorRGBA.White, platformID);
    }
    public DoubleJumpPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z,
                              float posX, float posY, float posZ, ColorRGBA color, String platformID) {
        super(manager, main, x, y, z, posX, posY, posZ, color, platformID);
    }
}
