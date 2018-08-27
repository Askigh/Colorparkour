package net.starype.testjme.collision;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class CollisionManager {

    private SimpleApplication main;
    private BulletAppState appState;
    
    public CollisionManager(SimpleApplication main) {
        this.main = main;
        appState = new BulletAppState();
    }

    public void init() {
        main.getStateManager().attach(appState);
    }

    public void addCollisionShapeObject(Spatial target) {

        CollisionShape shape = CollisionShapeFactory.createMeshShape(target);
        RigidBodyControl body = new RigidBodyControl(shape, 0);
        target.addControl(body);

        add(body);
    }

    public CharacterControl createPlayer(float width, float height, int speed, int fallSpeed) {

        CapsuleCollisionShape shape = new CapsuleCollisionShape(width,height,1);
        CharacterControl player = new CharacterControl(shape, 0.05f);
        player.setJumpSpeed(speed);
        player.setFallSpeed(fallSpeed);
        player.setGravity(new Vector3f(0,0,0));
        player.setPhysicsLocation(new Vector3f(0,10,0));
        add(player);
        return player;
    }

    private void add(Object o) {
        appState.getPhysicsSpace().add(o);
    }

    public BulletAppState getAppState() { return appState; }
}
