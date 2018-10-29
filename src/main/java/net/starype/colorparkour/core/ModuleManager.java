package net.starype.colorparkour.core;

import com.jme3.math.Vector3f;
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
        if (playerPos.add(0,-1,0).add(end.mult(-1)).length() < 1.5f) {
            if(currentModule < modules.size() -1) {
                currentModule++;
                current.setActive(false);
                start();
            } else {
                System.out.println("Félicitations vous avez fini le jeu !");
                main.resetGame();
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
    public List<ModuleSY> getModules() {return modules; }
}