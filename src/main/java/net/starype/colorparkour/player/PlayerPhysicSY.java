package net.starype.colorparkour.player;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import net.starype.colorparkour.collision.CollisionManager;

public class PlayerPhysicSY implements PhysicsTickListener {

    private CollisionManager manager;
    private Camera cam;
    private Player player;
    private RigidBodyControl collisionBody;

    private Vector3f camForward, camLeft, walkDirection;
    public boolean left = false, right = false, forward = false, backward = false;
    private boolean cLeft, cRight, cForward, cBackward;
    private boolean redirect = false;

    private static final int TPF_COEFF_AVERAGE = 58;

    protected PlayerPhysicSY(CollisionManager manager, Camera cam, Player player) {

        this.manager = manager;
        this.cam = cam;
        this.player = player;
        camForward = new Vector3f();
        camLeft = new Vector3f();
        walkDirection = new Vector3f();
        assignBools();
        collisionBody = createBody();
    }

    private RigidBodyControl createBody() {
        RigidBodyControl body = manager.loadObject(CapsuleCollisionShape.class, 10, 1.5f,6f,1);
        body.setPhysicsLocation(cam.getLocation());
        body.setPhysicsRotation(cam.getRotation());
        body.setGravity(new Vector3f());
        return body;
    }

    protected void initListener() {

        manager.getAppState().getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {

        camForward.set(cam.getDirection()).multLocal(0.6f*tpf*TPF_COEFF_AVERAGE);
        camLeft.set(cam.getLeft().multLocal(0.4f*tpf*TPF_COEFF_AVERAGE));
        walkDirection.set(0, 0, 0);
        /*
            Here we set the value of walkDirection depending of the key pressed.
            if left, we use the left value, if right we inverse the left value, and so on
         */
        if (left) {
            walkDirection.addLocal(new Vector3f(camLeft.x,0,camLeft.z));
        }
        if (right) {
            walkDirection.addLocal(new Vector3f(-camLeft.x,0,-camLeft.z));
        }
        if (forward) {
            walkDirection.addLocal(new Vector3f(camForward.x,0,camForward.z));
        }

        if (backward) {
            walkDirection.addLocal(new Vector3f(-camForward.x,0,-camForward.z));
        }

        cam.setLocation(collisionBody.getPhysicsLocation());
        float speed = collisionBody.getLinearVelocity().length();
        boolean sameKeys = cBackward == backward
                && cForward == forward
                && cRight == right
                && cLeft == left;

        boolean noKeyPressed = !left && !right && !forward && !backward;

        if(noKeyPressed) {
            boolean shouldStop = speed < 0.25f;

            if (shouldStop) {
                collisionBody.setLinearVelocity(new Vector3f());
            } else {
                collisionBody.setLinearVelocity(collisionBody.getLinearVelocity().mult(0.9f * tpf * TPF_COEFF_AVERAGE));
            }
        }

        if(speed > 13 && !sameKeys)
            collisionBody.setLinearVelocity(walkDirection.mult(1/(speed/13)));
        else if(speed < 13 && sameKeys)
            collisionBody.applyCentralForce(walkDirection.mult(600*tpf*TPF_COEFF_AVERAGE));

        assignBools();
    }

    private void assignBools() {
        cLeft = left;
        cRight = right;
        cForward = forward;
        cBackward = backward;
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }
    /*
            if(noKeyPressed) {
            boolean shouldStop = speed < 0.25f;

            if(shouldStop) {
                collisionBody.setLinearVelocity(new Vector3f());
            } else {
                collisionBody.setLinearVelocity(collisionBody.getLinearVelocity().mult(0.9f*tpf*TPF_COEFF_AVERAGE));
            }
        }
        if(!sameKeys && !noKeyPressed) {
            redirect = true;
            collisionBody.setLinearVelocity(collisionBody.getLinearVelocity()
                    .mult(0.9f)
                    .add(walkDirection.mult(speed/8*tpf*62.5f)).mult(0.5f*tpf*TPF_COEFF_AVERAGE));
            if(speed < 10)
                redirect = false;
        }

        if(speed < 13 && !noKeyPressed) {
            collisionBody.applyCentralForce(walkDirection.mult(600*tpf*TPF_COEFF_AVERAGE));
        }

        if(!redirect)
            assignBools();
     */
}
