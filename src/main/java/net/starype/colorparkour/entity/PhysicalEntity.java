package net.starype.colorparkour.entity;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import net.starype.colorparkour.utils.CollisionManager;

/**
 * Every object that has a physical body and an appearance is a PhysicalEntity.
 *
 */
public abstract class PhysicalEntity {

    protected RigidBodyControl body;
    protected Spatial appearance;
    protected CollisionManager manager;
    protected SimpleApplication main;

    public PhysicalEntity(CollisionManager manager, SimpleApplication main) {
        this.manager = manager;
        this.main = main;
    }

    protected void addInPhysicsSpace() {
        addInPhysicsSpace(body);
    }
    protected void addInPhysicsSpace(RigidBodyControl target) {
        manager.getAppState().getPhysicsSpace().add(target);
    }

    public Vector3f getPosition() {
        return appearance.getLocalTranslation();
    }

    public void setPosition(Vector3f pos) {
        if(body != null) {
            body.setPhysicsLocation(pos);
        }
    }
    public RigidBodyControl getBody() {
        return body;
    }
    public Spatial getAppearance() { return appearance; }
}
