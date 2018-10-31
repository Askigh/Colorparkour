package net.starype.colorparkour.utils;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.entity.platform.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformBuilder {

    public static final Logger LOGGER = LoggerFactory.getLogger(PlatformBuilder.class);
    private CollisionManager manager;
    private SimpleApplication main;

    public PlatformBuilder(CollisionManager manager, SimpleApplication main) {
        LOGGER.info("Initializing PlatformBuilder");
        this.manager = manager;
        this.main = main;
    }

    public ColoredPlatform colored(float[] size, float[] position, ColorRGBA color, String platformID) {
        LOGGER.debug("Created a colored platform.");
        return new ColoredPlatform(manager, main, size, position, color, platformID);
    }

    public ColoredPlatform ice(float[] size, float[] position, ColorRGBA color, String platformID) {
        LOGGER.debug("Created an ice platform.");
        return new IcePlatform(manager, main, size, position, color, platformID);
    }

    public ColoredPlatform doubleJump(float[] size, float[] position, ColorRGBA color, String platformID) {
        LOGGER.debug("Created a double jump platform.");
        return new DoubleJumpPlatform(manager, main, size, position, color, platformID);
    }

    public ColoredPlatform moving(float[] size, ColorRGBA color, Vector3f departure, Vector3f arrival, float speed, String platformID) {
        LOGGER.debug("Created a moving platform.");
        return new MovingPlatform(manager, main, size, departure, arrival, speed, color, platformID);
    }

    public ColoredPlatform sticky(float[] size, Vector3f departure, Vector3f arrival, float speed,
                                  ColorRGBA color, String platformID) {
        LOGGER.debug("Created a stikcy platform.");
        return new StickyMovingPlatform(manager, main, size, departure, arrival, speed, color, platformID);
    }
}
