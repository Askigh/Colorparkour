package net.starype.colorparkour.utils;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import net.starype.colorparkour.core.ColorParkourMain;
import net.starype.colorparkour.entity.platform.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public enum PlatformJSONBuilder {

    COLORED(ColoredPlatform.class, "normal"),
    DOUBLE_JUMP(DoubleJumpPlatform.class, "doubleJump"),
    ICE(IcePlatform.class, "ice"),
    MOVING(MovingPlatform.class, "moving"),
    STICKY(StickyMovingPlatform.class, "sticky");

    public static final Logger LOGGER = LoggerFactory.getLogger(PlatformJSONBuilder.class);
    private static final Set<String> universalArguments;

    static {
        universalArguments = Sets.newHashSet("name", "type", "size", "position", "rgba");
    }

    private final Class<? extends ColoredPlatform> platformClass;
    private final String platformName;

    PlatformJSONBuilder(Class<? extends ColoredPlatform> platformClass, String platformName) {
        this.platformClass = platformClass;
        this.platformName = platformName;
    }

    private static Optional<PlatformJSONBuilder> getPlatformType(String platformType) {
        platformType = platformType != null && !platformType.equals("") ? platformType : COLORED.platformName;
        for (PlatformJSONBuilder type : values())
            if (type.platformName.equalsIgnoreCase(platformType))
                return Optional.of(type);
        return Optional.empty();
    }

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
        objects[0] = main.getCollisionManager();
        objects[1] = main;
        objects[2] = getFloatArray(platform.getAsJsonArray("size"));
        objects[parameters.length - 1] = platform.get("name").getAsString();
        objects[parameters.length - 2] = new ColorRGBA(color[0]/255, color[1]/255, color[2]/255, color[3]/255);

        JsonObject declaredParameters = platform.getAsJsonObject();
        for (String s : universalArguments)
            declaredParameters.remove(s);

        //TODO: fix this shit
        for (int i = 3; i < parameters.length-2; i++) {
            Parameter param = parameters[i];
            for (String configuredPoint : declaredParameters.keySet()) {

                if(platform.get(configuredPoint) instanceof JsonArray){
                    Class arrayType = getArrayType(platform.get(configuredPoint).getAsJsonArray());
                    if(arrayType == null)
                        continue;
                    LOGGER.debug("Array type: " + arrayType.getCanonicalName() + " | ConfName: " + configuredPoint + " | Current param: " + param.getType().getCanonicalName());
                    //Float array
                    if(arrayType.getCanonicalName().equals(float.class.getCanonicalName())){
                        float[] datas = getFloatArray(platform.get(configuredPoint).getAsJsonArray());
                        //param is Vector3f
                        if(param.getType().getCanonicalName().equals(Vector3f.class.getCanonicalName())){
                            LOGGER.debug("This param is a Vector3f");
                            objects[i] = new Vector3f(datas[0], datas[1], datas[2]);

                            //param is a float array
                        } else if (param.getType().getCanonicalName().equals(float.class.getCanonicalName() + "[]")) {
                            LOGGER.debug("This param is a float[]");
                            objects[i] = datas;
                        }
                    } else {
                        //String array
                        if(param.getType().getCanonicalName().equals(String.class.getCanonicalName() + "[]")){
                            LOGGER.debug("This param is a String[]");
                            objects[i] = getStringArray(platform.get(configuredPoint).getAsJsonArray());
                        }
                    }

                } else if(platform.get(configuredPoint) instanceof JsonPrimitive){
                    JsonPrimitive element = (JsonPrimitive)platform.get(configuredPoint);

                    if(element.isString() && param.getType().getCanonicalName().equals(String.class.getCanonicalName())){
                        LOGGER.debug("This param is a String");
                        objects[i] = element.getAsString();
                    } else if(element.isNumber() && param.getType().getCanonicalName().equals(Number.class.getCanonicalName())){
                        LOGGER.debug("This param is a Number");
                        objects[i] = element.getAsNumber();
                    }
                }
            }
        }
        LOGGER.debug(Arrays.asList(parameters).toString());
        LOGGER.debug(Arrays.asList(objects).toString());
        /*try {

            return Optional.of((ColoredPlatform) constructor.newInstance(objects));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }*/

        return Optional.empty();
    }

    private static Class getArrayType(JsonArray array){
        if(array.size() == 0)
            return null;
        boolean isFloat = true;
        try { Float.parseFloat(array.get(0).getAsString()); } catch (NumberFormatException e){ isFloat = false; }
        return isFloat ? float.class : String.class;
    }

    private static float[] getFloatArray(JsonArray array) {
        float[] fData = new float[array.size()];
        for (int i = 0; i < array.size(); i++)
            fData[i] = Float.parseFloat(array.get(i).getAsString());
        return fData;
    }

    private static String[] getStringArray(JsonArray array){
        String[] sData = new String[array.size()];
        for (int i = 0; i < array.size(); i++)
            sData[i] = array.get(i).getAsString();
        return sData;
    }

    public Class<? extends ColoredPlatform> getPlatformClass() {
        return platformClass;
    }
}
