package net.starype.colorparkour.entity.player;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import net.starype.colorparkour.utils.CollisionManager;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.module.ModuleSY;
import net.starype.colorparkour.module.ModuleManager;
import net.starype.colorparkour.entity.platform.*;
import net.starype.colorparkour.entity.platform.event.ContactEvent;
import net.starype.colorparkour.utils.Referential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * The classes whose names are similar or equal to other classes end with 'SY', in order not to mix up
 * All of them are provided by Starype
 */
public class PlayerPhysicSY implements PhysicsTickListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(PlayerPhysicSY.class);

    private CollisionManager manager;
    private Camera cam;
    private Player player;
    private RigidBodyControl body;
    private ColoredPlatform lastContact;

    private final Vector3f camForward, camLeft, walkDirection;
    public boolean left = false, right = false, forward = false, backward = false;
    private float speedBoost = 1f;

    private boolean jumpBonus = false;
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

    PlayerPhysicSY(CollisionManager manager, Camera cam, Player player, ModuleManager moduleManager, ColorParkourMain main) {
        this.moduleManager = moduleManager;
        this.manager = manager;
        this.cam = cam;
        this.player = player;
        this.body = createBody(main);
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

    private RigidBodyControl createBody(ColorParkourMain main) {

        player.setAppearance(new Geometry("hit_box", new Box(0.2f, 0.2f, 0.2f)));
        Material mat = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Magenta);
        player.getAppearance().setMaterial(mat);
        RigidBodyControl body = manager.loadObject(BoxCollisionShape.class, 20, true, player.getAppearance());
        manager.getAppState().getPhysicsSpace().add(body);
        body.setPhysicsLocation(cam.getLocation());
        body.setPhysicsRotation(cam.getRotation());
        body.setGravity(ColorParkourMain.GAME_GRAVITY);
        body.setFriction(1);
        player.setBody(body);
        return body;
    }

    void initListener() {
        manager.getAppState().getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {

        if(body.getPhysicsSpace() == null) {
            return;
        }
        manageCollisions();
        if (body.getPhysicsLocation().y < -10) {
            player.resetPosition(moduleManager.getCurrentModule());
        }
        camForward.set(cam.getDirection()).multLocal(0.6f * tpf * TPF_COEFF_AVERAGE);
        camLeft.set(cam.getLeft().multLocal(0.4f * tpf * TPF_COEFF_AVERAGE));
        walkDirection.set(0, 0, 0);

        // We want the camera to be at the top of the body
        cam.setLocation(body.getPhysicsLocation().add(0, 1.1f, 0));

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
            applyForcesTo(body);
        } else {
            body.setGravity(new Vector3f());
            applyForcesTo(optionalRef.get().getExternalBody());
        }
    }

    private void applyForcesTo(RigidBodyControl control) {
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
        if(!isOnGround(body) && !noKeyTouched()) {
            force.multLocal(0.5f);
        }
        control.applyCentralForce(force);
    }

    private boolean noKeyTouched() {
        return !left && !right && !forward && !backward;
    }
    public void jump() {

        if(!isOnGround(body) && !jumpBonus) {
            return;
        }
        if(!isOnGround(body)) {
            jumpBonus = false;
        }

        speedBoost-= 0.5f;
        Vector3f spaceSpeed = body.getLinearVelocity();
        /*
            Modification of the "classic" physics. If the body is falling, we still want
            the jump to propulse the player upward
        */
        float y = spaceSpeed.y > 0 ? spaceSpeed.y : spaceSpeed.y / 2.5f;
        body.setLinearVelocity(new Vector3f(spaceSpeed.x, y + jump_power, spaceSpeed.z));

        if (lastContact instanceof ContactEvent) {
            ((ContactEvent) lastContact).leaveByJump(this);
        }
        speedBoost+= 0.5f;
    }

    public void addBonus() { this.jumpBonus = true; }

    private void manageCollisions() {

        if (!isOnGround(body)) {
            if (lastContact != null) {
                onGround = false;
                if(lastContact instanceof ContactEvent) {
                    ((ContactEvent) lastContact).leave(this);
                }
                lastContact = null;
            }
            return;
        }
        // If the boolean onGround hasn't been set to true yet (We want to set the jumpBonus only once)
        //jumpBonus = 1;
        if (lastContact instanceof ContactEvent) {
            ContactEvent contactPlatform = (ContactEvent) lastContact;

            contactPlatform.collision(this);
            if (!onGround) {
                // Before there was a 80 ms delay
                contactPlatform.collided(getInstance());
            }
        }
        onGround = true;
    }
    private PlayerPhysicSY getInstance() { return this;}

    private boolean isOnGround(RigidBodyControl body) {
        Vector3f location = new Vector3f();
        Vector3f rayVector = new Vector3f();

        location.set(new Vector3f(0, 1, 0)).addLocal(body.getPhysicsLocation());
        rayVector.set(new Vector3f(0, 2, 0)).multLocal(-1 - 0.1f).addLocal(location);

        List<PhysicsRayTestResult> results = body.getPhysicsSpace().rayTest(location, rayVector);

        for (PhysicsRayTestResult physicsRayTestResult : results) {
            if (!physicsRayTestResult.getCollisionObject().equals(body)) {

                ModuleSY currentModule = moduleManager.getCurrentModule();
                Optional<ColoredPlatform> optionalPlatform = currentModule.getPlatformByBody(
                        (RigidBodyControl) physicsRayTestResult.getCollisionObject());

                optionalPlatform.ifPresent(x -> this.lastContact = optionalPlatform.get());
                return true;
            }
        }
        return false;
    }
    /**
     * This is an alternative method that could be useful, it looks a bit less precise than the other one
     *
    private boolean isOnGround(Spatial col1) {
        BoundingBox box = new BoundingBox(col1.getLocalTranslation(), 0.1f, 0.1f, 0.1f);
        for(ColoredPlatform platform : moduleManager.getCurrentModule().getPlatforms()) {
            CollisionResults results = new CollisionResults();
            box.collideWith(platform.getAppearance(), results);
            if(results.size() > 0) {
                return true;
            }
        }
        return false;
    }*/

    PlayerPhysicSY setLowSpeedFriction(float low_speed_friction) {
        this.low_speed_friction = low_speed_friction;
        return this;
    }

    PlayerPhysicSY setAcceleration(float acceleration) {
        this.acceleration = acceleration;
        return this;
    }

    PlayerPhysicSY setStandardFriction(float standard_friction) {
        this.standard_friction = standard_friction;
        return this;
    }

    PlayerPhysicSY setFrictionExpansion(float friction_expansion) {
        this.friction_expansion = friction_expansion;
        return this;
    }

    PlayerPhysicSY setJumpPower(float jump_power) {
        this.jump_power = jump_power;
        return this;
    }

    public void sprint() { this.speedBoost = 2.2f; }
    public void walk() { this.speedBoost = 1f; }
    public Vector3f getForce() { return force; }
    public RigidBodyControl getBody() { return body; }

    // non used overrided method
    @Override
    public void physicsTick(PhysicsSpace space, float tpf) { }
}
