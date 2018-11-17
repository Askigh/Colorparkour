package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import net.starype.colorparkour.utils.CollisionManager;
import net.starype.colorparkour.entity.platform.event.ContactEvent;
import net.starype.colorparkour.entity.player.PlayerPhysicSY;

public class DoubleJumpPlatform extends ColoredPlatform implements ContactEvent {

    public DoubleJumpPlatform(CollisionManager manager, SimpleApplication main, float[] size, float[] position, ColorRGBA color, String platformID) {
        super(manager, main, size, position, color, platformID);
    }

    @Override
    public void leave(PlayerPhysicSY physicSY) {
        physicSY.addBonus();
    }
}
