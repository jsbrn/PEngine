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
import project.objects.components.Block;
import project.objects.components.Dialogue;
import project.objects.components.Flow;

public class Project {
    
    private static Project project;
    
    private String name; 
    private ArrayList<Level> levels;
    private Level current_level;
    private ArrayList<SceneObject> object_gallery;
    
    public Project(String name) {
        this.name = name;
        this.object_gallery = new ArrayList<SceneObject>();
        this.levels = new ArrayList<Level>();
        SceneObject player = new SceneObject();
        player.setHitbox(false);
        player.setType("Player");
        player.setName("player");
        player.gravity(true);
        player.collides(true);
        this.object_gallery.add(player);
        Level home = new Level();
        home.name = "home";
        levels.add(home);
    }
    
    public static Project getProject() { return project; }
    public static void newProject(String name) { project = new Project(name); }
    public static boolean doesProjectExist(String project_name) {
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
            for (SceneObject o: l.all_objects) {
                if (type.equals(o.type)) {
                    list.add(o);
                }
            }
        }
        return list;
    }
    
    public void addLevel(Level l) {
        levels.add(l);
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
    
    private void createProjectDirs(String project_name) {
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
    
    public void deleteLevel(String level_id) {
        for (Level l: levels) {
            if (l.name.equals(level_id)) {
                levels.remove(l);
            }
        }
    }
    
    public void switchToLevel(String level_id) {
        for (Level l: levels) {
            if (l.name.equals(level_id)) {
                current_level = l;
                selected_object = null;
                GUI.updateWindowTitle();
                GUI.refreshObjectProperties();
                GUI.refreshLevelEditor();
            }
        }
    }
    
    public void switchToLevel(int index) {
        current_level = levels.get(index);
        GUI.updateWindowTitle();
        GUI.refreshObjectProperties();
        GUI.refreshLevelEditor();
    }
    
    public void loadProject(String project_name) {
        levels.clear();
        object_gallery.clear();
        current_level = null;
        selected_object = null;
        Properties prop = new Properties();
        
        try {
            FileInputStream f = new FileInputStream(Project.USER_HOME+"/level_editor/projects/"+project_name+"/project.txt");
            prop.load(f);
        } catch(Exception e) {}
        
        int lcount = Integer.parseInt(prop.getProperty("LvlCount"));
        for (int i = 0; i != lcount; i++) {
            loadLevel(i, prop);
        }
        int gcount = Integer.parseInt(prop.getProperty("GallObjCount"));
        for (int i = 0; i != gcount; i++) {
            SceneObject o = loadObject(-1, i, prop, true);
            if (o != null) addGalleryObject(o);
        }
        PROJECT_NAME = project_name;
        switchToLevel(0);
        loadAssets();
        GUI.updateWindowTitle();
        GUI.refreshLevelEditor();
        GUI.refreshObjectProperties();
        GUI.refreshGalleryListings();

    }
    
    /**
     * Saves the object to the properties file. Called on project save.
     * @param o The SceneObject to save.
     * @param lindex The index of the level that the object is in.
     * @param oindex The index of the object in the list it is in (can be the level's list or the gallery).
     * @param prop The properties file to write to.
     * @param gallery Is this a gallery object? If false, then it will be treated as if it is in a level.
     */
    static void saveObject(SceneObject o, int lindex, int oindex, Properties prop, boolean gallery) {
        String prefix = "Lvl"+lindex;
        if (gallery) prefix = "Gall";
        prop.setProperty(prefix+"Obj"+oindex+"Pos", (int)(o.getWorldCoordinates()[0]+(int)(o.getDimensions()[0]/2))+" "+(int)((o.getWorldCoordinates()[1]+(int)(o.getDimensions()[1]/2))));
        prop.setProperty(prefix+"Obj"+oindex+"Dim", o.getDimensions()[0]+" "+o.getDimensions()[1]);
        prop.setProperty(prefix+"Obj"+oindex+"Name", o.NAME);
        prop.setProperty(prefix+"Obj"+oindex+"Layer", o.LAYER+"");
        prop.setProperty(prefix+"Obj"+oindex+"Grav", o.GRAVITY+"");
        prop.setProperty(prefix+"Obj"+oindex+"Coll", o.COLLIDES+"");
        prop.setProperty(prefix+"Obj"+oindex+"Img", o.texture+".png");
        prop.setProperty(prefix+"Obj"+oindex+"Type", o.CLASS);
        prop.setProperty(prefix+"Obj"+oindex+"IsHitbox", o.isHitbox()+"");

        prop.setProperty(prefix+"Obj"+oindex+"AnimCount", o.animations.size()+"");
        for (int ii = 0; ii != o.animations.size(); ii++) {
            prop.setProperty(prefix+"Obj"+oindex+"Anim"+ii+"Name", o.animations.get(ii).NAME+"");
            prop.setProperty(prefix+"Obj"+oindex+"Anim"+ii+"SpriteName", o.animations.get(ii).SPRITE_NAME+"");
            prop.setProperty(prefix+"Obj"+oindex+"Anim"+ii+"Widths", Project.integersToString(o.animations.get(ii).widths));
            prop.setProperty(prefix+"Obj"+oindex+"Anim"+ii+"Heights", Project.integersToString(o.animations.get(ii).heights));
            prop.setProperty(prefix+"Obj"+oindex+"Anim"+ii+"FrmDur", o.animations.get(ii).FRAME_DURATION+"");
            prop.setProperty(prefix+"Obj"+oindex+"Anim"+ii+"Loops", o.animations.get(ii).loop+"");
        }
        prop.setProperty(prefix+"Obj"+oindex+"DlgCount", o.dialogues.size()+"");
        for (int ii = 0; ii != o.dialogues.size(); ii++) {
            prop.setProperty(prefix+"Obj"+oindex+"Dlg"+ii+"Name", o.dialogues.get(ii).NAME+"");
        }
        prop.setProperty(prefix+"Obj"+oindex+"FlowCount", o.flows.size()+"");
        for (int ii = 0; ii != o.flows.size(); ii++) {
            prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"Name", o.flows.get(ii).NAME+"");
            prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"Run", o.flows.get(ii).RUN_ON_SPAWN+"");
            prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"BlockCount", o.flows.get(ii).blockCount()+"");
            for (int iii = 0; iii != o.flows.get(ii).blockCount(); iii++) {
                Block b = o.flows.get(ii).getBlock(iii);
                prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"Title", b.title()+"");
                prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"Cat", b.getCategory());
                prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"Coords", b.getCoords()[0]+" "+b.getCoords()[1]);
                prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"ID", b.getID()+"");
                prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"ParamCount", b.paramCount()+"");
                //param conns
                for (int iv = 0; iv != b.paramCount(); iv++) {
                    prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"P"+iv+"Value", b.getParametre(iv, 0));
                    prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"P"+iv+"Conn", b.getParametreConnection(iv)+"");
                }
                //dot conns
                for (int iv = 0; iv != 5; iv++) {
                    prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"D"+iv+"Conn", b.getConnection(iv)+"");
                }
            }
        }
    }
    
    static SceneObject loadObject(int lindex, int oindex, Properties prop, boolean gallery) {
        String prefix = "Lvl"+lindex;
        if (gallery) prefix = "Gall";
        SceneObject o = new SceneObject();
        String pos = prop.getProperty(prefix+"Obj"+oindex+"Pos");
        String dim = prop.getProperty(prefix+"Obj"+oindex+"Dim");
        ArrayList<Integer> position = parseIntegers(pos, true), dimensions = parseIntegers(dim, true);
        o.setWidth(dimensions.get(0));
        o.setHeight(dimensions.get(1));
        o.setWorldX(position.get(0)-(dimensions.get(0)/2));
        o.setWorldY(position.get(1)-(dimensions.get(1)/2));
        o.NAME = prop.getProperty(prefix+"Obj"+oindex+"Name");
        /*if (prop.getProperty(prefix+"Obj"+oindex+"Img") != null)*/ o.texture = prop.getProperty(prefix+"Obj"+oindex+"Img").replace(".png", "");
        /*if (prop.getProperty(prefix+"Obj"+oindex+"Layer") != null)*/ o.LAYER = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"Layer"));
        o.GRAVITY = Boolean.parseBoolean(prop.getProperty(prefix+"Obj"+oindex+"Grav"));
        o.COLLIDES = Boolean.parseBoolean(prop.getProperty(prefix+"Obj"+oindex+"Coll"));
        o.CLASS = prop.getProperty(prefix+"Obj"+oindex+"Type");
        o.setHitbox(Boolean.parseBoolean(prop.getProperty(prefix+"Obj"+oindex+"IsHitbox")));

        int anim_count = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"AnimCount"));
        for (int ii = 0; ii != anim_count; ii++) {
            Animation s = new Animation();
            o.animations.add(s);
            s.NAME = prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Name");
            s.SPRITE_NAME = prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"SpriteName");
            s.widths = parseIntegers(prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Widths"), true);
            s.heights = parseIntegers(prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Heights"), true);
            s.loop = Boolean.parseBoolean(prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Loops"));
            s.FRAME_DURATION = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"FrmDur"));
        }
        int dialogue_count = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"DlgCount"));
        for (int ii = 0; ii != dialogue_count; ii++) {
            Dialogue d = new Dialogue();
            o.dialogues.add(d);
            d.NAME = prop.getProperty(prefix+"Obj"+oindex+"Dlg"+ii+"Name");
        }
        int flow_count = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"FlowCount"));
        for (int ii = 0; ii != flow_count; ii++) {
            Flow d = new Flow();
            o.flows.add(d);
            d.NAME = prop.getProperty(prefix+"Obj"+oindex+"Flow"+ii+"Name");
            d.RUN_ON_SPAWN = Boolean.parseBoolean(prop.getProperty(prefix+"Obj"+oindex+"Flow"+ii+"Run"));
            int block_count = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"Flow"+ii+"BlockCount"));
            for (int iii = 0; iii != block_count; iii++) {
                String tit = prop.getProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"Title");
                String cat = prop.getProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"Cat");
                Block from_list = BlockList.getBlock(tit, cat);
                if (from_list == null) continue;
                Block b = new Block();
                from_list.copyTo(b);
                ArrayList<Integer> coords = Project.parseIntegers(prop.getProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"Coords"), true);
                b.setX(coords.get(0));
                b.setY(coords.get(1));
                b.setID(Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"ID")));
                //set conns
                int p_count = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"ParamCount"));
                for (int iv = 0; iv != p_count; iv++) {
                    String val = prop.getProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"P"+iv+"Value");
                    int conn = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"P"+iv+"Conn"));
                    b.setParametreConnection(iv, conn);
                    b.setParametre(iv, 0, val);
                }
                for (int iv = 0; iv != 5; iv++) {
                    int conn = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"D"+iv+"Conn"));
                    b.setConnection(iv, conn);
                }
                d.addBlock(b);
            }
        }
        return o;
    }
    
    /**
     * Loads the level from the save file (is strictly called from loadProject, 
     * as it assumes the Properties instance passed has already called load() on a file).
     * @param index
     * @param prop 
     */
    static void loadLevel(int index, Properties prop) {
        Level level = new Level();
        String top_rgb = prop.getProperty("Lvl"+index+"TopBGColor");
        String bottom_rgb = prop.getProperty("Lvl"+index+"BottomBGColor");
        String amb_rbg = prop.getProperty("Lvl"+index+"AmbientColor");
        String spawn = prop.getProperty("Lvl"+index+"Spawn");
        String campos = prop.getProperty("Lvl"+index+"CameraPosition");
        ArrayList<Integer> t_rgb = parseIntegers(top_rgb, true), b_rgb = parseIntegers(bottom_rgb, true), 
                a_rgb = parseIntegers(amb_rbg, true), spwn = parseIntegers(spawn, true), cam_pos = parseIntegers(campos, true);
        level.R1 = t_rgb.get(0);level.G1 = t_rgb.get(1);level.B1 = t_rgb.get(2);
        level.R2 = b_rgb.get(0);level.G2 = b_rgb.get(1);level.B2 = b_rgb.get(2);
        level.R3 = a_rgb.get(0);level.G3 = a_rgb.get(1);level.B3 = a_rgb.get(2);
        level.lighting_intensity = Integer.parseInt(prop.getProperty("Lvl"+index+"AmbientIntensity"));
        level.ambient_sound_volume = Float.parseFloat(prop.getProperty("Lvl"+index+"AmbientSoundVolume"));
        level.bg_music_vol = Float.parseFloat(prop.getProperty("Lvl"+index+"BGMusicVolume"));
        level.player_spawn = new int[]{spwn.get(0), spwn.get(1)};
        level.camera_spawn = new int[]{cam_pos.get(0), cam_pos.get(1)};
        level.ambient_sound = prop.getProperty("Lvl"+index+"AmbientSound");
        level.name = prop.getProperty("Lvl"+index+"ID");
        level.bg_music = prop.getProperty("Lvl"+index+"Music");
        level.zoom = Integer.parseInt(prop.getProperty("Lvl"+index+"Zoom"));
        level.width = Integer.parseInt(prop.getProperty("Lvl"+index+"Width"));
        level.height = Integer.parseInt(prop.getProperty("Lvl"+index+"Height"));
        level.loop_bg_music = Boolean.parseBoolean(prop.getProperty("Lvl"+index+"LoopBGMusic"));
        level.loop_ambient_sound = Boolean.parseBoolean(prop.getProperty("Lvl"+index+"LoopAmbientSound"));
        level.auto_bg_music = Boolean.parseBoolean(prop.getProperty("Lvl"+index+"AutoBGMusic"));
        level.auto_ambient_sound = Boolean.parseBoolean(prop.getProperty("Lvl"+index+"AutoAmbientSound"));

        int obj_count = Integer.parseInt(prop.getProperty("Lvl"+index+"ObjectCount"));
        for (int i = 0; i != obj_count; i++) {
            SceneObject o = loadObject(index, i, prop, false);
            if (o != null) level.add(o);
        }
        
        addLevel(level);

    }
    
    public boolean galleryObjectExists(String name) {
        for (SceneObject o: object_gallery) {
            if (o.NAME.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public void mkdirs() {
        new File(Assets.USER_HOME+"/level_editor").mkdir();
        new File(Assets.USER_HOME+"/level_editor/jars").mkdir();
        new File(Assets.USER_HOME+"/level_editor/projects").mkdir();
    }
    
    public static void deleteDirectory(File dir) {
        File[] list = dir.listFiles();
        for (File f: list) {
            if (f.isDirectory()) deleteDirectory(f); else f.delete();
        }
        dir.delete();
    }
    
}
