package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.entity.platform.event.ContactEvent;
import net.starype.colorparkour.entity.player.PlayerPhysicSY;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugPlatform extends ColoredPlatform implements ContactEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugPlatform.class);
    public DebugPlatform(CollisionManager manager, SimpleApplication main, float[] size, Vector3f pos, ColorRGBA color, String platformID) {
        super(manager, main, size, pos, color, platformID);
    }

    @Override
    public void collided(PlayerPhysicSY physicSY) {
        LOGGER.info("A collision has occured");
    }

    @Override
    public void leaveByJump(PlayerPhysicSY physicSY) {
        LOGGER.info("Left the platform");
    }
}
