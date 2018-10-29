package net.starype.colorparkour.core;

import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModuleManager {

    private List<Module> modules;
    private int currentModule;

    public ModuleManager() {
        this.modules = new ArrayList<>();
    }

    public void add(Module... modules) {
        Arrays.asList(modules).forEach(m -> this.modules.add(m));
    }

    public void start() {
        modules.get(currentModule).setActive(true);
    }

    public void checkNext(Vector3f playerPos) {
        Module current = modules.get(currentModule);
        Vector3f end = current.getFinalPosition();
        if (playerPos.add(end.mult(-1)).length() < 0.5f) {
            currentModule++;
            current.setActive(false);
            start();
        }
    }
}