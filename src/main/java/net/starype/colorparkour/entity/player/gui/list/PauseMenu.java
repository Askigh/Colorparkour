package net.starype.colorparkour.entity.player.gui.list;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.player.gui.PlayerInventory;

import static net.starype.colorparkour.core.ColorParkourMain.HEIGHT;
import static net.starype.colorparkour.core.ColorParkourMain.WIDTH;

public class PauseMenu extends Container {

    public PauseMenu(PlayerInventory inventory) {
        setLocalTranslation((WIDTH - 400) / 2, HEIGHT / 2 + 100, 0);
        loadButtons(inventory);
    }

    private void loadButtons(PlayerInventory inventory) {
        ColorParkourMain main = inventory.getMainInstance();
        Button resume = addChild(new Button("Resume"));
        resume.setColor(ColorRGBA.Black);
        resume.addClickCommands(source -> {
            inventory.activatePlayer(false);
            main.getRpcManager().update(new DiscordRichPresence
                    .Builder("Level: " + main.getModuleManager().getCurrentModule().getLevelName())
                    .setDetails("In Game"+ main.getModuleManager().getCurrentModule().getLevelName())
                    .setStartTimestamps(main.getRpcManager().getStartTimestamps())
                    .setBigImage("green_-_1024", "ColorParkour " + ColorParkourMain.VERSION)
                    .build()
            );
        });
        Button restartLevel = addChild(new Button("Restart level"));
        restartLevel.addClickCommands(source -> {
            main.getPlayer().resetPosition(main.getModuleManager().getCurrentModule());
            inventory.activatePlayer(true);
            main.getRpcManager().setStartTimestamps(System.currentTimeMillis());
            main.getRpcManager().update(new DiscordRichPresence
                    .Builder("Level: " + main.getModuleManager().getCurrentModule().getLevelName())
                    .setDetails("In Game")
                    .setStartTimestamps(main.getRpcManager().getStartTimestamps())
                    .setBigImage("green_-_1024", "ColorParkour " + ColorParkourMain.VERSION)
                    .build()
            );
        });
        Button previousLevel = addChild(new Button("Previous level"));
        previousLevel.setColor(ColorRGBA.Black);
        previousLevel.addClickCommands(source -> {
            main.getModuleManager().previous();
            inventory.activatePlayer(true);
        });
        restartLevel.setColor(ColorRGBA.Black);
        restartLevel.setLocalTranslation(new Vector3f(50, 200, 0));

        Button skyBox = addChild(new Button("Enable Sky"));
        skyBox.setColor(ColorRGBA.Green);
        skyBox.addClickCommands(source -> {
            skyBox.setColor(skyBox.getColor().equals(ColorRGBA.Green) ? ColorRGBA.Black : ColorRGBA.Green);
            if(skyBox.getColor().equals(ColorRGBA.Black)) {
                main.setSkyEnabled(false);
            } else {
                main.setSkyEnabled(true);
            }
        });
        Button changeFOV = addChild(new Button("Enable FOV Change using arrows"));
        changeFOV.setColor(ColorRGBA.Green);
        changeFOV.addClickCommands(source -> {
            inventory.setChangeFOV(!inventory.isChangeFOVActive());
            changeFOV.setColor(changeFOV.getColor().equals(ColorRGBA.Black) ? ColorRGBA.Green : ColorRGBA.Black);
        });
        Button contrSettings = addChild(new Button("Control settings"));
        contrSettings.setColor(ColorRGBA.Black);
        contrSettings.addClickCommands(source -> {
            inventory.hideAll();
            inventory.getSettings().show();
        });
        Button backToMenu = addChild(new Button("Back to the menu"));
        backToMenu.setColor(ColorRGBA.Black);
        backToMenu.addClickCommands(source -> inventory.showOnly(1));

        Button quitButton = addChild(new Button("Quit Game"));
        quitButton.setColor(ColorRGBA.Black);
        quitButton.addClickCommands((Command<Button>) source -> {
            main.stop();
        });
        quitButton.setLocalTranslation(0, 0, 0);
    }
}
