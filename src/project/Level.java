package project;

import java.awt.Color;
import java.util.ArrayList;
import misc.MiscMath;
import project.objects.SceneObject;

public class Level {
    
    public static final int ALL_OBJECTS = 0, DISTANT_OBJECTS = 1, BACKGROUND_OBJECTS = 2, 
            MIDDLE_OBJECTS = 3, FOREGROUND_OBJECTS = 4;
    private ArrayList<SceneObject> layers[];
    
    private String name = "", ambient_sound = "", bg_music = "";
    private Color bg_color_top, bg_color_bottom, lighting_color;
    private int width = 128, height = 128, zoom = 4;
    private double lighting_intensity = 0;
    private boolean loop_bg_music = true, loop_ambient_sound = true, auto_bg_music = true,
            auto_ambient_sound = true;
    private float bg_music_vol = 1, ambient_sound_volume = 1;
    
    private int[] player_spawn = {0, 0}, camera_spawn = {0, 0};
    
    public Level() {
        this.layers = new ArrayList[5];
        for (int i = 0; i < layers.length; i++) {
            this.layers[i] = new ArrayList<SceneObject>();
        }
    }
    
    public int[] playerSpawn() { return player_spawn; }
    public int[] cameraSpawn() { return camera_spawn; }
    public void setPlayerSpawn(int x, int y) { player_spawn = new int[]{x, y}; }
    public void setCameraSpawn(int x, int y) { camera_spawn = new int[]{x, y}; }
    
    public Color getTopBGColor() { return bg_color_top; }
    public Color getBottomBGColor() { return bg_color_bottom; }
    public Color getLightingColor() { return lighting_color; }
    public double getLightIntensity() { return lighting_intensity; }
    
    public void setLightingColor(Color c) { lighting_color = c; }
    public void setTopBGColor(Color c) { bg_color_top = c; }
    public void setBottomBGColor(Color c) { bg_color_bottom = c; }
    public void setLightingIntensity(double i) { lighting_intensity = i; }
    
    public int[] dimensions() { return new int[]{width, height}; }
    public int getZoom() { return zoom; }
    
    public ArrayList<SceneObject> getObjects(int layer) { return layers[layer]; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public void add(SceneObject o) {
        if (getObjects(ALL_OBJECTS).contains(o) == false) {
            getObjects(ALL_OBJECTS).add(o);
            moveToLayer(o.getLayer(), o);
        }
    }
    
    public SceneObject getObject(int onscreen_x, int onscreen_y) {
        for (int i = layers[ALL_OBJECTS].size()-1; i != -1; i--) {
            SceneObject o = layers[ALL_OBJECTS].get(i);
            int[] on_screen = o.getOnscreenCoordinates();
            if (MiscMath.pointIntersects(onscreen_x, onscreen_y, 
                    on_screen[0], on_screen[1], o.getOnscreenWidth(), o.getOnscreenHeight())) {
                return o;
            }
        }
        return null;
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
        return layers;
    }
}
