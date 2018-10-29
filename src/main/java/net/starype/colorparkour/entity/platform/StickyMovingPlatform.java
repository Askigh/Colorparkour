package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.entity.player.PlayerPhysicSY;

public class StickyMovingPlatform extends MovingPlatform {


    public StickyMovingPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z,
                                Vector3f departure, Vector3f arrival, float speed, String platformID) {
        this(manager, main, x, y, z, ColorRGBA.White, departure, arrival, speed, platformID);
    }

    public StickyMovingPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z,
                                ColorRGBA color, Vector3f departure, Vector3f arrival, float speed, String platformID) {
        super(manager, main, x, y, z, color, departure, arrival, speed, platformID);
    }

    public void stick(PlayerPhysicSY physics) {
        LOGGER.info(physics.getForce()+"");
        physics.getForce().set(physics.getForce().add(direction.mult(10)));
        LOGGER.info("Sticking player "+direction.mult(speed)+" "+physics.getForce()+" "+body.getLinearVelocity());
    }
}
