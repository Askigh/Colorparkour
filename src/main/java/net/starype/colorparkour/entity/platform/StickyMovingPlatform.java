package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.collision.CollisionManager;

public class StickyMovingPlatform extends MovingPlatform {

    public StickyMovingPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z,
                                Vector3f departure, Vector3f arrival, float speed) {
        this(manager, main, x, y, z, ColorRGBA.White, departure, arrival, speed);
    }

    public StickyMovingPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z,
                                ColorRGBA color, Vector3f departure, Vector3f arrival, float speed) {
        super(manager, main, x, y, z, color, departure, arrival, speed);
    }

    public void stick(RigidBodyControl body) {
        body.setLinearVelocity(body.getLinearVelocity().add(direction.mult(speed)));
    }
}
