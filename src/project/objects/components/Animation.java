package project.objects.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import project.Level;
import project.Project;
import project.objects.SceneObject;

public class Animation {
    
    private String name, spritesheet;
    private boolean locked;
    private int frame_dur, frame_count; //dur in milliseconds
    
    public Animation() {
        this.spritesheet = "";
        this.name = "";
        this.locked = false;
        this.frame_count = 1;
        this.frame_dur = 100;
    }
    
    public void autoName(SceneObject o) {
        int i = 1;
        while (true) {
            String n = "animation"+(i > 1 ? ""+i : "");
            if (!o.containsAnimation(n)) { setName(n); break; }
            i++;
        }
    }
    
    public static boolean isValidName(String name) {
        if (name == null) return false;
        if (name.trim().length() == 0) return false;
        //remove all valid chars so that the invalids are leftover.
        //if your string is empty then there are no invalid chars
        return name.replaceAll("^[a-zA-Z_$][a-zA-Z_$0-9]*$", "").equals("");
    }
    
    public int getFrameDuration() { return frame_dur; }

    public void setFrameDuration(int frame_dur) {
        this.frame_dur = frame_dur;
    }
    
    public void setLocked(boolean l) { locked = l; }
    public boolean isLocked() { return locked; }
    
    public int frameCount() { return frame_count; }
    public void setFrameCount(int c) { frame_count = c < 1 ? 1 : c; }
    public void addFrameCount(int d) { setFrameCount(frame_count + d); }
    
    @Override
    public String toString() {
        return getName();
    }

    public String getSpriteSheet() {
        return spritesheet;
    }

    public void setSpriteSheet(String spritesheet) {
        this.spritesheet = spritesheet;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public boolean equalTo(Animation a) {
        if (!(name.equals(a.name))) return false;
        if (!(spritesheet.equals(a.spritesheet))) return false;
        if (frame_dur != a.frame_dur) return false;
        if (frame_count != a.frame_count) return false;
        return true;
    }
    
    public void copyTo(Animation new_a, boolean copy_name) {
        if (copy_name) new_a.name = name;
        new_a.spritesheet = spritesheet;
        new_a.frame_dur = frame_dur;
        new_a.frame_count = frame_count;
    }
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("a\n");
            bw.write("n="+name+"\n");
            bw.write("s="+spritesheet+"\n");
            bw.write("lk="+locked+"\n");
            bw.write("fd="+frame_dur+"\n");
            bw.write("fc="+frame_count+"\n");
            bw.write("/a\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean load(BufferedReader br) {
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.equals("/a")) return true;
                
                if (line.indexOf("n=") == 0) name = line.substring(2);
                if (line.indexOf("s=") == 0) spritesheet = line.substring(2);
                if (line.indexOf("lk=") == 0) locked = Boolean.parseBoolean(line.substring(3));
                if (line.indexOf("fd=") == 0) frame_dur = Integer.parseInt(line.substring(3));
                if (line.indexOf("fc=") == 0) frame_count = Integer.parseInt(line.substring(3));
                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
}
