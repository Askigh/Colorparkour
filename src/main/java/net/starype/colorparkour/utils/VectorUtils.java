package net.starype.colorparkour.utils;

import com.jme3.math.Vector3f;

public class VectorUtils {

    public static boolean isVectorZero(Vector3f vec) {
        return vec.x == 0 && vec.y == 0 && vec.z == 0;
    }

    public static boolean areValuesSimilar(Vector3f v1, Vector3f v2) {
        return v1.x == v2.x && v1.y == v2.y && v1.z == v2.z;
    }
}
