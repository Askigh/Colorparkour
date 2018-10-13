package net.starype.colorparkour.collision;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Spatial;
import java.lang.reflect.Constructor;

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

    /**
     *
     * @param type The kind of shape you want to get
     * @param mass The mass of the body created. If non null, it will create a dynamic object
     * @param datas The parameters (only primitives or spatial) required in the constructeur of the type class
     * @return a RigidBodyControl created from the data added
     */
    public RigidBodyControl loadObject(Class<? extends CollisionShape> type, int mass, Object... datas) {

        if(datas.length == 0) throw new IllegalArgumentException("Constructor datas cannot be ommitted");

        // if a CollisionShapeFactory is required, for complex shapes
        if(type.equals(CollisionShape.class) || type.equals(HullCollisionShape.class))
            return processForSpatials(datas[0], mass);

        // loading a new instance of the type parameter
        Class<?>[] typeParams = new Class<?>[datas.length];
        CollisionShape shape = null;

        try {
            for(int i = 0; i<datas.length; i++) {
                typeParams[i] = (Class<?>) datas[i].getClass().getField("TYPE").get(null);
            }

            Constructor<? extends CollisionShape> constructor = type.getConstructor(typeParams);
            shape = constructor.newInstance(datas);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // creating the body
        RigidBodyControl control = new RigidBodyControl(shape, mass);
        appState.getPhysicsSpace().add(control);
        return control;
    }

    private RigidBodyControl processForSpatials(Object spatial, int mass) {

        if(!(spatial instanceof Spatial))
            throw new IllegalArgumentException("Cannot load a CollisionShape if datas[0] is not a spatial");

        CollisionShape shape = mass > 0
                ? CollisionShapeFactory.createDynamicMeshShape(((Spatial) spatial))
                : CollisionShapeFactory.createMeshShape((Spatial) spatial);

        RigidBodyControl control = new RigidBodyControl(shape, mass);
        ((Spatial) spatial).addControl(control);
        appState.getPhysicsSpace().add(control);
        return control;
    }

    public BulletAppState getAppState() { return appState; }
}
