package net.starype.colorparkour.module;

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

    public ModuleManager(ColorParkourMain main) {
        this.modules = new ArrayList<>();
        this.main = main;
    }

    public void add(ModuleSY... modules) {
        for (ModuleSY m : modules) {
            this.modules.add(m);
            try {
                m.loadPlatformsFromJson();
            } catch (FileNotFoundException e) {
                LOGGER.debug("exists: " + new File(m.getPath()).exists());
                LOGGER.error("ModuleSY configuration file not found !");
            }
        }
    }

    public void start() {
        modules.get(currentModule).setActive(true);
    }

    public void checkNext(Vector3f playerPos) {
        if (!main.getPlayer().getPhysicPlayer().isOnGround(main.getPlayer().getBody())) {
            return;
        }
        ModuleSY current = modules.get(currentModule);
        Vector3f end = current.getFinalPosition();
        ColoredPlatform lastPlatform = current.getPlatforms().get(current.getPlatforms().size() - 1);
        float distanceMax = lastPlatform.getSize().x + lastPlatform.getSize().y / 2;
        if (playerPos.add(0, -1, 0).add(end.mult(-1)).length() < distanceMax) {
            if (currentModule < modules.size() - 1) {
                currentModule++;
                current.setActive(false);
                main.getPlayer().resetPosition(getCurrentModule());
                start();
            } else {
                System.out.println("Félicitations vous avez fini le jeu !");
                main.getPlayer().resetPosition(getCurrentModule());
            }
        }
    }

    public void previous() {
        if (currentModule != 0) {
            getCurrentModule().setActive(false);
            currentModule--;
            main.getPlayer().resetPosition(getCurrentModule());
            getCurrentModule().setActive(true);
        }
    }

    public ModuleSY getCurrentModule() {
        return modules.get(currentModule);
    }

    public ColoredPlatform first() {
        return getCurrentModule().getPlatforms().get(0);
    }

    public ColoredPlatform last() {
        return getCurrentModule().getPlatforms().get(getCurrentModule().getPlatforms().size() - 1);
    }

    public List<ModuleSY> getModules() {
        return modules;
    }

}