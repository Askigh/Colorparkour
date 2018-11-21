package net.starype.colorparkour.entity.player.gui.list;

import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.event.*;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.player.gui.ColorParkourGUI;
import net.starype.colorparkour.entity.player.gui.PlayerInventory;

import static net.starype.colorparkour.core.ColorParkourMain.*;

import java.util.ArrayList;
import java.util.List;


public class ControlSettings implements ActionListener, RawInputListener {

    private final List<Action> actions;
    private final ColorParkourMain main;
    private final ControlSettings instance = this;
    private final Container controlMenu;
    private Action keySelected = null;
    private boolean selecting = false;
    private Button currentButton = null;
    private PlayerInventory inventory;
    private ColorParkourGUI gui;

    public ControlSettings(ColorParkourMain main, PlayerInventory inventory) {
        main.getInputManager().addRawInputListener(this);
        this.main = main;
        this.actions = new ArrayList<>();
        this.controlMenu = new Container();
        this.inventory = inventory;
        createMenu();
        gui = new ColorParkourGUI("controls", controlMenu).withInputActions(true);
        gui.setActive(false);
        inventory.getGUIS().add(gui);
    }

    private void createMenu() {
        controlMenu.setPreferredSize(new Vector3f(100, 400, 0));
        controlMenu.setLocalTranslation(WIDTH/2, HEIGHT/1.5f, 0);
        Button forward = controlMenu.addChild(new Button("Forward: W"));
        forward.addClickCommands(source -> {
            this.keySelected = findByName("Forward");
            selecting = true;
            this.currentButton = forward;
        });
        Button backward = controlMenu.addChild(new Button("Backward: S"));
        backward.addClickCommands(source -> {
            this.keySelected = findByName("Backward");
            selecting = true;
            this.currentButton = backward;
        });
        Button left = controlMenu.addChild(new Button("Left: A"));
        left.addClickCommands(source -> {
            this.keySelected = findByName("Left");
            selecting = true;
            this.currentButton = left;
        });
        Button right = controlMenu.addChild(new Button("Right: D"));
        right.addClickCommands(source -> {
            this.keySelected = findByName("Right");
            selecting = true;
            this.currentButton = right;
        });
        Button jump = controlMenu.addChild(new Button("Jump: Space"));
        jump.addClickCommands(source -> {
            this.keySelected = findByName("Jump");
            selecting = true;
            this.currentButton = jump;
        });
        Button sprint = controlMenu.addChild(new Button("Sprint: F"));
        sprint.addClickCommands(source -> {
            this.keySelected = findByName("Sprint");
            selecting = true;
            this.currentButton = sprint;
        });
        for(Spatial node : controlMenu.getChildren()) {
            if(node instanceof Button) {
                ((Button) node).setColor(ColorRGBA.Black);
            }
        }
    }

    public void show() {
        inventory.showOnly(inventory.getGUIS().indexOf(gui));
        GuiGlobals.getInstance().setCursorEventsEnabled(true);
    }
    public void hide() {
        inventory.hide(inventory.getGUIS().indexOf(gui));
        selecting = false;
    }
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        for(Action action : actions) {
            if(action.name.equals(name) && (isPressed || action.releaseActive)) {
                action.execute(isPressed);
            }
        }
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
        if(keySelected == null || !selecting || currentButton == null) {
            return;
        }
        keySelected.setTrigger(evt.getKeyCode());
        currentButton.setText(keySelected.getName() + ": "+ (evt.getKeyChar()+"").toUpperCase());
        if(currentButton.getText().equals("Jump: ")) {
            currentButton.setText("Jump: SPACE");
        }
        selecting = false;
    }

    public Action findByName(String name) {
        for(Action action : actions) {
            if(action.getName().equals(name)) {
                return action;
            }
        }
        return null;
    }
    public abstract class Action {

        private final String name;
        private KeyTrigger trigger;
        private final boolean releaseActive;

        public Action(final String name, int key, boolean releaseActive) {
            this.name = name;
            this.trigger = new KeyTrigger(key);
            this.releaseActive = releaseActive;
            main.getInputManager().addMapping(name, trigger);
            main.getInputManager().addListener(instance, name);
            actions.add(this);
        }

        public void setTrigger(int key) {
            actions.remove(this);
            main.getInputManager().deleteMapping(name);
            this.trigger = new KeyTrigger(key);
            main.getInputManager().addMapping(name, trigger);
            main.getInputManager().addListener(instance, name);
            actions.add(this);
        }

        public abstract void execute(boolean keyPressed);

        public String getName() {
            return name;
        }
        public KeyTrigger getTrigger() { return trigger; }
    }

    /////////////////
    /////////////////
    @Override
    public void endInput() { }
    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) { }
    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) { }
    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) { }
    @Override
    public void beginInput() { }
    @Override
    public void onTouchEvent(TouchEvent evt) { }
    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) { }
}
