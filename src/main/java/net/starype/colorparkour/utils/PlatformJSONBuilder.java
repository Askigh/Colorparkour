package net.starype.colorparkour.utils;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jme3.math.ColorRGBA;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.platform.ColoredPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.Set;

public enum PlatformJSONBuilder {

    COLORED(ColoredPlatform.class, "normal", null),

    ;

    public static final Logger LOGGER = LoggerFactory.getLogger(PlatformJSONBuilder.class);
    private static final Set<String> universalArguments;

    static {
        universalArguments = Sets.newHashSet("name", "type", "size", "position", "rgba");
    }

    private final Set<String> unsettableParameters;
    private final Class<? extends ColoredPlatform> platformClass;
    private final String platformName;

    PlatformJSONBuilder(Class<? extends ColoredPlatform> platformClass, String platformName, Set<String> unsettableParameters) {
        this.platformClass = platformClass;
        this.platformName = platformName;
        this.unsettableParameters = unsettableParameters;
    }

    private static Optional<PlatformJSONBuilder> getPlatformType(String platformType) {
        platformType = platformType != null && !platformType.equals("") ? platformType : COLORED.platformName;
        for (PlatformJSONBuilder type : values())
            if (type.platformName.equalsIgnoreCase(platformType))
                return Optional.of(type);
        return Optional.empty();
    }

    // TODO: Complete json reading
    public static Optional<ColoredPlatform> loadPlatform(JsonElement args, ColorParkourMain main) throws NullPointerException {
        JsonObject platform = args.getAsJsonObject();
        Optional<PlatformJSONBuilder> typeOpt = getPlatformType(platform.get("type").getAsString());
        if (!typeOpt.isPresent()) {
            LOGGER.warn("Entered type isn't valid ! (type:" + platform.get("type").getAsString() + ")");
            return Optional.empty();
        }

        Constructor constructor = typeOpt.get().getPlatformClass().getConstructors()[0];
        Parameter[] parameters = constructor.getParameters();

        float[] color = getFloatArray(platform.getAsJsonArray("rgba"));

        Object[] objects = new Object[parameters.length];
        objects[0] = main;
        objects[1] = main.getCollisionManager();
        objects[2] = getFloatArray(platform.getAsJsonArray("size"));
        objects[parameters.length - 1] = platform.get("name");
        objects[parameters.length - 2] = new ColorRGBA(color[0], color[1], color[2], color[3]);

        JsonObject declaredParameters = platform.getAsJsonObject();
        for (String s : universalArguments)
            declaredParameters.remove(s);

        //TODO: fix this shit
        LOGGER.debug(declaredParameters.toString());
        for (String s : declaredParameters.keySet()) {
            for (Parameter param : parameters) {
                if (param.getName().equals(s)) {
                    LOGGER.debug("Found : " + s + " !");
                } else {
                    LOGGER.debug("Param " + param.getName() + " | element: " + s);
                }
            }
        }

        /*
        Parameter[] parameters = starypeCommand.getExecutorMethod().getParameters();
        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType() == String[].class) objects[i] = args;
            else if (parameters[i].getType() == User.class) objects[i] = message == null ? null : message.getAuthor();
            else if (parameters[i].getType() == TextChannel.class)
                objects[i] = message == null ? null : message.getTextChannel();
            else if (parameters[i].getType() == PrivateChannel.class)
                objects[i] = message == null ? null : message.getPrivateChannel();
            else if (parameters[i].getType() == Guild.class) objects[i] = message == null ? null : message.getGuild();
            else if (parameters[i].getType() == String.class) objects[i] = starypeCommand.getName();
            else if (parameters[i].getType() == Message.class) objects[i] = message;
            else if (parameters[i].getType() == JDA.class) objects[i] = StarypeBot.getInstance().getJDA();
            else if (parameters[i].getType() == MessageChannel.class) objects[i] = message.getChannel();
        }
        try {
            starypeCommand.getExecutorMethod().invoke(starypeCommand.getCommandHolder(), objects);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }*/
        return Optional.empty();
    }

    private static float[] getFloatArray(JsonArray array) {
        float[] fData = new float[array.size()];
        for (int i = 0; i < array.size(); i++)
            fData[i] = Float.parseFloat(array.get(i).getAsString());
        return fData;
    }

    public Class<? extends ColoredPlatform> getPlatformClass() {
        return platformClass;
    }
}
