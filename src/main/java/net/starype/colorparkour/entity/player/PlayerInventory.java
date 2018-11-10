package net.starype.colorparkour.entity.player;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;

public class PlayerInventory extends BaseAppState implements ScreenController {

    private SimpleApplication main;
    private final NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;

    public PlayerInventory(SimpleApplication main) {
        this.main = main;
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                main.getAssetManager(),
                main.getInputManager(),
                main.getAudioRenderer(),
                main.getGuiViewPort());
        main.getViewPort().addProcessor(niftyDisplay);
        this.nifty = niftyDisplay.getNifty();
        createGUI();
    }

    @Override
    public void onEnable() { }

    @Override public void onDisable() { }

    public void show() {
        nifty.gotoScreen("start");
    }
    public void hide() {
        nifty.exit();
    }

    private void createGUI() {

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("configs/gui_settings.xml");
        nifty.registerScreenController(this);

        nifty.addScreen("start", new ScreenBuilder("Hello Nifty Screen") {{
            controller(new DefaultScreenController()); // Screen properties

            // <layer>
            layer(new LayerBuilder("Layer_ID") {{
                childLayoutVertical(); // layer properties, add more...

                // <panel>
                panel(new PanelBuilder("Panel_ID") {{
                    childLayoutCenter(); // panel properties, add more...

                    // GUI elements
                    control(new ButtonBuilder("QuitButton", "Quit") {{
                        alignCenter();
                        valignCenter();
                        height("10%");
                        width("10%");
                        visibleToMouse(true);
                        interactOnClick("quitGame()");
                    }});

                    //.. add more GUI elements here

                }});
                // </panel>
            }});
            // </layer>
        }}.build(nifty));
        // </screen>
    }
    public void quitGame() {
        System.out.println("STOPPED");
        main.stop();
    }
    public Nifty getNifty() { return nifty; }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    @Override
    protected void initialize(Application app) {

    }

    @Override
    protected void cleanup(Application app) {

    }

}