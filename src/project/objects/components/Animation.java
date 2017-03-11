package project.objects.components;

import java.util.ArrayList;
import project.Project;
import project.objects.SceneObject;

public class Animation {
    
    private ArrayList<Integer> widths = new ArrayList<Integer>(), heights = new ArrayList<Integer>();
    private String name = "", spritesheet = "";
    private boolean loop = false, locked = false;
    private int frame_dur = 100;
    
    public Animation() {
        
    }
    
    public void removeFrame(int i) {
        widths.remove(i);
        heights.remove(i);
    }
    
    public boolean equalTo(Animation a) {
        if (!(name.equals(a.name))) return false;
        if (!(spritesheet.equals(a.spritesheet))) return false;
        if (loop != a.loop) return false;
        for (int i = 0; i != widths.size(); i++) {
            if (widths.get(i) != a.widths.get(i)) return false;
            if (heights.get(i) != a.heights.get(i)) return false;
        }
        return true;
    }
    
    public void copyTo(Animation new_a) {
        new_a.name = name;
        new_a.loop = loop;
        new_a.spritesheet = spritesheet;
        new_a.widths.clear();
        new_a.heights.clear();
        new_a.widths.addAll(widths);
        new_a.heights.addAll(heights);
    }
    
    /**
     * Adds a frame. If any of the parametres are 0, then this method will not work.
     */
    public void addFrame(int width, int height, int duration) {
        if (duration <= 0 || width <= 0 || height <= 0) {
            return;
        }
        widths.add(width);
        heights.add(height);
    }
    
}
