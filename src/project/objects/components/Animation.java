package project.objects.components;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import misc.MiscMath;
import project.Project;
import project.objects.SceneObject;

public class Animation {
    
    private ArrayList<Integer> widths = new ArrayList<Integer>(), heights = new ArrayList<Integer>();
    private String name = "", spritesheet = "";
    private boolean loop = false, locked = false;
    private int frame_dur = 100;
    
    public Animation() {
        
    }
    
    public String getName() {
        return name;
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
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("a\n");
            bw.write("n="+name+"\n");
            bw.write("s="+spritesheet+"\n");
            bw.write("w="+MiscMath.integersToString(widths)+"\n");
            bw.write("h="+MiscMath.integersToString(heights)+"\n");
            bw.write("lk="+locked+"\n");
            bw.write("lp="+loop+"\n");
            bw.write("/a\n");
        } catch (IOException ex) {
            Logger.getLogger(Animation.class.getName()).log(Level.SEVERE, null, ex);
        }
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
