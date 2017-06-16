package project;

import project.objects.SceneObject;
import gui.GUI;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import misc.Assets;
import misc.MiscMath;

public class Project {
    
    private static Project project;
    
    private String name; 
    private ArrayList<Level> levels;
    private ArrayList<SceneObject> object_gallery;
    
    private Level current_level, home_level;
    
    private Project(String name, boolean home, boolean gallery_player) {
        this.name = name;
        this.object_gallery = new ArrayList<SceneObject>();
        this.levels = new ArrayList<Level>();
        if (gallery_player) {
            SceneObject player = new SceneObject();
            player.setHitbox(false);
            player.setType("Player");
            player.setName("player");
            player.setGravity(true);
            player.setCollides(true);
            this.object_gallery.add(player);
        }
        if (home) {
            Level l = new Level(); l.setName("level");
            addLevel(l);
            switchToLevel(l.getName());
            setHomeLevel(l);
        }
    }
    
    public void setHomeLevel(Level l) {
        if (containsLevel(l.getName())) home_level = l;
    }

    public Level getHomeLevel() {
        return home_level;
    }
    
    public static Project getProject() { return project; }
    public static boolean projectExists(String name) { 
        if (name == null) return false;
        return new File(Assets.USER_HOME+"/.pengine/projects/"+name).exists(); 
    }
    
    public static void deleteProject(String name, boolean everything) { 
        if (name == null) return;
        Assets.delete(new File(Assets.USER_HOME+"/.pengine/projects/"+name+(!everything ? "/project.txt" : "")));
    }
    
    public static void newProject(String name, boolean home, boolean player) { 
        project = new Project(name, home, player);        
    }
    
    public String getName() { return name; }
    /**
     * Change the name of the project. Changes the directory as well, which means that the project will need to be resaved.
     * @param n 
     */
    public void setName(String n) { 
        name = n;
    }
    public String getDirectory() { return Assets.USER_HOME+"/.pengine/projects/"+name; }
    public boolean existsOnDisk() { return projectExists(getName()); }
    
    public SceneObject getGalleryObject(int i) { return object_gallery.get(i); }
    public SceneObject getGalleryObject(String type) { 
        for (SceneObject g: object_gallery) if (g.getType().equals(type)) return g;
        return null;
    }
    public void addGalleryObject(SceneObject o) { if (!object_gallery.contains(o)) object_gallery.add(o); }
    public void removeGalleryObject(SceneObject o) { if (object_gallery.contains(o)) object_gallery.remove(o); }
    public boolean removeGalleryObject(int index) {
        if (index < 0 || index >= object_gallery.size()) return false;
        return object_gallery.remove(index) != null;
    }
    public boolean containsGalleryObject(SceneObject o) { return object_gallery.contains(o); }
    public boolean containsGalleryObject(String type) { 
        for (SceneObject o: object_gallery) if (o.getType().equals(type)) return true;
        return false;
    }
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
    
    public void switchToLevel(Level l) { switchToLevel(l.getName()); }
    
    public void switchToLevel(String level_id) {
        for (Level l: getLevels()) {
            if (l.getName().equals(level_id)) current_level = l;
        }
    }
    
    public boolean save() {
        mkdirs();
        File f = new File(getDirectory()+"/project.txt");
        FileWriter fw;
        System.out.println("Saving to file " + f.getAbsoluteFile().getAbsolutePath());
        try {
            if (!f.exists()) f.createNewFile();
            fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            
            for (Level l : levels) l.save(bw);
            for (SceneObject o: object_gallery) o.save(bw);
            
            bw.write("curr="+current_level.getName()+"\n");
            bw.write("home="+home_level.getName()+"\n");
            bw.write("camera="+(int)GUI.getSceneCanvas().getCameraX()+" "+(int)GUI.getSceneCanvas().getCameraY()+"\n");
            bw.write("zoom="+(int)GUI.getSceneCanvas().getZoom()+"\n");
            
            bw.close();
            System.out.println("Saved to "+f.getAbsolutePath());
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public void load() {
        File f = new File(getDirectory()+"/project.txt");
        if (!f.exists()) return;
        FileReader fr;
        System.out.println("Loading from file: " + f.getAbsoluteFile().getAbsolutePath());
        try {
            fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                String line = br.readLine();
                
                if (line == null) break;
                line = line.trim();
                
                if (line.equals("l")) {
                    Level l = new Level();
                    if (l.load(br)) addLevel(l);
                }
                if (line.equals("so")) {
                    SceneObject o = new SceneObject();
                    if (o.load(br)) addGalleryObject(o);
                }
                
                if (line.indexOf("curr=") == 0) switchToLevel(line.substring(5));
                if (line.indexOf("home=") == 0) setHomeLevel(getLevel(line.substring(5)));
                if (line.indexOf("zoom=") == 0) GUI.getSceneCanvas()
                        .setZoom(Integer.parseInt(line.substring(5)));
                if (line.indexOf("camera=") == 0) {
                    int[] coords = MiscMath.toIntArray(line.substring(7));
                    GUI.getSceneCanvas().setCamera(coords[0], coords[1]);
                }
                
            }
            br.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean mkdirs() {
        return new File(getDirectory()+"/assets").mkdirs();
    }
    
    public ArrayList<Level> getLevels() {
        return levels;
    }
    
    public Level getLevel(int i) {
        return i > -1 && i < levels.size() ? levels.get(i) : null;
    }
    
    public Level getLevel(String level_id) {
        for (Level l: levels) {
            if (l.getName().equals(level_id)) {
                return l;
            }
        }
        return null;
    }
    
    public void deleteLevel(Level l) {
        deleteLevel(l.getName());
    }
    
    public void deleteLevel(String level_id) {
        Level l = getLevel(level_id);
        if (l != null) levels.remove(l);
    }
    
    public boolean galleryObjectExists(String name) {
        for (SceneObject o: object_gallery) {
            if (o.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Pass in a gallery object to copy it's contents to all objects in the project
     * that have a matching type.
     * @param gallery_object 
     */
    public void applyGalleryChanges(SceneObject gallery_object) {
        if (!Project.getProject().containsGalleryObject(gallery_object)) return;
        int o_count = 0, l_count = 0;
        for (Level l: levels) {
            boolean matching = false;
            for (SceneObject o: l.getObjects(Level.ALL_OBJECTS)) {
                if (gallery_object.getType().equals(o.getType())) {
                    gallery_object.copyTo(o, false, true);
                    o_count++;
                    matching = true;
                }
            }
            if (matching) l_count++;
        }
        System.out.println("Copied object properties to "+o_count+" instances across "+l_count+" levels.");
    }
    
    public ArrayList<SceneObject> getGalleryObjects() { return object_gallery; }
    
    public void autoName() {
        int i = 1;
        while (true) {
            String n = "project"+(i > 1 ? ""+i : "");
            if (!Project.projectExists(n)) { setName(n); break; }
            i++;
        }
    }
    
}
