package net.starype.colorparkour.entity.player;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.PhysicEntity;
import net.starype.colorparkour.entity.platform.PlatformManager;

public class Player extends PhysicEntity {

    private CameraSY camera;
    private PlayerPhysicSY physicBody;

    public Player(ColorParkourMain main, Camera source, CollisionManager manager, PlatformManager platformManager) {
        super(manager, main);
        camera = new CameraSY(main, source);
        physicBody = new PlayerPhysicSY(manager, source, this, platformManager, main);
        physicBody.setAcceleration(1200)
                .setLowSpeedFriction(-100)
                .setFrictionExpansion(1.2f)
                .setStandardFriction(-3)
                .setJumpPower(19);
    }

    public void initialize() {
        camera.initMappings();
        physicBody.initListener();
    }

    public PlayerPhysicSY getPhysicPlayer() { return physicBody; }

    public CameraSY getCamera() { return camera; }

    protected void setBody(RigidBodyControl body) { super.body = body; }
    protected void setAppearance(Spatial appearance) {super.appearance = appearance; }
}
