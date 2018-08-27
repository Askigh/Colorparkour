package net.starype.testjme.settings;

import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import java.util.ArrayList;
import java.util.List;

public class KeyboardManager {

    private List<Action> actions;
    private SimpleApplication main;

    public KeyboardManager(SimpleApplication main){
        actions = new ArrayList<>();
        this.main = main;
    }

    public void addKey(String name, final int key) {
        addKey(name, key, listener);
    }

    public void addKey(String name, final int key, ActionListener listener) {

        main.getInputManager().addMapping(name,  new KeyTrigger(key));
        main.getInputManager().addListener(listener, name);
    }

    public void addAction(Action action) {
        actions.add(action);
    }


    public void addLinkedKeyAction(String name, final int key, Action action) {

        addKey(name, key);
        action.setKeyName(name);
        actions.add(action);
    }

    private final ActionListener listener = (name, keyPressed, tpf) -> {

        for(Action action : actions) {

            if(action.getKeyName().equals(name) && (action.isReleaseActive() || keyPressed))
                action.execute(keyPressed);
        }
    };

    public static abstract class Action {

        private String keyName;
        private boolean releaseActive = false;

        public Action() {
            this("undefined");
        }

        public Action(String keyName) {
            this.keyName = keyName;
        }

        public String getKeyName() {
            return keyName;
        }
        public void setKeyName(String keyName) {
            this.keyName = keyName;
        }
        public boolean isReleaseActive() { return releaseActive; }
        public Action withReleaseActive(boolean releaseActive) { this.releaseActive = releaseActive; return this; }

        public abstract void execute(boolean keyPressed);
    }
}
