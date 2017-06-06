package project.objects.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import misc.MiscMath;
import project.Project;
import project.objects.SceneObject;

public class Animation {
    
    private String name = "", spritesheet = "";
    private boolean locked = false;
    private int frame_dur = 100, frame_count = 1; //dur in milliseconds
    
    public int getFrameDuration() { return frame_dur; }

    public void setFrameDuration(int frame_dur) {
        this.frame_dur = frame_dur;
    }
    
    public int frameCount() { return frame_count; }
    public void setFrameCount(int c) { frame_count = c < 0 ? 0 : c; }
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
    
    public void copyTo(Animation new_a) {
        new_a.name = name;
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
            Logger.getLogger(Animation.class.getName()).log(Level.SEVERE, null, ex);
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
