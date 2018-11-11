package net.starype.colorparkour.entity.player;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.module.ModuleManager;
import net.starype.colorparkour.module.ModuleSY;
import net.starype.colorparkour.entity.PhysicEntity;
import net.starype.colorparkour.entity.platform.ColoredPlatform;
import net.starype.colorparkour.entity.platform.MovingPlatform;
import net.starype.colorparkour.utils.Referential;

public class Player extends PhysicEntity {

    private CameraSY camera;
    private PlayerPhysicSY physicBody;

    public Player(ColorParkourMain main, Camera source, CollisionManager manager, ModuleManager moduleManager) {
        super(manager, main);
        camera = new CameraSY(main, source);
        physicBody = new PlayerPhysicSY(manager, source, this, moduleManager, main);
        physicBody.setAcceleration(2100)
                .setLowSpeedFriction(-75)
                .setFrictionExpansion(1.3f)
                .setStandardFriction(-5)
                .setJumpPower(22);
    }

    public void initialize() {
        camera.initMappings();
        physicBody.initListener();
    }

    public void resetPosition(ModuleSY module) {
        body.setLinearVelocity(new Vector3f());
        Vector3f pos = module.getInitialLocation().add(0, 2, 0);
        setPosition(pos);
        camera.getSource().setLocation(pos);
        camera.getSource().setRotation(ColorParkourMain.INITIAL_ROTATION);

        for(ColoredPlatform col : module.getPlatforms()) {
            if(col instanceof MovingPlatform) {
                col.setPosition(((MovingPlatform) col).getDeparture());
            }
        }
        Referential.of(body).get().setEnabled(false);
        main.getViewPort().setBackgroundColor(ColorRGBA.randomColor());
    }

    public PlayerPhysicSY getPhysicPlayer() { return physicBody; }
    public CameraSY getCamera() { return camera; }

    protected void setBody(RigidBodyControl body) { super.body = body; }
    protected void setAppearance(Spatial appearance) {super.appearance = appearance; }
}
