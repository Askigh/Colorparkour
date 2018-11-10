package net.starype.colorparkour.utils;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Askigh
 * <p>
 * Referential is a class that helps you changing the physics referential of an object.
 * The speed of the defined object will always be calculated like the following :
 * <p>
 * Referential speed ({@link Vector3f} or {@link RigidBodyControl#getLinearVelocity()}) + object speed
 * <p>
 * For instance if you want your player to be in a car,
 * the referentialBody is the car, and the attached body is the player. Don't forget to update the position of
 * the referential if it is moving by calling the {@link Referential#update()} method,
 * or the static method {@link Referential#updateAll()}
 * <p>
 * TODO: a {@link List<RigidBodyControl>} instead of a {@link RigidBodyControl} for the attached body.
 */
public class Referential {

    private static final Logger LOGGER = LoggerFactory.getLogger(Referential.class);

    private static final List<Referential> referentials = new ArrayList<>();

    private final Vector3f referentialSpeed = new Vector3f();
    private final Vector3f referentialPos = new Vector3f();
    private final RigidBodyControl referentialBody;
    private final List<RigidBodyControl> attachedBodies = new ArrayList<>();
    private final RigidBodyControl invisibleBody;

    private boolean enabled = true;
    private boolean destroyed = false;

    /**
     * Creates a referential using a body
     *
     * @param referentialBody the referential
     * @param attachments      the bodies attached to the referential
     */
    public Referential(RigidBodyControl referentialBody, @Nonnull RigidBodyControl... attachments) {
        this(referentialBody, referentialBody.getPhysicsLocation(), referentialBody.getLinearVelocity(), attachments);
    }

    /**
     * @param referentialSpeed the defined speed of your referential, can be null or changed
     * @param referentialPos   the defined position of your referential, can be null or changed
     * @param attachments       the bodies linked to the referential
     */
    public Referential(Vector3f referentialSpeed, Vector3f referentialPos, @Nonnull RigidBodyControl... attachments) {
        this(null, referentialPos, referentialSpeed, attachments);
    }

    /**
     * Initiation constructor
     */
    private Referential(RigidBodyControl referentialBody, Vector3f referentialPos, Vector3f referentialSpeed,
                        RigidBodyControl... attachments) {
        this.referentialBody = referentialBody;

        for(RigidBodyControl control : attachments)
            attachedBodies.add(control);
        this.referentialPos.set(referentialPos);
        this.referentialSpeed.set(referentialSpeed);
        this.invisibleBody = createGhostBody(attachments[0]);
        referentials.add(this);
    }

    /**
     * @param body
     * @return An Optional containing the Referential needed if present
     */
    public static Optional<Referential> of(RigidBodyControl body) {
        for (Referential ref : referentials) {
            if (ref.getAttachments().contains(body)) {
                return Optional.of(ref);
            }
        }
        return Optional.empty();
    }

    /**
     * Updates all the enabled referentials
     * Teleports the ghost body to the real body, and (TODO) Apply the different forces correctly to the attached body
     */
    public static void updateAll() {
        for (Referential ref : referentials) {
            // no need to check if ref is destroyed, as destroying a referential removes it from the list
            if (!ref.isEnabled()) {
                continue;
            }
            if (ref.getReferentialBody().isPresent()) {
                ref.referentialPos.set(ref.getReferentialBody().get().getPhysicsLocation());
                ref.referentialSpeed.set(ref.getReferentialBody().get().getLinearVelocity());
            }
            ref.invisibleBody.setPhysicsLocation(new Vector3f(0,-10,0));

            for(RigidBodyControl attachment : ref.attachedBodies) {
                attachment.setLinearVelocity(ref.getReferentialSpeed().add(ref.invisibleBody.getLinearVelocity()));
            }
        }
    }

    /**
     * Creates the invisible body that will have forces and speed applied instead of the attached body
     *
     * @param attachment the attached body
     * @return the invisible body
     */
    public RigidBodyControl createGhostBody(RigidBodyControl attachment) {
        RigidBodyControl ghost = (RigidBodyControl) attachment.jmeClone();
        attachment.getPhysicsSpace().add(ghost);
        ghost.setGravity(new Vector3f());
        ghost.setLinearVelocity(new Vector3f());
        return ghost;
    }
    /**
     * A destroyed Referential cannot be used anymore
     */
    public void destroy() {
        referentials.remove(this);
        destroyed = true;
    }

    /**
     * You generally want to call {@link Referential#updateAll()} but if you want this specific referential to
     * be updated, call this method
     */
    public void update() {
        if (!enabled || destroyed) {
            LOGGER.error("Cannot update a disabled or destroyed referential");
            return;
        }
        if (getReferentialBody().isPresent()) {
            referentialPos.set(getReferentialBody().get().getPhysicsLocation());
            referentialSpeed.set(getReferentialBody().get().getLinearVelocity());
        }
        invisibleBody.setPhysicsLocation(new Vector3f(0,-10,0));
        for(RigidBodyControl attachment : attachedBodies) {
            attachment.setLinearVelocity(referentialSpeed.add(invisibleBody.getLinearVelocity()));
        }
    }

    /**
     * If enabled, the invisible body will be updated and forces will be applied to the attached body
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return Whether the Referential is enabled or not
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return whether the referential is destroyed or not
     */
    public boolean isDestroyed() {
        return destroyed;
    }
    /**
     * @return the list of non-destroyed referentials
     */
    public static List<Referential> getReferentials() {
        return referentials;
    }

    /**
     * @return the speed of the referential. May be incorrect if not updated
     */
    public Vector3f getReferentialSpeed() {
        return referentialSpeed;
    }

    /**
     * @return the attached body
     */
    public List<RigidBodyControl> getAttachments() {
        return attachedBodies;
    }

    /**
     * @return the invisible body that is constantly teleported at the location of the attached body
     * if {@link Referential#update()} or {@link Referential#updateAll()} is called
     */
    public RigidBodyControl getExternalBody() {
        return invisibleBody;
    }
    /**
     * Sets the body to the relative position of the referential
     *
     * @param relative the relative position
     */
    public void setBodyPosition(RigidBodyControl body, Vector3f relative) {
        body.setPhysicsLocation(relative.add(referentialPos));
    }

    /**
     * @return the relative position of the body
     */
    public Vector3f getBodyPosition(RigidBodyControl body) {
        return body.getPhysicsLocation().add(referentialPos.mult(-1));
    }

    /**
     * @return the referential body if present
     */
    public Optional<RigidBodyControl> getReferentialBody() {
        return Optional.of(referentialBody);
    }
}
