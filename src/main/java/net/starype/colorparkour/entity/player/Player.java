package net.starype.colorparkour.entity.player;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.Camera;
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
    }

    public void initialize() {
        camera.initMappings();
        physicBody.initListener();
    }

    public PlayerPhysicSY getPhysicPlayer() { return physicBody; }

    public CameraSY getCamera() { return camera; }

    protected void setBody(RigidBodyControl body) { super.body = body; }
}
