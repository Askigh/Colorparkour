package net.starype.colorparkour.rpc;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.starype.colorparkour.core.ColorParkourMain;

/**
 * This file is a part of Colorparkour.
 *
 * @author Clément (Cleymax) {@literal <cleymaxpro@gmail.com>}
 * Created the 25/11/2018 at 22:33.
 */

public class DiscordRPCManager {

    private final ColorParkourMain main;
    private long start;

    public DiscordRPCManager(ColorParkourMain main) {
        this.main = main;
    }

    public void init() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().build();
        DiscordRPC.discordInitialize("513345146562019339", handlers, true);
    }

    public void update(DiscordRichPresence discordRichPresence) {
        DiscordRPC.discordUpdatePresence(discordRichPresence);
    }

    public void stop() {
        DiscordRPC.discordShutdown();
    }

    public long getStartTimestamps() {
        return start;
    }

    public void setStartTimestamps(long currentTimeMillis) {
        this.start = currentTimeMillis;
    }

    public void update(String red) {
        update(new DiscordRichPresence
                .Builder("Level: " + main.getModuleManager().getCurrentModule().getLevelName())
                .setDetails("In Game")
                .setStartTimestamps(main.getRpcManager().getStartTimestamps())
                .setBigImage(red + "_-_1024", "ColorParkour " + "v0.0.3")
                .build()
        );
    }
}
