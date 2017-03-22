package project;

import java.util.ArrayList;
import project.objects.SceneObject;

public class Level {
    
    public static final int ALL_OBJECTS = 0, DISTANT_OBJECTS = 1, BACKGROUND_OBJECTS = 2, 
            MIDDLE_OBJECTS = 3, FOREGROUND_OBJECTS = 4;
    private ArrayList[] objects;
    
    private String name = "", ambient_sound = "", bg_music = "";
    private int[] bg_color_top, bg_color_bottom, lighting_color;
    private int lighting_intensity = 0, width = 128, height = 128, zoom = 4;
    private boolean loop_bg_music = true, loop_ambient_sound = true, auto_bg_music = true,
            auto_ambient_sound = true;
    private float bg_music_vol = 1, ambient_sound_volume = 1;
    
    private int[] player_spawn = {0, 0}, camera_spawn = {0, 0};
    
    public Level() {
        this.objects = new ArrayList[5];
        for (int i = 0; i < objects.length; i++) {
            this.objects[i] = new ArrayList<SceneObject>();
        }
    }
    
    public int[] playerSpawn() { return player_spawn; }
    public int[] cameraSpawn() { return camera_spawn; }
    
    public int[] getTopBGColor() { return bg_color_top; }
    public int[] getBottomBGColor() { return bg_color_bottom; }
    public int[] getLightingColor() { return lighting_color; }
    public int getLightIntensity() { return lighting_intensity; }
    
    public int[] dimensions() { return new int[]{width, height}; }
    public int getZoom() { return zoom; }
    
    public ArrayList<SceneObject> getObjects(int layer) { return objects[layer]; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public void add(SceneObject o) {
        if (getObjects(ALL_OBJECTS).contains(o) == false) {
            getObjects(ALL_OBJECTS).add(o);
            moveToLayer(o.getLayer(), o);
        }
    }
    
    public void moveForward(SceneObject o) {
        for (int i = 0; i != layers().length; i++) {
            if (i == o.getLayer()) {
                int orig = layers()[i].indexOf(o);
                if (orig < layers()[i].size()-1) {
                    layers()[i].remove(o);
                    layers()[i].add(orig+1, o);
                }
            }
        }
    }
    
    public void removeObject(SceneObject o) {
        getObjects(ALL_OBJECTS).remove(o);
        for (int i = 0; i != layers().length; i++) {
            layers()[i].remove(o);
        }
    }
    
    public void moveBackward(SceneObject o) {
        for (int i = 0; i != layers().length; i++) {
            if (i == o.getLayer()) {
                int orig = layers()[i].indexOf(o);
                if (orig > 0) {
                    Object prev = layers()[i].get(orig-1);
                    layers()[i].add(orig+1, prev);
                    layers()[i].remove(prev);
                }
            }
        }
    }
    
    public void moveToLayer(int layer, SceneObject o) {
        for (int i = 0; i != layers().length; i++) {
            if (i != o.getLayer()) {
                layers()[i].remove(o);
            } else {
                if (layers()[i].contains(o) == false) {
                    layers()[i].add(o);
                }
            }
        }
    }
    
    public void resize(double x, double y) {
        width = (int)(width + x < 1 ? 1 : width + x);
        height = (int)(height + y < 1 ? 1 : height + y);
    }
    
    public boolean containsObject(String name) {
        for (SceneObject o: getObjects(ALL_OBJECTS)) {
            if (o.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList[] layers() {
        return objects;
    }
}
