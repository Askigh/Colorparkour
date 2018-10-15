package net.starype.colorparkour.utils;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

// this class is pretty much totally useless, it will be deleted
public class ShapesSY {

    public static Geometry loadCube(AssetManager assetManager) {
        Box box = new Box(1, 1, 1);
        Geometry geometry = new Geometry("box", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture t = assetManager.loadTexture("/assets/Textures/logo.png");
        mat.setTexture("ColorMap", t);

        geometry.setMaterial(mat);
        geometry.setLocalTranslation(0,0,5);
        return geometry;
    }
}
