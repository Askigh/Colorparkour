package net.starype.colorparkour.entity.player;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.core.ModuleManager;
import net.starype.colorparkour.entity.PhysicEntity;
import net.starype.colorparkour.utils.PlatformBuilder;

public class Player extends PhysicEntity {

    private CameraSY camera;
    private PlayerPhysicSY physicBody;

    public Player(ColorParkourMain main, Camera source, CollisionManager manager, ModuleManager moduleManager) {
        super(manager, main);
        camera = new CameraSY(main, source);
        physicBody = new PlayerPhysicSY(manager, source, this, moduleManager, main);
        physicBody.setAcceleration(1800)
                .setLowSpeedFriction(-75)
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
    protected void setAppearance(Spatial appearance) {super.appearance = appearance; }
}
