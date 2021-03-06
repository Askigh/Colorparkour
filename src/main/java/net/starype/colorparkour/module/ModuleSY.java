package net.starype.colorparkour.module;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.platform.ColoredPlatform;
import net.starype.colorparkour.entity.platform.MovingPlatform;
import net.starype.colorparkour.entity.platform.event.LoadEvent;
import net.starype.colorparkour.utils.PlatformJSONBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * The classes whose names are similar or equal to other classes end with 'SY', in order not to mix up
 * All of them are provided by Starype
 */
public class ModuleSY {

    public static final Logger LOGGER = LoggerFactory.getLogger(ModuleSY.class);

    private List<ColoredPlatform> platforms;
    private ColorParkourMain main;
    private PhysicsSpace space;
    private Vector3f finalPosition = new Vector3f();
    private final String path;
    private String levelName;
    private boolean loaded = false;

    public ModuleSY(ColorParkourMain main, PhysicsSpace space, String path) {
        this.platforms = new ArrayList<>();
        this.main = main;
        this.space = space;
        this.path = path;
    }

    public ModuleSY build(ModuleManager manager) {
        manager.add(this);
        return this;
    }

    public void setActive(boolean active) {
        boolean alreadyLoaded = loaded;
        for (ColoredPlatform platform : platforms) {
            // Actions when we want to enable the level
            if (active) {
                if (!loaded) {
                    alreadyLoaded = true;
                    loadPlatform(platform);
                } else {
                    platform.show();
                }
                // Actions when we want to disable the level
            } else {
                platform.hide(true);
            }

        }
        loaded = alreadyLoaded;
        if (active) {
            showOnly(ColorRGBA.White);
        }
    }

    private void loadPlatform(ColoredPlatform platform) {
        platform.loadBody(platform.getSize().x, platform.getSize().y, platform.getSize().z,
                platform.getPosition(), platform.getColor());
        if (platform instanceof LoadEvent) {
            ((LoadEvent) platform).load();
        }
        platform.show();
    }

    public void loadPlatformsFromJson() throws FileNotFoundException, NullPointerException {
        Gson gson = new Gson();
        JsonObject datas = gson.fromJson(new FileReader(new File(path)), JsonObject.class);

        this.levelName = datas.get("levelName").getAsString();

        Iterator<JsonElement> platforms = datas.getAsJsonArray("platforms").iterator();
        while (platforms.hasNext())
            PlatformJSONBuilder.loadPlatform(platforms.next(), main);
    }

    public ModuleSY add(ColoredPlatform... plats) {
        Arrays.asList(plats).forEach(p -> platforms.add(p));
        return this;
    }

    public Optional<ColoredPlatform> getPlatformBySpatial(Spatial spatial) {
        for (ColoredPlatform plat : platforms)
            if (plat.getAppearance().equals(spatial))
                return Optional.of(plat);
        return Optional.empty();
    }

    public Optional<ColoredPlatform> getPlatformByBody(RigidBodyControl body) {
        for (ColoredPlatform plat : platforms)
            if (plat.getBody().equals(body))
                return Optional.of(plat);
        return Optional.empty();
    }

    public void reversePlatforms() {
        for (ColoredPlatform plat : platforms) {
            if (plat instanceof MovingPlatform) {
                MovingPlatform movPlat = (MovingPlatform) plat;
                if (movPlat.getAppearance() == null) {
                    continue;
                }
                movPlat.getAppearance().setLocalTranslation(movPlat.getBody().getPhysicsLocation().add(0, 1.1f, 0));
                Vector3f position = movPlat.getPosition();
                float distanceDep = position.add(movPlat.getDeparture().mult(-1)).length();
                float distanceArr = position.add(movPlat.getArrival().mult(-1)).length();

                Vector3f dir = movPlat.getDirection();
                boolean closeToArr = distanceArr < 2f && dir.equals(movPlat.getInitialDirection());
                boolean closeToDep = distanceDep < 2f && dir.mult(-1).equals(movPlat.getInitialDirection());

                if (closeToArr || closeToDep) {
                    movPlat.setDirection(movPlat.getDirection().mult(-1));
                    movPlat.resetMovement();
                }
            }
        }
    }

    public void showOnly(ColorRGBA color) {
        for (ColoredPlatform platform : platforms) {
            if (!platform.getColor().equals(color) && !platform.getColor().equals(ColorRGBA.White)) {
                platform.hide(false);
            } else {
                platform.show();
            }
        }
    }

    public Vector3f getFinalPosition() {
        finalPosition.set(platforms.get(platforms.size() - 1).getPosition());
        return finalPosition;
    }

    public Vector3f getInitialLocation() {
        return platforms.get(0).getPosition();
    }

    public List<ColoredPlatform> getPlatforms() {
        return platforms;
    }

    public String getPath() {
        return path;
    }

    public String getLevelName() {
        return levelName;
    }
}
