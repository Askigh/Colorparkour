package net.starype.colorparkour.entity.platform;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.collision.CollisionManager;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.utils.Referential;

public class StickyMovingPlatform extends MovingPlatform {

    private final Referential referential;

    public StickyMovingPlatform(CollisionManager manager, SimpleApplication main, float[] size, Vector3f departure,
                                Vector3f arrival, float speed, ColorRGBA color, String platformID) {
        super(manager, main, size, departure, arrival, speed, color, platformID);
        body.setFriction(0);
        referential = new Referential(body, ((ColorParkourMain) main).getPlayer().getBody(),
                manager.getAppState().getPhysicsSpace());
        referential.setEnabled(false);
    }

    public void stick() {
        referential.setEnabled(true);
    }
}
