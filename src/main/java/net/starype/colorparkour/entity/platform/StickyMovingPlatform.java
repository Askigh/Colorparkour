package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.platform.event.ContactEvent;
import net.starype.colorparkour.entity.player.PlayerPhysicSY;
import net.starype.colorparkour.utils.Referential;

public class StickyMovingPlatform extends MovingPlatform implements ContactEvent {

    private final Referential referential;

    public StickyMovingPlatform(CollisionManager manager, SimpleApplication main, float[] size, Vector3f departure,
                                Vector3f arrival, float speed, ColorRGBA color, String platformID) {
        super(manager, main, size, departure, arrival, speed, color, platformID);
        body.setFriction(0.5f);
        referential = new Referential(body, ((ColorParkourMain) main).getPlayer().getBody());
        referential.setEnabled(false);
    }

    @Override
    public void collision(PlayerPhysicSY physicSY) {
        referential.update();
    }

    @Override
    public void collided(PlayerPhysicSY physicSY) {
        LOGGER.warn("Enabling referential");
        ((ColorParkourMain) main).getPlayer().getBody().setFriction(0);
        referential.setEnabled(true);
    }

    @Override
    public void leaveByJump(PlayerPhysicSY physicSY) {
        //((ColorParkourMain) main).getPlayer().getBody().setFriction(1);
        referential.setEnabled(false);
    }
}
