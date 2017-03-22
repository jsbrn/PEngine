package project;

import project.objects.SceneObject;
import gui.GUI;
import project.objects.components.Animation;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import misc.Assets;

public class Project {
    
    private static Project project;
    
    private String name; 
    private ArrayList<Level> levels;
    private ArrayList<SceneObject> object_gallery;
    
    private Level current_level;
    
    public Project(String name) {
        this.name = name;
        this.object_gallery = new ArrayList<SceneObject>();
        this.levels = new ArrayList<Level>();
        SceneObject player = new SceneObject();
        player.setHitbox(false);
        player.setType("Player");
        player.setName("player");
        player.setGravity(true);
        player.setCollides(true);
        this.object_gallery.add(player);
        Level home = new Level();
        home.setName("home");
        this.addLevel(home);
    }
    
    public static Project getProject() { return project; }
    
    public static void newProject(String name) { project = new Project(name); }
    
    public static boolean existsOnDisk(String project_name) {
        return new File(Assets.USER_HOME+"/level_editor/projects/"+project_name+"/").exists();
    }
    
    public void addGalleryObject(SceneObject o) {
        if (!object_gallery.contains(o)) object_gallery.add(o);
    }
    
    public void removeGalleryObject(SceneObject o) {
        if (object_gallery.contains(o)) object_gallery.remove(o);
    }
    
    public ArrayList<SceneObject> getObjectsByType(String type) {
        ArrayList<SceneObject> list = new ArrayList<SceneObject>();
        if (type == null) return list;
        if (type.isEmpty()) return list;
        for (Level l: levels) {
            for (SceneObject o: l.getObjects(Level.ALL_OBJECTS)) {
                if (type.equals(o.getType())) {
                    list.add(o);
                }
            }
        }
        return list;
    }
    
    public void addLevel(Level l) {
        levels.add(l);
    }
    
    public Level getCurrentLevel() { return current_level; }
    
    public void switchToLevel(String level_id) {
        for (Level l: Project.getProject().getLevels()) {
            if (l.getName().equals(level_id)) {
                current_level = l;
                selected_object = null;
                GUI.updateWindowTitle();
                GUI.refreshObjectProperties();
                GUI.refreshLevelEditor();
            }
        }
    }
    
    /*public SceneObject getObject(int onscreen_x, int onscreen_y) {
        ArrayList<SceneObject> all_objects = new ArrayList<SceneObject>();
        all_objects.addAll(current_level.distant_objects);
        all_objects.addAll(current_level.bg_objects);
        all_objects.addAll(current_level.mid_objects);
        all_objects.addAll(current_level.fg_objects);
        for (int i = all_objects.size()-1; i != -1; i--) {
            SceneObject o = all_objects.get(i);
            int[] on_screen = o.getOnscreenCoordinates();
            if (MiscMath.pointIntersects(onscreen_x, onscreen_y, 
                    on_screen[0], on_screen[1], o.getOnscreenWidth(), o.getOnscreenHeight())) {
                return o;
            }
        }
        return null;
    }*/
    
    public void save(BufferedWriter bw, boolean verbose) {
        
    }
    
    private void mkdirs(String project_name) {
        File project = new File(Assets.USER_HOME+"/level_editor/projects/"+project_name+"/");
        File assets = new File(Assets.USER_HOME+"/level_editor/projects/"+project_name+"/assets/");
        File textures = new File(Assets.USER_HOME+"/level_editor/projects/"+project_name+"/assets/textures");
        File object_textures = new File(Assets.USER_HOME+"/level_editor/projects/"+project_name+"/assets/textures/objects");
        File animation_sprites = new File(Assets.USER_HOME+"/level_editor/projects/"+project_name+"/assets/textures/animations");
        File audio_files = new File(Assets.USER_HOME+"/level_editor/projects/"+project_name+"/assets/audio/");
        if (!project.exists()) project.mkdir();
        if (!assets.exists()) assets.mkdir();
        if (!textures.exists()) textures.mkdir();
        if (!object_textures.exists()) object_textures.mkdir();
        if (!animation_sprites.exists()) animation_sprites.mkdir();
        if (!audio_files.exists()) animation_sprites.mkdir();
    }
    
    public ArrayList<Level> getLevels() {
        return levels;
    }
    
    public void deleteLevel(String level_id) {
        for (Level l: levels) {
            if (l.getName().equals(level_id)) {
                levels.remove(l);
            }
        }
    }
    
    public boolean galleryObjectExists(String name) {
        for (SceneObject o: object_gallery) {
            if (o.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public static void deleteFromDisk(File dir) {
        File[] list = dir.listFiles();
        for (File f: list) {
            if (f.isDirectory()) deleteFromDisk(f); else f.delete();
        }
        dir.delete();
    }
    
}
