package project;

import java.util.ArrayList;
import project.objects.SceneObject;

public class Level {
    
    private ArrayList<SceneObject> all_objects, distant_objects, bg_objects, mid_objects, fg_objects;
    private ArrayList[] layers;
    
    private String name = "", ambient_sound = "", bg_music = "";
    private int[] bg_color_top, bg_color_bottom, lighting_color;
    private int lighting_intensity = 0, width = 128, height = 128, zoom = 4;
    private boolean loop_bg_music = true, loop_ambient_sound = true, auto_bg_music = true,
            auto_ambient_sound = true;
    private float bg_music_vol = 1, ambient_sound_volume = 1;
    
    private int[] player_spawn = {0, 0}, camera_spawn = {0, 0};
    
    public Level() {
        this.all_objects = new ArrayList<SceneObject>();
        this.distant_objects = new ArrayList<SceneObject>();
        this.bg_objects = new ArrayList<SceneObject>();
        this.mid_objects = new ArrayList<SceneObject>();
        this.fg_objects = new ArrayList<SceneObject>();
        this.layers = new ArrayList[]{distant_objects, bg_objects, mid_objects, fg_objects};
    }
    
    public void add(SceneObject o) {
        if (all_objects.contains(o) == false) {
            all_objects.add(o);
            moveToLayer(o.LAYER, o);
        }
    }
    
    public void moveForward(SceneObject o) {
        for (int i = 0; i != layers().length; i++) {
            if (i == o.LAYER) {
                int orig = layers()[i].indexOf(o);
                if (orig < layers()[i].size()-1) {
                    layers()[i].remove(o);
                    layers()[i].add(orig+1, o);
                }
            }
        }
    }
    
    public void removeObject(SceneObject o) {
        all_objects.remove(o);
        for (int i = 0; i != layers().length; i++) {
            layers()[i].remove(o);
        }
    }
    
    public void moveBackward(SceneObject o) {
        for (int i = 0; i != layers().length; i++) {
            if (i == o.LAYER) {
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
            if (i != o.layer) {
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
        for (SceneObject o: all_objects) {
            if (o.name.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList[] layers() {
        return layers;
    }
}
