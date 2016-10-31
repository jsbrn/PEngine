package scene;

import gui.GUI;
import misc.MiscMath;
import misc.Script;
import misc.Animation;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import misc.Block;
import misc.BlockList;
import misc.Dialogue;
import misc.Flow;
import misc.Level;
import threads.PreviewThread;

public class Scene {
    
    public static double ORIGIN_X = 10, ORIGIN_Y = 10, CANVAS_WIDTH = 0, CANVAS_HEIGHT = 0, 
            LAST_MOUSE_X = 0, LAST_MOUSE_Y = 0;
    
    public static ArrayList<Level> LEVELS = new ArrayList<Level>();
    public static Level CURRENT_LEVEL;
    
    public static PreviewThread PREVIEW_THREAD;
    
    public static SceneObject SELECTED_OBJECT, ACTIVE_EDIT_OBJECT;
    
    public static String PROJECT_NAME = "", USER_HOME;
    public static ArrayList<String> OBJECT_TEXTURE_NAMES = new ArrayList<String>(),
            ANIMATION_TEXTURE_NAMES = new ArrayList<String>();
    public static ArrayList<BufferedImage> OBJECT_TEXTURES = new ArrayList<BufferedImage>(),
            ANIMATION_TEXTURES = new ArrayList<BufferedImage>();
    
    public static ArrayList<SceneObject> OBJECT_GALLERY = new ArrayList<SceneObject>();
    
    public static final int SELECT_TOOL = 0, CAMERA_TOOL = 1, MOVE_TOOL = 2, RESIZE_TOOL = 3;
    public static int SELECTED_TOOL = 1, ZOOM = 8;
    
    public static void moveCamera(double x, double y) {
        ORIGIN_X += x; ORIGIN_Y += y;
    }
    
    public static void moveToLayer(int layer, SceneObject o) {
        CURRENT_LEVEL.moveToLayer(layer, o);
    }
    
    public static void moveForward(SceneObject o) {
        CURRENT_LEVEL.moveForward(o);
    }
    
    public static void moveBackward(SceneObject o) {
        CURRENT_LEVEL.moveBackward(o);
    }
    
    public static void removeObject(SceneObject o) {
        CURRENT_LEVEL.removeObject(o);
    }
    
    public static void addGalleryObject(SceneObject o) {
        if (!OBJECT_GALLERY.contains(o)) OBJECT_GALLERY.add(o);
    }
    
    public static void removeGalleryObject(SceneObject o) {
        if (OBJECT_GALLERY.contains(o)) OBJECT_GALLERY.remove(o);
    }
    
