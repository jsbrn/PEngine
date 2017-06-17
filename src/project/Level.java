package project;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import misc.MiscMath;
import project.objects.SceneObject;

public class Level {
    
    public static final int ALL_OBJECTS = 0, DISTANT_OBJECTS = 1, BACKGROUND_OBJECTS = 2, 
            MIDDLE_OBJECTS = 3, FOREGROUND_OBJECTS = 4;
    private final ArrayList<SceneObject> layers[];
    
    private String name = "", bg_ambience = "", bg_music = "";
    private Color bg_color_top, bg_color_bottom, lighting_color;
    private int zoom = 4; 
    private int[] bounds;
    private double lighting_intensity = 0;
    private boolean loop_bg_music = true, loop_bg_ambience = true, auto_bg_music = true,
            auto_bg_ambience = true;
    private float bg_music_vol = 1, bg_ambience_vol = 1;
    
    private int[] player_spawn = {0, 0}, camera_spawn = {0, 0};
    
    public Level() {
        this.layers = new ArrayList[5];
        for (int i = 0; i < layers.length; i++) {
            this.layers[i] = new ArrayList<SceneObject>();
        }
        this.bounds = new int[]{-128, -64, 256, 128};
        this.bg_color_top = new Color(0, 0, 0);
        this.bg_color_bottom = new Color(0, 0, 0);
        this.lighting_color = new Color(0, 0, 0);
    }
    
    public void autoName() {
        int i = 1;
        while (true) {
            String n = "level"+(i > 1 ? ""+i : "");
            if (!Project.getProject().containsLevel(n)) { setName(n); break; }
            i++;
        }
    }
    
    public static boolean isValidName(String name) {
        if (name == null) return false;
        if (name.trim().length() == 0) return false;
        if (name.trim().equals("true") || name.trim().equals("false")
                || name.trim().toLowerCase().equals("player")) return false;
        return name.replaceAll("^[a-zA-Z_$][a-zA-Z_$0-9]*$", "").equals("");
    }
    
    public boolean loopBGMusic() { return loop_bg_music; }
    public boolean loopBGAmbience() { return loop_bg_ambience; }
    public boolean autoPlayBGMusic() { return auto_bg_music; }
    public boolean autoPlayBGAmbience() { return auto_bg_ambience; }
    
    public void loopBGMusic(boolean b) { loop_bg_music = b; }
    public void loopBGAmbience(boolean b) { loop_bg_ambience = b; }
    public void autoPlayBGAmbience(boolean b) { auto_bg_ambience = b; }
    public void autoPlayBGMusic(boolean b) { auto_bg_music = b; }
    
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
    
    public void setAmbientSound(String filename) { bg_ambience = filename; }
    public void setBGMusic(String filename) { bg_music = filename; }
    public String getAmbientSound() { return bg_ambience; }
    public String getBGMusic() { return bg_music; }
    
    public void setBGMusicVolume(float v) { bg_music_vol = v; }
    public void setBGAmbienceVolume(float v) { bg_ambience_vol = v; }

    public float getBGAmbienceVolume() { return bg_ambience_vol; }

    public float getBGMusicVolume() { return bg_music_vol; }
    
    
    public int[] bounds() { return bounds; }
    public int getZoom() { return zoom; }
    public void setZoom(int z) { 
        zoom = zoom + z > 8 ? 8 : (zoom + z < 1 ? 1 : zoom);
    }
    
