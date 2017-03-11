package scene;

import misc.Script;
import misc.Animation;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import misc.Dialogue;
import misc.Flow;

public class SceneObject {
    
    private double world_x, world_y, world_w, world_h;
    public String NAME, CLASS;
    public int LAYER = 1;
    public boolean GRAVITY, COLLIDES, LOCKED;
    
    public ArrayList<Script> SCRIPTS = new ArrayList<Script>();
    public ArrayList<Animation> ANIMATIONS = new ArrayList<Animation>();
    public ArrayList<Dialogue> DIALOGUES = new ArrayList<Dialogue>();
    public ArrayList<Flow> FLOWS = new ArrayList<Flow>();
    
    boolean hitbox = false;
    
    public String TEXTURE_NAME;
    
    public SceneObject(int world_x, int world_y, int world_w, int world_h, String name) {
        this.world_x = world_x;
        this.world_y = world_y;
        this.world_w = world_w;
        this.world_h = world_h;
        this.NAME = name;
        this.CLASS = "";
        this.LAYER = 2;
        this.GRAVITY = false;
        this.COLLIDES = true;
        this.TEXTURE_NAME = "";
    }
    
    public SceneObject() {
        this.LAYER = 2;
        this.GRAVITY = false;
        this.CLASS = "";
        this.COLLIDES = true;
        this.NAME = "";
        this.TEXTURE_NAME = "";
    }
    
    public ArrayList<String> getErrors() {
        ArrayList<String> list = new ArrayList<String>();
        boolean disconnect = true;
        for (SceneObject o: Scene.OBJECT_GALLERY) { if (o.CLASS.equals(CLASS)) disconnect = false; }
        if (disconnect && !isHitbox()) list.add("        Object type '"+CLASS+"' cannot be found in the Gallery!\n");
        if (Scene.OBJECT_TEXTURE_NAMES.indexOf(TEXTURE_NAME) == -1 && !isHitbox()) 
            list.add("Texture file '"+TEXTURE_NAME+".png' cannot be found in the assets folder!\n");
        for (Script s: SCRIPTS) { list.addAll(s.getErrors()); }
        for (Animation s: ANIMATIONS) { list.addAll(s.getErrors()); }
        for (Dialogue s: DIALOGUES) { list.addAll(s.getErrors()); }
        return list;
    }
    
    public void setHitbox(boolean b) {
        hitbox = b;
    }
    
    public boolean isHitbox() {
        return hitbox;
    }
    
    public void move(double x, double y) {
        world_x += x; world_y += y;
        if (world_x < 0) {
            world_x = 0;
        }
        if (world_y < 0) {
            world_y = 0;
        }
    }
    
