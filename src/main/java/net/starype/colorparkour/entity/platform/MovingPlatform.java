package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.entity.platform.event.LoadEvent;

public class MovingPlatform extends ColoredPlatform implements LoadEvent {

    protected Vector3f departure;
    protected Vector3f arrival;
    protected Vector3f initialDirection;
    protected Vector3f direction;
    protected float speed;

    public MovingPlatform(CollisionManager manager, SimpleApplication main, float[] size, Vector3f departure, Vector3f arrival,
                          float speed, ColorRGBA color, String platformID) {
        super(manager, main, size, departure, color, platformID);
        this.departure = departure;
        this.arrival = arrival;
        this.speed = speed;
    }
    @Override
    public void load() {
        createMove();
        body.setFriction(0);
    }

    private void createMove() {
        direction = new Vector3f(arrival.x - departure.x,
                arrival.y - departure.y, arrival.z - departure.z);
        this.initialDirection = direction;
        resetMovement();
    }

    public void resetMovement() {
        body.setLinearVelocity(direction.mult(speed));
    }
    public Vector3f getDirection() {
        return direction;
    }
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }
    public Vector3f getDeparture() {
        return departure;
    }
    public Vector3f getArrival() {
        return arrival;
    }
    public Vector3f getInitialDirection() {
        return initialDirection;
    }

    @Override
    public Vector3f getPosition() {
        if(body == null) {
            return super.getPosition();
        } else {
            return body.getPhysicsLocation();
        }
    }
}