    public static void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, (int)CANVAS_WIDTH, (int)CANVAS_HEIGHT);
        if (CURRENT_LEVEL == null) return;
        double r1 = CURRENT_LEVEL.R1, g1 = CURRENT_LEVEL.G1, b1 = CURRENT_LEVEL.B1;
        double r2 = CURRENT_LEVEL.R2, g2 = CURRENT_LEVEL.G2, b2 = CURRENT_LEVEL.B2;
        int height = 10;
        int increments = (int)(CANVAS_HEIGHT/height);
        double g_add = (g2-g1)/increments;
        double r_add = (r2-r1)/increments;
        double b_add = (b2-b1)/increments;
        for (int y = 0; y < 1+increments; y+=1) {
            r1+=r_add;g1+=g_add;b1+=b_add;
            if (r1 <= 255 && g1 <= 255 && b1 <= 255 && r1 >= 0 && g1 >= 0 && b1 >= 0) {
                g.setColor(new Color((int)r1, (int)g1, (int)b1));
            }
            g.fillRect(0, y*height, (int)CANVAS_WIDTH, height);
        }
        
        for (SceneObject o: CURRENT_LEVEL.DISTANT_OBJECTS) {
            if (MiscMath.rectanglesIntersect(o.getOnscreenCoordinates()[0], o.getOnscreenCoordinates()[1], 
                    o.getOnscreenWidth(), o.getOnscreenHeight(), 
                    0, 0, (int)CANVAS_WIDTH, (int)CANVAS_HEIGHT) || o.getOnscreenHeight() > CANVAS_HEIGHT || o.getOnscreenWidth() > CANVAS_WIDTH) {
                o.draw(g);
            }
        }
        for (SceneObject o: CURRENT_LEVEL.BACKGROUND_OBJECTS) {
            if (MiscMath.rectanglesIntersect(o.getOnscreenCoordinates()[0], o.getOnscreenCoordinates()[1], 
                    o.getOnscreenWidth(), o.getOnscreenHeight(), 
                    0, 0, (int)CANVAS_WIDTH, (int)CANVAS_HEIGHT) || o.getOnscreenHeight() > CANVAS_HEIGHT || o.getOnscreenWidth() > CANVAS_WIDTH) {
                o.draw(g);
            }
        }
        for (SceneObject o: CURRENT_LEVEL.NORMAL_OBJECTS) {
            if (MiscMath.rectanglesIntersect(o.getOnscreenCoordinates()[0], o.getOnscreenCoordinates()[1], 
                    o.getOnscreenWidth(), o.getOnscreenHeight(), 
                    0, 0, (int)CANVAS_WIDTH, (int)CANVAS_HEIGHT) || o.getOnscreenHeight() > CANVAS_HEIGHT || o.getOnscreenWidth() > CANVAS_WIDTH) {
                o.draw(g);
            }
        }
        for (SceneObject o: CURRENT_LEVEL.FOREGROUND_OBJECTS) {
            if (MiscMath.rectanglesIntersect(o.getOnscreenCoordinates()[0], o.getOnscreenCoordinates()[1], 
                    o.getOnscreenWidth(), o.getOnscreenHeight(), 
                    0, 0, (int)CANVAS_WIDTH, (int)CANVAS_HEIGHT) || o.getOnscreenHeight() > CANVAS_HEIGHT || o.getOnscreenWidth() > CANVAS_WIDTH) {
                o.draw(g);
            }
        }
        g.setColor(new Color(CURRENT_LEVEL.R3, CURRENT_LEVEL.G3, CURRENT_LEVEL.B3, CURRENT_LEVEL.AMBIENT_INTENSITY));
        g.fillRect(0, 0, (int)CANVAS_WIDTH, (int)CANVAS_HEIGHT);
        
        g.setColor(Color.green);
        g.drawRect((int)ORIGIN_X, (int)ORIGIN_Y, (int)CURRENT_LEVEL.WIDTH*ZOOM, (int)CURRENT_LEVEL.HEIGHT*ZOOM);
        g.setColor(Color.red);
        g.drawLine((int)Scene.ORIGIN_X, 0, (int)Scene.ORIGIN_X, 100000);
        g.setColor(Color.yellow);
        g.drawLine(0, (int)Scene.ORIGIN_Y, 100000, (int)Scene.ORIGIN_Y);
        g.setColor(Color.cyan);
        g.drawLine((int)(CURRENT_LEVEL.SPAWN_COORD[0]*ZOOM)+(int)ORIGIN_X-3, (int)(CURRENT_LEVEL.SPAWN_COORD[1]*ZOOM)+(int)ORIGIN_Y-3, 
                (int)(CURRENT_LEVEL.SPAWN_COORD[0]*ZOOM)+(int)ORIGIN_X+3, (int)(CURRENT_LEVEL.SPAWN_COORD[1]*ZOOM)+(int)ORIGIN_Y+3);
        g.drawLine((int)(CURRENT_LEVEL.SPAWN_COORD[0]*ZOOM)+(int)ORIGIN_X+3, (int)(CURRENT_LEVEL.SPAWN_COORD[1]*ZOOM)+(int)ORIGIN_Y-3, 
                (int)(CURRENT_LEVEL.SPAWN_COORD[0]*ZOOM)+(int)ORIGIN_X-3, (int)(CURRENT_LEVEL.SPAWN_COORD[1]*ZOOM)+(int)ORIGIN_Y+3);
        //cam coords
        g.fillRect((int)(CURRENT_LEVEL.CAM_COORD[0]*ZOOM)+(int)ORIGIN_X-3, (int)(CURRENT_LEVEL.CAM_COORD[1]*ZOOM)+(int)ORIGIN_Y-3, 
                6, 6);
        
        g.setColor(Color.white);
        drawString("Mouse: "+(int)((LAST_MOUSE_X-ORIGIN_X)/Scene.ZOOM)+", "+(int)((LAST_MOUSE_Y-ORIGIN_Y)/Scene.ZOOM), 8, (int)CANVAS_HEIGHT-10, g);
        if (SELECTED_TOOL == MOVE_TOOL) {
            drawString("Arrow keys: precision movement", 8, (int)CANVAS_HEIGHT-30, g);
        }
        if (SELECTED_TOOL == RESIZE_TOOL) {
            drawString("Arrow keys: precision resizing", 8, (int)CANVAS_HEIGHT-30, g);
        }
        if (SELECTED_TOOL == CAMERA_TOOL) {
            drawString("Press C to move camera to origin", 8, (int)CANVAS_HEIGHT-30, g);
            drawString("Press X to reset camera", 8, (int)CANVAS_HEIGHT-50, g);
        }
        
    }
    
    public static ArrayList<SceneObject> getObjectsByType(String type) {
        ArrayList<SceneObject> list = new ArrayList<SceneObject>();
        if (type == null) return list;
        if (type.isEmpty()) return list;
        for (Level l: LEVELS) {
            for (SceneObject o: l.ALL_OBJECTS) {
                if (type.equals(o.CLASS)) {
                    list.add(o);
                }
            }
        }
        return list;
    }
    
    public static void drawString(String str, int x, int y, Graphics g) {
        g.setColor(Color.gray.darker());
        g.drawString(str, x+1, y+1);
        g.setColor(Color.white);
        g.drawString(str, x, y);
    }
    
    public static void addLevel(Level l) {
        LEVELS.add(l);
    }
    
    public static void addToScene(SceneObject o) {
        CURRENT_LEVEL.add(o);
    }
    
    public static SceneObject getObject(int onscreen_x, int onscreen_y) {
        ArrayList<SceneObject> all_objects = new ArrayList<SceneObject>();
        all_objects.addAll(CURRENT_LEVEL.DISTANT_OBJECTS);
        all_objects.addAll(CURRENT_LEVEL.BACKGROUND_OBJECTS);
        all_objects.addAll(CURRENT_LEVEL.NORMAL_OBJECTS);
        all_objects.addAll(CURRENT_LEVEL.FOREGROUND_OBJECTS);
        for (int i = all_objects.size()-1; i != -1; i--) {
            SceneObject o = all_objects.get(i);
            int[] on_screen = o.getOnscreenCoordinates();
            if (MiscMath.pointIntersects(onscreen_x, onscreen_y, 
                    on_screen[0], on_screen[1], o.getOnscreenWidth(), o.getOnscreenHeight())) {
                return o;
            }
        }
        return null;
    }
    
    public static void resizeLevel(double x, double y) {
        CURRENT_LEVEL.WIDTH += x; CURRENT_LEVEL.HEIGHT += y;
        if (CURRENT_LEVEL.WIDTH < 1) {
            CURRENT_LEVEL.WIDTH = 1;
        }
        if (CURRENT_LEVEL.HEIGHT < 1) {
            CURRENT_LEVEL.HEIGHT = 1;
        }
    }
    
    public static void zoomCamera(int amount) {
        double w_x_before = ((LAST_MOUSE_X-ORIGIN_X)/Scene.ZOOM)+ORIGIN_X;
        double w_y_before = ((LAST_MOUSE_Y-ORIGIN_Y)/Scene.ZOOM)+ORIGIN_Y;
        ZOOM+=amount;
        if (ZOOM < 1) {
            ZOOM = 1;
        }
        if (ZOOM > 12) {
            ZOOM = 12;
        }
        double w_x_after = ((LAST_MOUSE_X-ORIGIN_X)/Scene.ZOOM)+ORIGIN_X;
        double w_y_after = ((LAST_MOUSE_Y-ORIGIN_Y)/Scene.ZOOM)+ORIGIN_Y;
        ORIGIN_X += (w_x_after-w_x_before)*ZOOM;
        ORIGIN_Y += (w_y_after-w_y_before)*ZOOM;
    }
    
    public static void saveProject(boolean verbose) {
        createProjectDirs(Scene.PROJECT_NAME);
        Properties prop = new Properties();
        prop.setProperty("LvlCount", LEVELS.size()+"");
        prop.setProperty("GallObjCount", OBJECT_GALLERY.size()+"");
        for (int g = 0; g != OBJECT_GALLERY.size(); g++) {
            saveObject(OBJECT_GALLERY.get(g), -1, g, prop, true);
        }
        for(int l = 0; l != LEVELS.size(); l++) {
            //level properties
            Level level = LEVELS.get(l);
            prop.setProperty("Lvl"+l+"TopBGColor", level.R1+" "+level.G1+" "+level.B1);
            prop.setProperty("Lvl"+l+"BottomBGColor", level.R2+" "+level.G2+" "+level.B2);
            prop.setProperty("Lvl"+l+"AmbientColor", level.R3+" "+level.G3+" "+level.B3);
            prop.setProperty("Lvl"+l+"AmbientIntensity", ""+level.AMBIENT_INTENSITY);
            prop.setProperty("Lvl"+l+"AmbientSound", level.AMBIENT_SOUND);
            prop.setProperty("Lvl"+l+"LoopBGMusic", ""+level.LOOP_BG_MUSIC);
            prop.setProperty("Lvl"+l+"LoopAmbientSound", ""+level.LOOP_AMBIENT_SOUND);
            prop.setProperty("Lvl"+l+"Zoom", ""+level.ZOOM);
            prop.setProperty("Lvl"+l+"BGMusicVolume", ""+level.MUSIC_VOLUME);
            prop.setProperty("Lvl"+l+"AmbientSoundVolume", ""+level.AMBIENT_VOLUME);
            prop.setProperty("Lvl"+l+"AutoBGMusic", ""+level.PLAY_BG_MUSIC_AUTOMATICALLY);
            prop.setProperty("Lvl"+l+"AutoAmbientSound", ""+level.PLAY_AMBIENT_SOUND_AUTOMATICALLY);
            prop.setProperty("Lvl"+l+"ID", level.NAME);
            prop.setProperty("Lvl"+l+"Spawn", level.SPAWN_COORD[0]+" "+level.SPAWN_COORD[1]);
            prop.setProperty("Lvl"+l+"CameraPosition", level.CAM_COORD[0]+" "+level.CAM_COORD[1]);
            prop.setProperty("Lvl"+l+"Music", level.BG_MUSIC);
            prop.setProperty("Lvl"+l+"ObjectCount", level.ALL_OBJECTS.size()+"");
            prop.setProperty("Lvl"+l+"Width", (int)level.WIDTH+"");
            prop.setProperty("Lvl"+l+"Height", (int)level.HEIGHT+"");

            //objects
            for (int i = 0; i != level.ALL_OBJECTS.size(); i++) {
                saveObject(level.ALL_OBJECTS.get(i), l, i, prop, false);
            }

            //level scripts
            prop.setProperty("Lvl"+l+"ScrCount", level.SCRIPTS.size()+"");
            for (int i = 0; i != level.SCRIPTS.size(); i++) {
                prop.setProperty("Lvl"+l+"Scr"+i+"Name", level.SCRIPTS.get(i).NAME+"");
                prop.setProperty("Lvl"+l+"Scr"+i+"Content", mergeStrings(level.SCRIPTS.get(i).CONTENTS));
            }
        }
        
        //save to file and close the stream
        try {
            FileOutputStream f = new FileOutputStream(Scene.USER_HOME+"/level_editor/projects/"+PROJECT_NAME+"/project.txt");
            prop.store(f, null);
            f.close();
            if (verbose) JOptionPane.showMessageDialog(null, "Project '"+Scene.PROJECT_NAME+"' has been saved successfully!");
        } catch (IOException ex) {
            if (verbose) JOptionPane.showMessageDialog(null, "Error writing to file!\n"+ex.getLocalizedMessage());
        }
    }
    
    public static void newProject() {
        PROJECT_NAME = "project"+Math.abs(new Random().nextInt());
        LEVELS.clear();
        OBJECT_GALLERY.clear();
        SceneObject player_template = new SceneObject();
        player_template.setHitbox(false);
        player_template.CLASS = "Player";
        player_template.NAME = "player";
        player_template.GRAVITY = true;
        player_template.COLLIDES = true;
        OBJECT_GALLERY.add(player_template);
        Level home = new Level();
        home.NAME = "home";
        LEVELS.add(home);
        switchToLevel(home.NAME);
        GUI.updateWindowTitle();
        GUI.refreshGalleryListings();
    }
    
    public static void createProjectDirs(String project_name) {
        File project = new File(Scene.USER_HOME+"/level_editor/projects/"+project_name+"/");
        File assets = new File(Scene.USER_HOME+"/level_editor/projects/"+project_name+"/assets/");
        File textures = new File(Scene.USER_HOME+"/level_editor/projects/"+project_name+"/assets/textures");
        File object_textures = new File(Scene.USER_HOME+"/level_editor/projects/"+project_name+"/assets/textures/objects");
        File animation_sprites = new File(Scene.USER_HOME+"/level_editor/projects/"+project_name+"/assets/textures/animations");
        File audio_files = new File(Scene.USER_HOME+"/level_editor/projects/"+project_name+"/assets/audio/");
        if (!project.exists()) project.mkdir();
        if (!assets.exists()) assets.mkdir();
        if (!textures.exists()) textures.mkdir();
        if (!object_textures.exists()) object_textures.mkdir();
        if (!animation_sprites.exists()) animation_sprites.mkdir();
        if (!audio_files.exists()) animation_sprites.mkdir();
    }
    
    public static boolean doesProjectExist(String project_name) {
        return new File(Scene.USER_HOME+"/level_editor/projects/"+project_name+"/").exists();
    }
    
    public static void deleteLevel(String level_id) {
        for (Level l: LEVELS) {
            if (l.NAME.equals(level_id)) {
                LEVELS.remove(l);
            }
        }
    }
    
    public static void switchToLevel(String level_id) {
        for (Level l: LEVELS) {
            if (l.NAME.equals(level_id)) {
                CURRENT_LEVEL = l;
                SELECTED_OBJECT = null;
                GUI.updateWindowTitle();
                GUI.refreshObjectProperties();
                GUI.refreshLevelEditor();
            }
        }
    }
    
    public static void switchToLevel(int index) {
        CURRENT_LEVEL = LEVELS.get(index);
        SELECTED_OBJECT = null;
        GUI.updateWindowTitle();
        GUI.refreshObjectProperties();
        GUI.refreshLevelEditor();
    }
    
    public static void loadProject(String project_name) {
        LEVELS.clear();
        OBJECT_GALLERY.clear();
        CURRENT_LEVEL = null;
        SELECTED_OBJECT = null;
        Properties prop = new Properties();
        
        try {
            FileInputStream f = new FileInputStream(Scene.USER_HOME+"/level_editor/projects/"+project_name+"/project.txt");
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
        prop.setProperty(prefix+"Obj"+oindex+"Img", o.TEXTURE_NAME+".png");
        prop.setProperty(prefix+"Obj"+oindex+"Type", o.CLASS);
        prop.setProperty(prefix+"Obj"+oindex+"IsHitbox", o.isHitbox()+"");

        prop.setProperty(prefix+"Obj"+oindex+"AnimCount", o.ANIMATIONS.size()+"");
        for (int ii = 0; ii != o.ANIMATIONS.size(); ii++) {
            prop.setProperty(prefix+"Obj"+oindex+"Anim"+ii+"Name", o.ANIMATIONS.get(ii).NAME+"");
            prop.setProperty(prefix+"Obj"+oindex+"Anim"+ii+"SpriteName", o.ANIMATIONS.get(ii).SPRITE_NAME+"");
            prop.setProperty(prefix+"Obj"+oindex+"Anim"+ii+"Widths", Scene.integersToString(o.ANIMATIONS.get(ii).WIDTHS));
            prop.setProperty(prefix+"Obj"+oindex+"Anim"+ii+"Heights", Scene.integersToString(o.ANIMATIONS.get(ii).HEIGHTS));
            prop.setProperty(prefix+"Obj"+oindex+"Anim"+ii+"FrmDur", o.ANIMATIONS.get(ii).FRAME_DURATION+"");
            prop.setProperty(prefix+"Obj"+oindex+"Anim"+ii+"Loops", o.ANIMATIONS.get(ii).LOOP+"");
        }
        prop.setProperty(prefix+"Obj"+oindex+"DlgCount", o.DIALOGUES.size()+"");
        for (int ii = 0; ii != o.DIALOGUES.size(); ii++) {
            prop.setProperty(prefix+"Obj"+oindex+"Dlg"+ii+"Name", o.DIALOGUES.get(ii).NAME+"");
        }
        prop.setProperty(prefix+"Obj"+oindex+"FlowCount", o.FLOWS.size()+"");
        for (int ii = 0; ii != o.FLOWS.size(); ii++) {
            prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"Name", o.FLOWS.get(ii).NAME+"");
            prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"Run", o.FLOWS.get(ii).RUN_ON_SPAWN+"");
            prop.setProperty(prefix+"Obj"+oindex+"Flow"+ii+"BlockCount", o.FLOWS.get(ii).blockCount()+"");
            for (int iii = 0; iii != o.FLOWS.get(ii).blockCount(); iii++) {
                Block b = o.FLOWS.get(ii).getBlock(iii);
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
        /*if (prop.getProperty(prefix+"Obj"+oindex+"Img") != null)*/ o.TEXTURE_NAME = prop.getProperty(prefix+"Obj"+oindex+"Img").replace(".png", "");
        /*if (prop.getProperty(prefix+"Obj"+oindex+"Layer") != null)*/ o.LAYER = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"Layer"));
        o.GRAVITY = Boolean.parseBoolean(prop.getProperty(prefix+"Obj"+oindex+"Grav"));
        o.COLLIDES = Boolean.parseBoolean(prop.getProperty(prefix+"Obj"+oindex+"Coll"));
        o.CLASS = prop.getProperty(prefix+"Obj"+oindex+"Type");
        o.setHitbox(Boolean.parseBoolean(prop.getProperty(prefix+"Obj"+oindex+"IsHitbox")));

        int anim_count = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"AnimCount"));
        for (int ii = 0; ii != anim_count; ii++) {
            Animation s = new Animation();
            o.ANIMATIONS.add(s);
            s.NAME = prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Name");
            s.SPRITE_NAME = prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"SpriteName");
            s.WIDTHS = parseIntegers(prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Widths"), true);
            s.HEIGHTS = parseIntegers(prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Heights"), true);
            s.LOOP = Boolean.parseBoolean(prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Loops"));
            s.FRAME_DURATION = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"FrmDur"));
        }
        int dialogue_count = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"DlgCount"));
        for (int ii = 0; ii != dialogue_count; ii++) {
            Dialogue d = new Dialogue();
            o.DIALOGUES.add(d);
            d.NAME = prop.getProperty(prefix+"Obj"+oindex+"Dlg"+ii+"Name");
        }
        int flow_count = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"FlowCount"));
        for (int ii = 0; ii != flow_count; ii++) {
            Flow d = new Flow();
            o.FLOWS.add(d);
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
                ArrayList<Integer> coords = Scene.parseIntegers(prop.getProperty(prefix+"Obj"+oindex+"Flow"+ii+"Block"+iii+"Coords"), true);
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
        level.AMBIENT_INTENSITY = Integer.parseInt(prop.getProperty("Lvl"+index+"AmbientIntensity"));
        level.AMBIENT_VOLUME = Float.parseFloat(prop.getProperty("Lvl"+index+"AmbientSoundVolume"));
        level.MUSIC_VOLUME = Float.parseFloat(prop.getProperty("Lvl"+index+"BGMusicVolume"));
        level.SPAWN_COORD = new int[]{spwn.get(0), spwn.get(1)};
        level.CAM_COORD = new int[]{cam_pos.get(0), cam_pos.get(1)};
        level.AMBIENT_SOUND = prop.getProperty("Lvl"+index+"AmbientSound");
        level.NAME = prop.getProperty("Lvl"+index+"ID");
        level.BG_MUSIC = prop.getProperty("Lvl"+index+"Music");
        level.ZOOM = Integer.parseInt(prop.getProperty("Lvl"+index+"Zoom"));
        level.WIDTH = Integer.parseInt(prop.getProperty("Lvl"+index+"Width"));
        level.HEIGHT = Integer.parseInt(prop.getProperty("Lvl"+index+"Height"));
        level.LOOP_BG_MUSIC = Boolean.parseBoolean(prop.getProperty("Lvl"+index+"LoopBGMusic"));
        level.LOOP_AMBIENT_SOUND = Boolean.parseBoolean(prop.getProperty("Lvl"+index+"LoopAmbientSound"));
        level.PLAY_BG_MUSIC_AUTOMATICALLY = Boolean.parseBoolean(prop.getProperty("Lvl"+index+"AutoBGMusic"));
        level.PLAY_AMBIENT_SOUND_AUTOMATICALLY = Boolean.parseBoolean(prop.getProperty("Lvl"+index+"AutoAmbientSound"));

        int obj_count = Integer.parseInt(prop.getProperty("Lvl"+index+"ObjectCount"));
        for (int i = 0; i != obj_count; i++) {
            SceneObject o = loadObject(index, i, prop, false);
            if (o != null) level.add(o);
        }
        
        addLevel(level);

    }
    
    public static boolean sceneObjectExists(String name) {
        for (SceneObject o: CURRENT_LEVEL.ALL_OBJECTS) {
            if (o.NAME.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean scriptExists(String name) {
        for (Script o: CURRENT_LEVEL.SCRIPTS) {
            if (o.NAME.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public static void mkdirs() {
        new File(Scene.USER_HOME+"/level_editor").mkdir();
        new File(Scene.USER_HOME+"/level_editor/jars").mkdir();
        new File(Scene.USER_HOME+"/level_editor/projects").mkdir();
    }
    
    /**
     * Loads all assets from the project's 'objects' folder, into the OBJECT_TEXTURES list. Clears all previously loaded assets
     * first. Uses Scene.PROJECT_NAME in the directory.
     */
    public static void loadAssets() {
        File object_textures = new File(Scene.USER_HOME+"/level_editor/projects/"+PROJECT_NAME+"/assets/textures/objects");
        File anim_textures = new File(Scene.USER_HOME+"/level_editor/projects/"+PROJECT_NAME+"/assets/textures/animations");
        OBJECT_TEXTURES.clear();
        OBJECT_TEXTURE_NAMES.clear();
        ANIMATION_TEXTURES.clear();
        ANIMATION_TEXTURE_NAMES.clear();
        addToAssets(object_textures, OBJECT_TEXTURE_NAMES, OBJECT_TEXTURES);
        addToAssets(anim_textures, ANIMATION_TEXTURE_NAMES, ANIMATION_TEXTURES);
    }
    
    private static void addToAssets(File textures, ArrayList<String> names, ArrayList<BufferedImage> imgs) {
        if (textures.isDirectory()) {
            File[] files = textures.listFiles();
            for (File f: files) {
                if (f.getName().contains(".png")) {
                    BufferedImage img;
                    try {
                        img = ImageIO.read(f);
                        imgs.add(img);
                        names.add(f.getName().replace(".png", ""));
                        System.out.println("Loaded asset "+names.get(names.size()-1)+".png");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Error loading assets!\n"+e.getLocalizedMessage());
                        names.clear();
                        imgs.clear();
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Breaks any String object into a list of Strings. The character "\n" acts as a breakpoint.
     * @param s The String to parse.
     * @return An ArrayList of Strings for your viewing pleasure.
     */
    public static ArrayList<String> parseString(String s) {
        ArrayList<String> strs = new ArrayList<String>();
        String command = "";
        System.out.println("Parsing string: "+s);
        for (int i = 0; i != s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\n') {
                strs.add(command);
                command = "";
            } else {
                command += c;
                if (i == s.length()-1) {
                    strs.add(command);
                }
            }
        }
        System.out.println("Size of generated array: "+strs.size());
        return strs;
    }
    
    public static String integersToString(ArrayList<Integer> int_arr) {
        String c = "";
        for (int s: int_arr) {
            c+=s+" ";
        }
        return c.trim();
    }
    
    /**
     * Returns a String object containing every element in the specified array, separated by "\n". Used for writing to the save file.
     * @param str_arr
     * @return A String object.
     */
    public static String mergeStrings(ArrayList<String> str_arr) {
        String c = "";
        for (String s: str_arr) {
            c+=s+"\n";
        }
        return c;
    }
    
    /**
     * Takes a String consisting of a set of integers separated by spaces, and returns a filled ArrayList
     * containing said integers. If the String could not be parsed (ex. has invalid characters), it returns
     * an ArrayList with 420 elements, all with a value of 0.
     * @param s The String to read.
     * @param verbose Should a message dialog be opened if an error occurs?
     * @return An ArrayList<Integer> (see above).
     */
    public static ArrayList<Integer> parseIntegers(String s, boolean verbose) {
        ArrayList<Integer> values = new ArrayList<Integer>();
        boolean stop = false;
        if (s != null) {
            if (s.length() == 0) {
                stop = true;
            }
        } else {
            stop = true;
        }
        if (stop) {
            for (int i = 0; i != 420; i++) {
                values.add(0);
            }
            return values;
        }
        try {
            int val = 0;
            for (int i = 0; i != s.length(); i++) {
                char c = s.charAt(i);
                if (c == ' ') {
                    values.add(val);
                    val = 0;
                } else {
                    val *= 10;
                    val += Integer.parseInt(s.charAt(i)+"");
                    if (i == s.length()-1) {
                        values.add(val);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (verbose) JOptionPane.showMessageDialog(null, "Error reading the input!\n"+ex.getMessage());
            for (int i = 0; i != 420; i++) {
                values.add(0);
            }
        }
        return values;
    }
    
    public static void deleteDirectory(File dir) {
        File[] list = dir.listFiles();
        for (File f: list) {
            if (f.isDirectory()) deleteDirectory(f); else f.delete();
        }
        dir.delete();
    }
    
}
