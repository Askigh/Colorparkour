package net.starype.colorparkour.entity.player;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.core.module.ModuleSY;
import net.starype.colorparkour.core.module.ModuleManager;
import net.starype.colorparkour.entity.platform.*;
import net.starype.colorparkour.entity.platform.event.ContactEvent;
import net.starype.colorparkour.utils.Referential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerPhysicSY implements PhysicsTickListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(PlayerPhysicSY.class);

    private CollisionManager manager;
    private ColorParkourMain main;
    private Camera cam;
    private Player player;
    private RigidBodyControl body;
    private ColoredPlatform lastContact;

    private final Vector3f camForward, camLeft, walkDirection;
    public boolean left = false, right = false, forward = false, backward = false;
    private float speedBoost = 1f;

    private short jumpAmount = 0;
    private boolean jumpReset = false;
    private boolean onGround;

    private float low_speed_friction;
    private float acceleration;
    private float standard_friction;
    private float friction_expansion;
    private float jump_power;

    private final Vector3f force = new Vector3f();

    private ModuleManager moduleManager;

    // In average, TPF_COEFF_AVERAGE * tpf = 1
    private static final int TPF_COEFF_AVERAGE = 58;

    protected PlayerPhysicSY(CollisionManager manager, Camera cam, Player player, ModuleManager moduleManager, ColorParkourMain main) {
        this.moduleManager = moduleManager;
        this.manager = manager;
        this.cam = cam;
        this.player = player;
        this.body = createBody();
        this.main = main;
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

        player.setAppearance(new Geometry("hit_box", new Box(0.1f, 0.1f, 0.1f)));
        RigidBodyControl body = manager.loadObject(BoxCollisionShape.class, 20, true, player.getAppearance());
        manager.getAppState().getPhysicsSpace().add(body);
        body.setPhysicsLocation(cam.getLocation());
        body.setPhysicsRotation(cam.getRotation());
        body.setGravity(ColorParkourMain.GAME_GRAVITY);
        body.setFriction(1);

        player.setBody(body);
        return body;
    }

    protected void initListener() {
        manager.getAppState().getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {

        manageCollisions();
        if (body.getPhysicsLocation().y < -30) {
            Vector3f checkPoint = moduleManager.getCurrentModule().getInitialLocation();
            float ySize = moduleManager.first().getSize().y;
            body.setLinearVelocity(new Vector3f());
            player.resetPosition(checkPoint.add(0, 2 + ySize, 0), moduleManager.getCurrentModule());
            Referential.of(body).get().setEnabled(false);
            main.getViewPort().setBackgroundColor(ColorRGBA.randomColor());
        }
        camForward.set(cam.getDirection()).multLocal(0.6f * tpf * TPF_COEFF_AVERAGE);
        camLeft.set(cam.getLeft().multLocal(0.4f * tpf * TPF_COEFF_AVERAGE));
        walkDirection.set(0, 0, 0);

        // We want the camera to be at the top of the body
        cam.setLocation(body.getPhysicsLocation().add(0, 1.5f, 0));

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

        Optional<Referential> optionalRef = Referential.of(body);

        if (!optionalRef.isPresent() || !optionalRef.get().isEnabled()) {
            body.setGravity(ColorParkourMain.GAME_GRAVITY);
            applyForces(body);
        } else {
            body.setGravity(new Vector3f());
            applyForces(optionalRef.get().getExternalBody());
        }
    }

    private void applyForces(RigidBodyControl control) {
        Vector3f spaceSpeed = control.getLinearVelocity();
        Vector2f flatSpeed = new Vector2f(spaceSpeed.x, spaceSpeed.z);
        float friction;
        float speedXZ = flatSpeed.length();

        if (speedXZ < 3 && speedXZ != 0)
            friction = low_speed_friction / speedXZ;
        else
            friction = standard_friction * (float) Math.pow(speedXZ, friction_expansion);

        force.set(walkDirection.mult(acceleration * speedBoost)
                .add(new Vector3f(flatSpeed.x * friction, spaceSpeed.y, flatSpeed.y * friction)));
        control.applyCentralForce(force);
    }


    public void jump() {

        if (jumpAmount <= 0)
            return;

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


        if (lastContact != null && lastContact instanceof ContactEvent) {
            ((ContactEvent) lastContact).leaveByJump(this);
        }

    }

    public void setJumpAmount(short jumpAmount) {
        this.jumpAmount = jumpAmount;
    }

    public short getJumpAmount() {
        return jumpAmount;
    }

    public void manageCollisions() {

        if (!isOnGround(body)) {

            if (lastContact != null && lastContact instanceof ContactEvent) {
                jumpAmount--;
                System.out.println("Loosing jump");
                ((ContactEvent) lastContact).leave(this);
                lastContact = null;
                onGround = false;
            }
            return;
        }
        // If the boolean onGround hasn't been set to true yet (We want to set the jumpAmount only once)
        if (!onGround) {
            jumpAmount = 1;
        }
        if (lastContact instanceof ContactEvent) {
            ContactEvent contactPlatform = (ContactEvent) lastContact;

            contactPlatform.collision(this);
            if (!onGround) {
                contactPlatform.collided(this);
                LOGGER.info("COLLISION : " + jumpAmount);
                body.setPhysicsRotation(new Quaternion(0, 1, 0, 0));
            }
        }
        onGround = true;
    }

    protected boolean isOnGround(RigidBodyControl body) {

        Vector3f location = new Vector3f();
        Vector3f rayVector = new Vector3f();
        float playerHeight = 0.1f;
        location.set(new Vector3f(0, 1, 0)).multLocal(playerHeight).addLocal(body.getPhysicsLocation());
        rayVector.set(new Vector3f(0, 1, 0)).multLocal(-playerHeight - 0.1f).addLocal(location);
        List<PhysicsRayTestResult> results = body.getPhysicsSpace().rayTest(location, rayVector);
        for (PhysicsRayTestResult physicsRayTestResult : results) {
            if (!physicsRayTestResult.getCollisionObject().equals(body)) {
                ModuleSY currentModule = moduleManager.getCurrentModule();
                Optional<ColoredPlatform> optionalPlatform = currentModule.getPlatformByBody(
                        (RigidBodyControl) physicsRayTestResult.getCollisionObject());
                if (optionalPlatform.isPresent()) {
                    this.lastContact = optionalPlatform.get();
                }
                return true;
            }
        }
        return false;
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

    public Vector3f getForce() {
        return force;
    }

    public RigidBodyControl getBody() {
        return body;
    }

    // non used overrided method
    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
    }
}
