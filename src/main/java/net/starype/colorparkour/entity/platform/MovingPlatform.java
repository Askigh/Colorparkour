package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.collision.CollisionManager;

public class MovingPlatform extends ColoredPlatform {

    private Vector3f departure;
    private Vector3f arrival;
    private Vector3f initialDirection;
    private Vector3f direction;
    private float speed;

    public MovingPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z,
                          Vector3f departure, Vector3f arrival, float speed) {
        this(manager, main, x, y, z, ColorRGBA.White, departure, arrival, speed);
    }

    public MovingPlatform(CollisionManager manager, SimpleApplication main, float x, float y, float z,
                          ColorRGBA color, Vector3f departure, Vector3f arrival, float speed) {
        super(manager, main, x, y, z, departure.x, departure.y, departure.z, color);
        this.departure = departure;
        this.arrival = arrival;
        this.speed = speed;
        createMove();
    }

    private void createMove() {
        direction = new Vector3f(arrival.x - departure.x,
                arrival.y - departure.y, arrival.z -departure.z);
        this.initialDirection = direction;
        resetMovement();
    }
    public void resetMovement() {
        body.setLinearVelocity(direction.mult(speed));
    }

    public Vector3f getDirection() { return direction; }
    public void setDirection(Vector3f direction) { this.direction = direction; }
    public Vector3f getDeparture() { return departure; }
    public Vector3f getArrival() { return arrival; }
    public Vector3f getInitialDirection() { return initialDirection; }
}
