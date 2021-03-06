package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import net.starype.colorparkour.utils.CollisionManager;
import net.starype.colorparkour.entity.platform.event.ContactEvent;
import net.starype.colorparkour.entity.platform.event.LoadEvent;
import net.starype.colorparkour.entity.player.PlayerPhysicSY;

public class IcePlatform extends ColoredPlatform implements ContactEvent, LoadEvent {

    public IcePlatform(CollisionManager manager, SimpleApplication main, float[] size, float[] position, ColorRGBA color, String platformID) {
        super(manager, main, size, position, color, platformID);
        setTexture("ice.png");
    }

    @Override
    public void collided(PlayerPhysicSY physicSY) {
        physicSY.getBody().setFriction(0);
        body.setFriction(0);
    }

    @Override
    public void leaveByJump(PlayerPhysicSY physicSY) {
        physicSY.getBody().setFriction(1);
        body.setFriction(1);
    }

    @Override
    public void load() {
        body.setFriction(0);
    }
}
