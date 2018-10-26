package net.starype.colorparkour.entity.player;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.PhysicEntity;

public class Player extends PhysicEntity {

    private CameraSY camera;
    private PlayerPhysicSY physicBody;

    public Player(ColorParkourMain main, Camera source, CollisionManager manager) {
        super(manager, main);
        camera = new CameraSY(main, source);
        physicBody = new PlayerPhysicSY(manager, source, this);
        physicBody.setAcceleration(2500)
                .setLowSpeedFriction(-150)
                .setFrictionExpansion(1.3f)
                .setStandardFriction(-5)
                .setJumpPower(19);
    }

    public void initialize() {
        camera.initMappings();
        physicBody.initListener();
    }

    public PlayerPhysicSY getPhysicPlayer() { return physicBody; }

    public CameraSY getCamera() { return camera; }

    protected void setBody(RigidBodyControl body) { super.body = body; }
    protected Spatial getAppearance() { return  appearance; }
    protected void setAppearance(Spatial appearance) {super.appearance = appearance; }
}