    public ArrayList<SceneObject> getObjects(int layer) { return layers[layer]; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public void add(SceneObject o) {
        if (getObjects(ALL_OBJECTS).contains(o) == false) {
            moveToLayer(o.getLayer(), o);
            getObjects(ALL_OBJECTS).add(o);
        }
    }
    
    public SceneObject getObject(int onscreen_x, int onscreen_y) {
        return getObject(onscreen_x, onscreen_y, 0); //skip none
    }
    
    public SceneObject getObject(int onscreen_x, int onscreen_y, int skip) {
        for (int l = FOREGROUND_OBJECTS; l >= DISTANT_OBJECTS; l--) {
            for (int i = layers[l].size()-1; i != -1; i--) {
                SceneObject o = layers[l].get(i);
                int[] on_screen = o.getOnscreenCoords();
                if (MiscMath.pointIntersects(onscreen_x, onscreen_y, 
                        on_screen[0], on_screen[1], o.getOnscreenWidth(), o.getOnscreenHeight())) {
                    if (skip == 0) return o; else skip--;
                }
            }
        }
        return null;
    }
    
    public SceneObject getObject(String name) {
        if (name == null) return null;
        for (SceneObject o: layers[ALL_OBJECTS]) if (name.equals(o.getName())) return o;
        return null;
    }
    
    public void moveForward(SceneObject o) {
        for (int i = DISTANT_OBJECTS; i != layers().length; i++) {
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
        for (int i = 0; i < layers().length; i++) {
            layers()[i].remove(o);
        }
    }
    
    public void moveBackward(SceneObject o) {
        for (int i = DISTANT_OBJECTS; i < layers().length; i++) {
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
        for (int i = DISTANT_OBJECTS; i < layers().length; i++) {
            if (i != o.getLayer()) {
                layers()[i].remove(o);
            } else {
                if (layers()[i].contains(o) == false) {
                    layers()[i].add(o);
                }
            }
        }
        o.setLayer(layer);
    }
    
    public void setBounds(int x, int y, int w, int h) {
        bounds = new int[]{x, y, w, h};
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
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("l"+"\n");
                bw.write("n="+name+"\n");
                bw.write("b="+bounds[0]+" "+bounds[1]+" "+bounds[2]+" "+bounds[3]+"\n");
                bw.write("tc="+bg_color_top.getRed()+" "+bg_color_top.getGreen()+" "+bg_color_top.getBlue()+"\n");
                bw.write("bc="+bg_color_bottom.getRed()+" "+bg_color_bottom.getGreen()+" "+bg_color_bottom.getBlue()+"\n");
                bw.write("lc="+lighting_color.getRed()+" "+lighting_color.getGreen()+" "+lighting_color.getBlue()+"\n");
                bw.write("li="+lighting_intensity+"\n");
                bw.write("z="+zoom+"\n");
                bw.write("ps="+player_spawn[0]+" "+player_spawn[1]+"\n");
                bw.write("cs="+camera_spawn[0]+" "+camera_spawn[1]+"\n");
                bw.write("apm="+auto_bg_music+"\n");
                bw.write("apa="+auto_bg_ambience+"\n");
                bw.write("lm="+loop_bg_music+"\n");
                bw.write("la="+loop_bg_ambience+"\n");
                bw.write("mv="+bg_music_vol+"\n");
                bw.write("av="+bg_ambience_vol+"\n");
                bw.write("bgm="+bg_music+"\n");
                bw.write("as="+bg_ambience+"\n");
                
                for (SceneObject o: getObjects(ALL_OBJECTS)) o.save(bw);
                
            bw.write("/l"+"\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public final boolean load(BufferedReader br) {
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.equals("/l")) return true;
                
                if (line.indexOf("n=") == 0) name = line.substring(2);
                if (line.indexOf("b=") == 0) bounds = MiscMath.toIntArray(line.substring(2));
                if (line.indexOf("tc=") == 0) {
                    int[] rgb = MiscMath.toIntArray(line.substring(3).split(" "));
                    bg_color_top = new Color(rgb[0], rgb[1], rgb[2]);
                }
                if (line.indexOf("bc=") == 0) {
                    int[] rgb = MiscMath.toIntArray(line.substring(3).split(" "));
                    bg_color_bottom = new Color(rgb[0], rgb[1], rgb[2]);
                }
                if (line.indexOf("lc=") == 0) {
                    int[] rgb = MiscMath.toIntArray(line.substring(3).split(" "));
                    lighting_color = new Color(rgb[0], rgb[1], rgb[2]);
                }
                if (line.indexOf("li=") == 0) lighting_intensity = Float.parseFloat(line.substring(3));
                if (line.indexOf("z=") == 0) zoom = Integer.parseInt(line.substring(2));
                if (line.indexOf("ps=") == 0) player_spawn = MiscMath.toIntArray(line.substring(3));
                if (line.indexOf("cs=") == 0) camera_spawn = MiscMath.toIntArray(line.substring(3));
                if (line.indexOf("apm=") == 0) auto_bg_music = Boolean.parseBoolean(line.substring(4));
                if (line.indexOf("apa=") == 0) auto_bg_ambience = Boolean.parseBoolean(line.substring(4));
                if (line.indexOf("lm=") == 0) loop_bg_music = Boolean.parseBoolean(line.substring(3));
                if (line.indexOf("la=") == 0) loop_bg_ambience = Boolean.parseBoolean(line.substring(3));
                if (line.indexOf("mv=") == 0) bg_music_vol = Float.parseFloat(line.substring(3));
                if (line.indexOf("av=") == 0) bg_ambience_vol = Float.parseFloat(line.substring(3));
                if (line.indexOf("bgm=") == 0) bg_music = line.substring(4);
                if (line.indexOf("as=") == 0) bg_ambience = line.substring(3);
                
                if (line.equals("so")) {
                    SceneObject o = new SceneObject();
                    if (o.load(br)) add(o);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    @Override
    public String toString() {
        return getName()+(equals(Project.getProject().getHomeLevel()) ? " [Home]" : "");
    }
    
}