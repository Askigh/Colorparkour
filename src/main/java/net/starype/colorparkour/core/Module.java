package net.starype.colorparkour.core;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.PhysicsSpace;
import net.starype.colorparkour.entity.platform.ColoredPlatform;
import net.starype.colorparkour.entity.platform.PlatformManager;
import net.starype.colorparkour.entity.player.PlayerPhysicSY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module {

    private List<ColoredPlatform> platforms;
    private PlatformManager manager;
    private SimpleApplication main;
    private PhysicsSpace space;

    public Module(SimpleApplication main, PlatformManager manager, PhysicsSpace space) {
        this.manager = manager;
        this.platforms = new ArrayList<>();
        this.main = main;
        this.space = space;
    }

    public void setActive(boolean active) {
        for(ColoredPlatform platform : platforms) {
            if(active) {
                main.getRootNode().attachChild(platform.getAppearance());
                space.add(platform.getBody());
            } else {
                main.getRootNode().detachChild(platform.getAppearance());
                space.remove(platform.getBody());
            }
        }
    }

    public void add(ColoredPlatform... plats) {
        Arrays.asList(plats).forEach(p -> {
            platforms.add(p);
            manager.getPlatforms().add(p);
        });
    }
}
