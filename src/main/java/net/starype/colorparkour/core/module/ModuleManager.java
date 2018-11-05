package net.starype.colorparkour.core.module;

import com.jme3.math.Vector3f;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.platform.ColoredPlatform;
import net.starype.colorparkour.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(ModuleManager.class);

    private List<ModuleSY> modules;
    private int currentModule;
    private ColorParkourMain main;
    private Player player;

    public ModuleManager(ColorParkourMain main) {
        this.modules = new ArrayList<>();
        this.main = main;
    }

    public void attachPlayer(Player player) { this.player = player; }
    public void add(ModuleSY... modules) {
        for(ModuleSY m : modules){
            this.modules.add(m);
            try {
                m.loadPlatforms();
            } catch (FileNotFoundException e){
                LOGGER.debug("exists: " + new File(m.getPath()).exists());
                LOGGER.error("ModuleSY configuration file not found !");
            }
        }

    }

    public void start() {
        modules.get(currentModule).setActive(true);
    }

    public void checkNext(Vector3f playerPos) {
        ModuleSY current = modules.get(currentModule);
        Vector3f end = current.getFinalPosition();
        ColoredPlatform lastPlatform = current.getPlatforms().get(current.getPlatforms().size()-1);
        float distanceMax = lastPlatform.getSize().x + lastPlatform.getSize().y / 2;
        if (playerPos.add(0,-1,0).add(end.mult(-1)).length() < distanceMax) {
            float ySize = first().getSize().y;
            if(currentModule < modules.size() -1) {
                currentModule++;
                current.setActive(false);
                player.resetPosition(first().getPosition().add(0, 2+ySize, 0), getCurrentModule());
                start();
            } else {
                System.out.println("Félicitations vous avez fini le jeu !");
                player.resetPosition(first().getPosition().add(0, 2+ySize, 0), getCurrentModule());
            }
        }
    }
    public void back() {
        if(currentModule != 0)
            currentModule--;
    }
    public ModuleSY getCurrentModule() {
        return modules.get(currentModule);
    }
    public ColoredPlatform first() {
        return getCurrentModule().getPlatforms().get(0);
    }
    public ColoredPlatform last() {
        return getCurrentModule().getPlatforms().get(getCurrentModule().getPlatforms().size()-1);
    }
    public List<ModuleSY> getModules() {return modules; }
}