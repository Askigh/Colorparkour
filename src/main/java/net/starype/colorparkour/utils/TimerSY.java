package net.starype.colorparkour.utils;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import net.starype.colorparkour.core.ColorParkourMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * @author Lolilolulolilol
 */
public class TimerSY {

    public static final Logger LOGGER = LoggerFactory.getLogger(TimerSY.class);

    private final String timerPrefix, pattern, timerID;
    private float timeStamp;
    private BitmapText timerText;


    /**
     * Sets up the timer.
     *
     * @param guiFont      See {@link #loadTimer(BitmapFont, ColorRGBA, Vector2f)} guiFont parameter
     * @param color        See {@link #loadTimer(BitmapFont, ColorRGBA, Vector2f)} color parameter
     * @param textPosition See {@link #loadTimer(BitmapFont, ColorRGBA, Vector2f)} textPosition parameter
     * @param timerPrefix  This parameter defines which text should be displayed before the time
     * @param pattern      The defined time pattern.
     */
    public TimerSY(BitmapFont guiFont, ColorRGBA color, Vector2f textPosition, String timerPrefix, String pattern, String timerID) {
        this.timerPrefix = timerPrefix;
        this.pattern = pattern != null ? pattern : "HH:mm:ss";
        this.timerID = timerID;
        loadTimer(guiFont, color, textPosition);

        LOGGER.debug("Timer " + timerID + "'s initialization finished !");
    }

    /**
     * This method is updating the timeStamp. This method should be called each frame update.
     *
     * @param tpf This parameter is the time per frame.
     */
    private void updateTime(float tpf) {
        timeStamp += tpf;
    }

    /**
     * @return the timestamp of the timer.
     */
    public float getTimeStamp() {
        return timeStamp;
    }

    /**
     * This method updates the timer.
     */
    public void updateTimer(float tpf) {
        updateTime(tpf);
        timerText.setText(timerPrefix + getFormattedTime());
    }

    /**
     * @return the formatted time depending on the given pattern.
     */
    public String getFormattedTime() {
        return new SimpleDateFormat(pattern != null ? pattern : "HH:mm:ss").format(timeStamp * 1000);
    }

    /**
     * @return the BitmapText linked object.
     */
    public BitmapText getBitmapText() {
        timerText.setText(timerPrefix + getFormattedTime());
        return timerText;
    }

    /**
     * This method load the BitmapText object.
     *
     * @param guiFont  The text font.
     * @param color    The text color.
     * @param position The text position.
     */
    private BitmapText loadTimer(BitmapFont guiFont, ColorRGBA color, Vector2f position) {
        timerText = new BitmapText(guiFont, false);
        timerText.setSize(guiFont.getCharSet().getRenderedSize()*2);
        timerText.setColor(color);
        timerText.setText("");
        timerText.setLocalTranslation(position.x, ColorParkourMain.HEIGHT, 0);
        LOGGER.debug("Timer " + timerID + " : BitmapText initialized !");
        return timerText;
    }
}
