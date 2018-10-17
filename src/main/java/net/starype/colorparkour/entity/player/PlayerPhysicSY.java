package net.starype.colorparkour.entity.player;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import net.starype.colorparkour.collision.CollisionManager;

public class PlayerPhysicSY implements PhysicsTickListener {

    private CollisionManager manager;
    private Camera cam;
    private Player player;
    private RigidBodyControl body;

    private Vector3f camForward, camLeft, walkDirection, previous;
    public boolean left = false, right = false, forward = false, backward = false;

    private float low_speed_friction;
    private float acceleration;
    private float standard_friction;
    private float friction_expansion;

    private static final int TPF_COEFF_AVERAGE = 58;

    protected PlayerPhysicSY(CollisionManager manager, Camera cam, Player player) {

        this.manager = manager;
        this.cam = cam;
        this.player = player;
        camForward = new Vector3f();
        camLeft = new Vector3f();
        walkDirection = new Vector3f();
        previous = new Vector3f();
        this.body = createBody();
        loadDefaultValues();
    }
    private void loadDefaultValues() {
        low_speed_friction = -70f;
        standard_friction = -5;
        acceleration = 2500;
        friction_expansion = 1.3f;
    }
    private RigidBodyControl createBody() {

        RigidBodyControl body = manager.loadObject(ConeCollisionShape.class, 20, 1.6f,6f,1);
        body.setPhysicsLocation(cam.getLocation());
        body.setPhysicsRotation(cam.getRotation());
        body.setGravity(new Vector3f(0, -22, 0));
        player.setBody(body);
        return body;
    }

    protected void initListener() {
        manager.getAppState().getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {

        camForward.set(cam.getDirection()).multLocal(0.6f*tpf*TPF_COEFF_AVERAGE);
        camLeft.set(cam.getLeft().multLocal(0.4f*tpf*TPF_COEFF_AVERAGE));
        previous.set(walkDirection);
        walkDirection.set(0, 0, 0);

        // We want the camera to be at the top of the body
        cam.setLocation(body.getPhysicsLocation().add(0,1,0));
        /*
            Here we set the value of walkDirection depending of the key pressed.
            if left, we use the left value, if right we inverse the left value, and so on
         */
        if (left) {
            walkDirection.addLocal(new Vector3f(camLeft.x,0, camLeft.z));
        }
        if (right) {
            walkDirection.addLocal(new Vector3f(-camLeft.x,0, -camLeft.z));
        }
        if (forward) {
            walkDirection.addLocal(new Vector3f(camForward.x,0, camForward.z));
        }
        if (backward) {
            walkDirection.addLocal(new Vector3f(-camForward.x,0, -camForward.z));
        }

        Vector3f spaceSpeed = body.getLinearVelocity();
        Vector2f flatSpeed = new Vector2f(spaceSpeed.x, spaceSpeed.z);
        float friction;
        float speedXZ = flatSpeed.length();

        if(speedXZ < 3 && speedXZ != 0)
            friction = low_speed_friction / speedXZ;
        else
            friction = standard_friction * (float) Math.pow(speedXZ, friction_expansion);

        Vector3f force = walkDirection.mult(acceleration)
                .add(new Vector3f(flatSpeed.x * friction, spaceSpeed.y, flatSpeed.y * friction));
        body.applyCentralForce(force);

        if(noKeyTouched() && speedXZ < 1)
            body.setLinearVelocity(new Vector3f(0,spaceSpeed.y,0));
    }

    private boolean noKeyTouched() {
        return !right && !left && !forward && !backward;
    }

    public void setLowSpeedFriction(float low_speed_friction) { this.low_speed_friction = low_speed_friction; }
    public void setAcceleration(float acceleration) { this.acceleration = acceleration; }
    public void setStandardFriction(float standard_friction) { this.standard_friction = standard_friction; }
    public void setFrictionExpansion(float friction_expansion) { this.friction_expansion = friction_expansion; }

    // non used overrided method
    @Override
    public void physicsTick(PhysicsSpace space, float tpf) { }
}
