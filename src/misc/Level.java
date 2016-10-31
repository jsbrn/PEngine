package misc;

import java.util.ArrayList;
import scene.SceneObject;

public class Level {
    
    public ArrayList<SceneObject> ALL_OBJECTS, DISTANT_OBJECTS, BACKGROUND_OBJECTS, NORMAL_OBJECTS, FOREGROUND_OBJECTS;
    private ArrayList[] layers;
    
    public String NAME = "", AMBIENT_SOUND = "", BG_MUSIC = "";
    public int R1 = 0, G1 = 0, B1 = 0, R2 = 0, G2 = 0, B2 = 0, 
            R3 = 0, G3 = 0, B3 = 0, AMBIENT_INTENSITY = 0, WIDTH = 128, HEIGHT = 128, ZOOM = 4;
    public boolean LOOP_BG_MUSIC = true, LOOP_AMBIENT_SOUND = true, PLAY_BG_MUSIC_AUTOMATICALLY = true,
            PLAY_AMBIENT_SOUND_AUTOMATICALLY = true;
    public float MUSIC_VOLUME = 1, AMBIENT_VOLUME = 1;
    
    public int[] SPAWN_COORD = {0, 0}, CAM_COORD = {0, 0};
    
    public ArrayList<Script> SCRIPTS;
    
    public Level() {
        this.ALL_OBJECTS = new ArrayList<SceneObject>();
        this.DISTANT_OBJECTS = new ArrayList<SceneObject>();
        this.BACKGROUND_OBJECTS = new ArrayList<SceneObject>();
        this.NORMAL_OBJECTS = new ArrayList<SceneObject>();
        this.FOREGROUND_OBJECTS = new ArrayList<SceneObject>();
        this.SCRIPTS = new ArrayList<Script>();
        this.layers = new ArrayList[]{DISTANT_OBJECTS, BACKGROUND_OBJECTS, NORMAL_OBJECTS, FOREGROUND_OBJECTS};
    }
    
    public boolean scriptExists(String name) {
        for (Script o: SCRIPTS) {
            if (o.NAME.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public void add(SceneObject o) {
        if (ALL_OBJECTS.contains(o) == false) {
            ALL_OBJECTS.add(o);
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
        ALL_OBJECTS.remove(o);
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
            if (i != o.LAYER) {
                layers()[i].remove(o);
            } else {
                if (layers()[i].contains(o) == false) {
                    layers()[i].add(o);
                }
            }
        }
    }
    
    public ArrayList[] layers() {
        return layers;
    }
}
