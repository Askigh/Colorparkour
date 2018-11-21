package net.starype.colorparkour.entity.player.gui.list;

import com.jme3.math.ColorRGBA;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.component.IconComponent;
import net.starype.colorparkour.entity.player.gui.PlayerInventory;

import static net.starype.colorparkour.core.ColorParkourMain.HEIGHT;
import static net.starype.colorparkour.core.ColorParkourMain.WIDTH;

public class GameMenu extends Container {

    public GameMenu(PlayerInventory inventory) {
        setLocalTranslation((WIDTH - 400) / 2, HEIGHT / 2 + 100, 0);
        createButtons(inventory);
    }

    private void createButtons(PlayerInventory inventory) {
        Button play = addChild(new Button(null));
        play.setBackground(new IconComponent("assets/icons/play.png"));
        play.setColor(ColorRGBA.Orange);
        play.addClickCommands((Command<Button>) source -> {
            inventory.getMainInstance().startGame();
            inventory.activatePlayer(true);
        });
    }
}
