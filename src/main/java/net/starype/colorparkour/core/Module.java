package net.starype.colorparkour.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.entity.platform.ColoredPlatform;
import net.starype.colorparkour.entity.platform.PlatformManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module {

    private List<ColoredPlatform> platforms;
    private PlatformManager manager;
    private SimpleApplication main;
    private PhysicsSpace space;
    private Vector3f finalPosition;

    public Module(SimpleApplication main, PlatformManager manager, PhysicsSpace space, Vector3f finalPosition) {
        this.manager = manager;
        this.platforms = new ArrayList<>();
        this.main = main;
        this.space = space;
        this.finalPosition = finalPosition;
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

    public void loadPlatforms(String path) throws FileNotFoundException {
        Gson gson = new Gson();
        JsonObject datas = gson.fromJson(new FileReader(new File(path)), JsonObject.class);
        datas.get("");

    }

    public void add(ColoredPlatform... plats) {
        Arrays.asList(plats).forEach(p -> {
            platforms.add(p);
            manager.getPlatforms().add(p);
        });
    }

    public Vector3f getFinalPosition() {
        return finalPosition;
    }
}
