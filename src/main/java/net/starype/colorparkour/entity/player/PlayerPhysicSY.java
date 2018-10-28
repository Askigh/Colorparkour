package net.starype.colorparkour.entity.player;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.platform.ColoredPlatform;
import net.starype.colorparkour.entity.platform.DoubleJumpPlatform;
import net.starype.colorparkour.entity.platform.PlatformManager;
import net.starype.colorparkour.entity.platform.StickyMovingPlatform;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerPhysicSY implements PhysicsTickListener, PhysicsCollisionListener {

    private CollisionManager manager;
    private Camera cam;
    private Player player;
    private RigidBodyControl body;

    private Vector3f camForward, camLeft, walkDirection;
    public boolean left = false, right = false, forward = false, backward = false;
    private float speedBoost = 1f;

    private short jumpAmount = 0;
    private boolean inAir = false;
    private boolean jumpReset = false;

    private float low_speed_friction;
    private float acceleration;
    private float standard_friction;
    private float friction_expansion;
    private float jump_power;

    private PlatformManager platformManager;

    // In average, TPF_COEFF_AVERAGE * tpf = 1
    private static final int TPF_COEFF_AVERAGE = 58;

    protected PlayerPhysicSY(CollisionManager manager, Camera cam, Player player, PlatformManager platformManager) {
        this.platformManager = platformManager;
        this.manager = manager;
        this.cam = cam;
        this.player = player;
        this.body = createBody();
        camForward = new Vector3f();
        camLeft = new Vector3f();
        walkDirection = new Vector3f();
        loadDefaultValues();
    }

    private void loadDefaultValues() {
        low_speed_friction = -70f;
        standard_friction = -5;
        acceleration = 2500;
        friction_expansion = 1.3f;
        jump_power = 22f;
    }

    private RigidBodyControl createBody() {

        player.setAppearance(new Geometry("hit_box", new Box(1, 2, 1)));
        RigidBodyControl body = manager.loadObject(BoxCollisionShape.class, 20, player.getAppearance());
        body.setPhysicsLocation(cam.getLocation());
        body.setPhysicsRotation(cam.getRotation());
        body.setGravity(ColorParkourMain.GAME_GRAVITY);
        body.setFriction(0);

        player.setBody(body);
        return body;
    }

    protected void initListener() {
        manager.getAppState().getPhysicsSpace().addTickListener(this);
        manager.getAppState().getPhysicsSpace().addCollisionListener(this);
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        checkInAir();

        if (body.getPhysicsLocation().y < -60) {
            // TODO : Use the last check point location
            body.setPhysicsLocation(new Vector3f(0, 16, 0));
        }
        camForward.set(cam.getDirection()).multLocal(0.6f * tpf * TPF_COEFF_AVERAGE);
        camLeft.set(cam.getLeft().multLocal(0.4f * tpf * TPF_COEFF_AVERAGE));
        walkDirection.set(0, 0, 0);

        // We want the camera to be at the top of the body
        cam.setLocation(body.getPhysicsLocation().add(0, 1, 0));
        /*
            Here we set the value of walkDirection depending of the key pressed.
            if left, we use the left value, if right we inverse the left value, and so on
         */
        if (left) {
            walkDirection.addLocal(new Vector3f(camLeft.x, 0, camLeft.z));
        }
        if (right) {
            walkDirection.addLocal(new Vector3f(-camLeft.x, 0, -camLeft.z));
        }
        if (forward) {
            walkDirection.addLocal(new Vector3f(camForward.x, 0, camForward.z));
        }
        if (backward) {
            walkDirection.addLocal(new Vector3f(-camForward.x, 0, -camForward.z));
        }
        Vector3f spaceSpeed = body.getLinearVelocity();
        Vector2f flatSpeed = new Vector2f(spaceSpeed.x, spaceSpeed.z);
        float friction;
        float speedXZ = flatSpeed.length();

        if (speedXZ < 3 && speedXZ != 0)
            friction = low_speed_friction / speedXZ;
        else
            friction = standard_friction * (float) Math.pow(speedXZ, friction_expansion);

        Vector3f force = walkDirection.mult(acceleration * speedBoost)
                .add(new Vector3f(flatSpeed.x * friction, spaceSpeed.y, flatSpeed.y * friction));
        body.applyCentralForce(force);

        if (noKeyTouched() && speedXZ < 1.5f)
            body.setLinearVelocity(new Vector3f(0, spaceSpeed.y, 0));
    }

    private void checkInAir() {
        if (body.getLinearVelocity().y < 0) {
            inAir = true;
        }
    }

    public void jump() {
        // falling is considered as a jump
        if (jumpAmount - (inAir ? 1 : 0) <= 0)
            return;
        else {
            jumpAmount--;
        }

        Vector3f spaceSpeed = body.getLinearVelocity();
        /*
            Modification of the "classic" physics. If the body is falling, we still want
            the jump to propulse the player upward
        */
        float y = spaceSpeed.y > 0 ? spaceSpeed.y : spaceSpeed.y / 2.5f;
        body.setLinearVelocity(new Vector3f(spaceSpeed.x, y + jump_power, spaceSpeed.z));
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                jumpReset = false;
            }
        }, 30);
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA().equals(player.getAppearance()) || event.getNodeB().equals(player.getAppearance())) {
            inAir = false;

            ColoredPlatform platform;
            if (platformManager.getPlatformBySpatial(event.getNodeA()) != null) {
                platform = platformManager.getPlatformBySpatial(event.getNodeA());
            } else if (platformManager.getPlatformBySpatial(event.getNodeB()) != null) {
                platform = platformManager.getPlatformBySpatial(event.getNodeB());
            } else {
                return;
            }

            if (!jumpReset) {
                jumpAmount = (short) (platform instanceof DoubleJumpPlatform ? 2 : 1);
                jumpReset = true;

                if(platform instanceof StickyMovingPlatform) {
                    System.out.println("speed");
                    ((StickyMovingPlatform) platform).stick(body);
                }
            }
        }
    }

    private boolean noKeyTouched() {
        return !right && !left && !forward && !backward;
    }

    public PlayerPhysicSY setLowSpeedFriction(float low_speed_friction) {
        this.low_speed_friction = low_speed_friction;
        return this;
    }

    public PlayerPhysicSY setAcceleration(float acceleration) {
        this.acceleration = acceleration;
        return this;
    }

    public PlayerPhysicSY setStandardFriction(float standard_friction) {
        this.standard_friction = standard_friction;
        return this;
    }

    public PlayerPhysicSY setFrictionExpansion(float friction_expansion) {
        this.friction_expansion = friction_expansion;
        return this;
    }

    public PlayerPhysicSY setJumpPower(float jump_power) {
        this.jump_power = jump_power;
        return this;
    }

    public float getLow_speed_friction() {
        return low_speed_friction;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getStandard_friction() {
        return standard_friction;
    }

    public float getFriction_expansion() {
        return friction_expansion;
    }

    public float getJump_power() {
        return jump_power;
    }

    public void sprint() {
        this.speedBoost = 2.7f;
    }

    public void walk() {
        this.speedBoost = 1f;
    }

    // non used overrided method
    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
    }
}