    public boolean dialogueExists(String name) {
        for (Dialogue o: DIALOGUES) {
            if (o.NAME.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean animationExists(String name) {
        for (Animation o: ANIMATIONS) {
            if (o.NAME.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean flowExists(String name) {
        for (Flow o: FLOWS) {
            if (o.NAME.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean scriptExists(String name) {
        for (Script o: SCRIPTS) {
            if (o.NAME.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public void resize(double x, double y) {
        world_w += x; world_h += y;
        if (world_w < 1) {
            world_w = 1;
        }
        if (world_h < 1) {
            world_h = 1;
        }
    }
    
    public void setWidth(int w) {
        world_w = w;
    }
    
    public void setHeight(int h) {
        world_h = h;
    }
    
    public void setWorldX(int x) {
        world_x = x;
        if (world_x < 0) { world_x = 0; }
    }
    
    public void setWorldY(int y) {
        world_y = y;
        if (world_y < 0) { world_y = 0; }
    }
    
    public int[] getWorldCoordinates() {
        return new int[]{(int)world_x, (int)world_y};
    }
    
    public int[] getOnscreenCoordinates() {
        return new int[]{(int)(Scene.ORIGIN_X+(world_x*Scene.ZOOM)), 
                (int)(Scene.ORIGIN_Y+(world_y*Scene.ZOOM))};
    }
    
    public int getOnscreenWidth() {
        return (int)(world_w*Scene.ZOOM);
    }
    
    public int getOnscreenHeight() {
        return (int)(world_h*Scene.ZOOM);
    }
    
    public int[] getDimensions() {
        return new int[]{(int)world_w, (int)world_h};
    }
    
    /**
     * Creates a new object with exactly the same content, except for the name.
     * @return SceneObject
     */
    public SceneObject copy() {
        SceneObject o = new SceneObject();
        o.TEXTURE_NAME = this.TEXTURE_NAME;
        o.CLASS = this.CLASS;
        o.NAME = this.NAME+Math.abs(new Random().nextInt() % 10000);
        o.LAYER = this.LAYER;
        o.GRAVITY = this.GRAVITY;
        o.COLLIDES = this.COLLIDES;
        o.hitbox = this.hitbox;
        o.SCRIPTS.clear();
        for (Script s: this.SCRIPTS) {
            Script new_s = new Script();
            s.copyTo(new_s);
            o.SCRIPTS.add(new_s);
            new_s.setParent(o);
        }
        o.ANIMATIONS.clear();
        for (Animation a: this.ANIMATIONS) {
            Animation new_a = new Animation();
            a.copyTo(new_a);
            o.ANIMATIONS.add(new_a);
        }
        o.DIALOGUES.clear();
        for (Dialogue d: this.DIALOGUES) {
            Dialogue new_d = new Dialogue();
            d.copyTo(new_d);
            o.DIALOGUES.add(new_d);
            new_d.setParent(o);
            System.out.println("Adding "+new_d+" to "+o);
        }
        o.FLOWS.clear();
        for (Flow f: this.FLOWS) {
            Flow new_f = new Flow();
            f.copyTo(new_f);
            o.FLOWS.add(new_f);
            new_f.setParent(o);
        }
        o.setWorldX(this.getWorldCoordinates()[0] + 5);
        o.setWorldY(this.getWorldCoordinates()[1] + 5);
        o.setWidth(this.getDimensions()[0]);
        o.setHeight(this.getDimensions()[1]);
        return o;
    }
    
    public void draw(Graphics g) {
        BufferedImage texture = null;
        if (Scene.OBJECT_TEXTURE_NAMES.contains(TEXTURE_NAME)) {
            texture = Scene.OBJECT_TEXTURES.get(Scene.OBJECT_TEXTURE_NAMES.indexOf(TEXTURE_NAME));
        }
        
        if (isHitbox()) {
            g.setColor(new Color(50, 50, 100, 100));
            g.fillRect(
                    (int)(Scene.ORIGIN_X+((int)world_x*Scene.ZOOM)), 
                    (int)(Scene.ORIGIN_Y+((int)world_y*Scene.ZOOM)), 
                    (int)((int)world_w*Scene.ZOOM),
                    (int)((int)world_h*Scene.ZOOM));
        } else {
            if (texture != null && Scene.OBJECT_TEXTURE_NAMES.contains(TEXTURE_NAME)) {
                g.setColor(Color.white);
                g.drawImage(texture.getScaledInstance((int)((int)world_w*Scene.ZOOM),
                        (int)((int)world_h*Scene.ZOOM), Image.SCALE_FAST), (int)(Scene.ORIGIN_X+((int)world_x*Scene.ZOOM)), 
                        (int)(Scene.ORIGIN_Y+((int)world_y*Scene.ZOOM)), null);
            } else {
                g.setColor(Color.red);
                g.drawRect(
                    (int)(Scene.ORIGIN_X+((int)world_x*Scene.ZOOM)), 
                    (int)(Scene.ORIGIN_Y+((int)world_y*Scene.ZOOM)), 
                    (int)((int)world_w*Scene.ZOOM),
                    (int)((int)world_h*Scene.ZOOM));
                g.drawLine((int)(Scene.ORIGIN_X+((int)world_x*Scene.ZOOM)), 
                    (int)(Scene.ORIGIN_Y+((int)world_y*Scene.ZOOM)),
                    (int)(Scene.ORIGIN_X+((int)world_x*Scene.ZOOM))+(int)((int)world_w*Scene.ZOOM), 
                    (int)(Scene.ORIGIN_Y+((int)world_y*Scene.ZOOM))+(int)((int)world_h*Scene.ZOOM));
                g.setColor(Color.white);
                Scene.drawString(TEXTURE_NAME+".png", getOnscreenCoordinates()[0], getOnscreenCoordinates()[1], g);
            }
        }
        if (this.equals(Scene.SELECTED_OBJECT)) {
            g.setColor(Color.cyan.darker());
            g.drawRect(
                (int)(Scene.ORIGIN_X+((int)world_x*Scene.ZOOM))-1, 
                (int)(Scene.ORIGIN_Y+((int)world_y*Scene.ZOOM))-1, 
                (int)((int)world_w*Scene.ZOOM)+2,
                (int)((int)world_h*Scene.ZOOM)+2);
        }
    }
    
}
