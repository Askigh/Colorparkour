package net.starype.colorparkour.player;

import com.jme3.renderer.Camera;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.core.ColorParkourMain;

public class Player {

    private CameraSY camera;
    private PlayerPhysicSY body;

    public Player(ColorParkourMain main, Camera source, CollisionManager manager) {
        camera = new CameraSY(main, source);
        body = new PlayerPhysicSY(manager, source, this);
    }

    public void initialize() {
        camera.initMappings();
        body.initListener();
    }

    public PlayerPmybad
    hysicSY getPhysicPlayer() { return body; }
    public CameraSY getCamera() { return camera; }
}
