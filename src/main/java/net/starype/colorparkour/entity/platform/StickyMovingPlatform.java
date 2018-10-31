package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.entity.player.PlayerPhysicSY;

public class StickyMovingPlatform extends MovingPlatform {

    private boolean playerInContact;

    public StickyMovingPlatform(CollisionManager manager, SimpleApplication main, float[] size, Vector3f departure,
                                Vector3f arrival, float speed, ColorRGBA color, String platformID) {
        super(manager, main, size, departure, arrival, speed, color, platformID);
        body.setFriction(1);
    }

    public void stick(PlayerPhysicSY physics, boolean multiply) {
        /*Vector3f spaceSpeed = physics.getBody().getLinearVelocity();
        physics.getBody().setLinearVelocity(spaceSpeed.add(direction.mult(speed * (multiply ? 2 : 1))));
        physics.getBody().applyImpulse(spaceSpeed.add(direction.mult(speed * (multiply ? 2 : 1))),physics.getBody().getPhysicsLocation());
        LOGGER.info(""+direction.mult(speed* (multiply ? 2 : 1)));
        playerInContact = true;*/
    }

    public void stick(PlayerPhysicSY physics) {
        stick(physics, false);
    }

    public boolean isPlayerInContact() {
        return playerInContact;
    }

    public void setPlayerInContact(boolean playerInContact) {
        this.playerInContact = playerInContact;
    }
}
