package net.starype.colorparkour.entity;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import net.starype.colorparkour.collision.CollisionManager;

public abstract class PhysicEntity {

    protected RigidBodyControl body;
    protected Spatial appearance;
    protected CollisionManager manager;
    protected SimpleApplication main;

    public PhysicEntity(CollisionManager manager, SimpleApplication main) {
        this.manager = manager;
        this.main = main;
    }

    protected void addInPhysicsSpace() {
        manager.getAppState().getPhysicsSpace().add(body);
    }

    public Vector3f getPosition() {
        return appearance.getLocalTranslation();
    }

    public void setPosition(Vector3f pos) {
        body.setPhysicsLocation(pos);
    }
}
