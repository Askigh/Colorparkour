package net.starype.colorparkour.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import net.starype.colorparkour.entity.platform.ColoredPlatform;
import net.starype.colorparkour.entity.platform.MovingPlatform;
import net.starype.colorparkour.utils.PlatformBuilder;
import net.starype.colorparkour.utils.PlatformJSONBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class ModuleSY {

    public static final Logger LOGGER = LoggerFactory.getLogger(ModuleSY.class);

    private List<ColoredPlatform> platforms;
    private PlatformBuilder manager;
    private ColorParkourMain main;
    private PhysicsSpace space;
    private Vector3f finalPosition;
    private final String path;
    private String levelName;

    public ModuleSY(ColorParkourMain main, PlatformBuilder manager, PhysicsSpace space, String path) {
        this.manager = manager;
        this.platforms = new ArrayList<>();
        this.main = main;
        this.space = space;
        this.path = path;
    }

    public ModuleSY build() {
        this.finalPosition = platforms.get(platforms.size()-1).getPosition();
        return this;
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

    public void loadPlatforms() throws FileNotFoundException, NullPointerException {
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
            if(plat.getAppearance().equals(spatial))
                return Optional.of(plat);
        return Optional.empty();
    }
    public Optional<ColoredPlatform> getUnderPlatformBySpatial(Spatial spatial) {
        for (ColoredPlatform plat : platforms)
            if(plat.getAppearance().equals(spatial))
                return Optional.of(plat);
        return Optional.empty();
    }

    public void reversePlatforms() {
        for(ColoredPlatform plat : platforms) {
            if(plat instanceof MovingPlatform) {
                MovingPlatform movPlat = (MovingPlatform) plat;
                Vector3f position = movPlat.getPosition();
                float distanceDep = position.add(movPlat.getDeparture().mult(-1)).length();
                float distanceArr = position.add(movPlat.getArrival().mult(-1)).length();

                boolean closeToArr = distanceArr < 0.5f && movPlat.getDirection().equals(movPlat.getInitialDirection());
                boolean closeToDep = distanceDep < 0.5f && movPlat.getDirection().mult(-1)
                        .equals(movPlat.getInitialDirection());
                if(closeToArr || closeToDep) {
                    movPlat.setDirection(movPlat.getDirection().mult(-1));
                    movPlat.resetMovement();
                }
            }
        }
    }
    public Vector3f getFinalPosition() {
        return finalPosition;
    }
    public Vector3f getInitialLocation() { return platforms.get(0).getPosition(); }
    public List<ColoredPlatform> getPlatforms() { return platforms; }
    public String getPath(){
        return path;
    }
}
