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
    }
    
    public static Project getProject() { return project; }
    
    public static void newProject(String name) { 
        project = new Project(name); 
        Level l = new Level(); l.setName("home");
        project.addLevel(l);
        project.switchToLevel(l.getName());
    }
    
    public String getName() { return name; }
    public String getDirectory() { return Assets.USER_HOME+"/level_editor/projects/"+name+"/"; }
    public boolean existsOnDisk() { return new File(getDirectory()).exists(); }
    
    public SceneObject getGalleryObject(int i) { return object_gallery.get(i); }
    public void addGalleryObject(SceneObject o) { if (!object_gallery.contains(o)) object_gallery.add(o); }
    public void removeGalleryObject(SceneObject o) { if (object_gallery.contains(o)) object_gallery.remove(o); }
    public boolean removeGalleryObject(int index) {
        if (index < 0 || index >= object_gallery.size()) return false;
        return object_gallery.remove(index) != null;
    }
    public boolean containsGalleryObject(SceneObject o) { return object_gallery.contains(o); }
    public int gallerySize() { return object_gallery.size(); }
    
    public boolean containsLevel(String level_name) {
        for (Level l: levels) {
            if (l.getName().equals(level_name)) return true;
        }
        return false;
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
                GUI.updateWindowTitle();
                GUI.refreshObjectProperties();
                GUI.refreshLevelMenu();
            }
        }
    }
    
    public void save() {
        System.err.println("Project.save() not implemented.");
    }
    
    public void save(BufferedWriter bw, boolean verbose) {
        
    }
    
    public void mkdirs() {
        File project = new File(Assets.USER_HOME+"/level_editor/projects/"+name+"/");
        File assets = new File(Assets.USER_HOME+"/level_editor/projects/"+name+"/assets/");
        File textures = new File(Assets.USER_HOME+"/level_editor/projects/"+name+"/assets/textures");
        File object_textures = new File(Assets.USER_HOME+"/level_editor/projects/"+name+"/assets/textures/objects");
        File animation_sprites = new File(Assets.USER_HOME+"/level_editor/projects/"+name+"/assets/textures/animations");
        File audio_files = new File(Assets.USER_HOME+"/level_editor/projects/"+name+"/assets/audio/");
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
    
    /**
     * Pass in a gallery object to copy it's contents to all objects in the project
     * that have a matching type. TODO: FIX WHAT HAPPENS WHEN YOU CHANGE THE TYPE
     * @param gallery_object 
     */
    public void applyGalleryChanges(SceneObject gallery_object) {
        if (!Project.getProject().containsGalleryObject(gallery_object)) return;
        for (Level l: levels) {
            for (SceneObject o: l.getObjects(Level.ALL_OBJECTS)) {
                if (gallery_object.getType().equals(o.getType())) gallery_object.copyTo(o);
            }
        }
    }
    
    public ArrayList<SceneObject> getGalleryObjects() { return object_gallery; }
    
}
