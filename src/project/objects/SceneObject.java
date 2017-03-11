package project.objects;

import project.objects.components.Animation;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import project.objects.components.Dialogue;
import project.objects.components.Flow;
import project.Project;

public class SceneObject {
    
    private double world_x, world_y, world_w, world_h;
    private String name, type;
    private int layer = 1;
    private boolean grav, collides, locked;
    
    public ArrayList<Animation> ANIMATIONS = new ArrayList<Animation>();
    public ArrayList<Dialogue> DIALOGUES = new ArrayList<Dialogue>();
    public ArrayList<Flow> FLOWS = new ArrayList<Flow>();
    
    boolean hitbox = false;
    
    public String texture;
    
    public SceneObject(int world_x, int world_y, int world_w, int world_h, String name) {
        this.world_x = world_x;
        this.world_y = world_y;
        this.world_w = world_w;
        this.world_h = world_h;
        this.name = name;
        this.type = "";
        this.layer = 2;
        this.grav = false;
        this.collides = true;
        this.texture = "";
    }
    
    public SceneObject() {
        this.layer = 2;
        this.grav = false;
        this.type = "";
        this.collides = true;
        this.name = "";
        this.texture = "";
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
    
    public boolean containsDialogue(String name) {
        for (Dialogue o: DIALOGUES) {
            if (o.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsAnimation(String name) {
        for (Animation o: ANIMATIONS) {
            if (o.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsFlow(String name) {
        for (Flow o: FLOWS) {
            if (o.getName().equals(name)) {
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
        return new int[]{(int)(Project.getOriginX()+(world_x*Project.ZOOM)), 
                (int)(Project.getOriginY()+(world_y*Project.ZOOM))};
    }
    
    public int getOnscreenWidth() {
        return (int)(world_w*Project.ZOOM);
    }
    
    public int getOnscreenHeight() {
        return (int)(world_h*Project.ZOOM);
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
        o.texture = this.texture;
        o.class = this.class;
        o.name = this.name+Math.abs(new Random().nextInt() % 10000);
        o.LAYER = this.LAYER;
        o.GRAVITY = this.GRAVITY;
        o.COLLIDES = this.COLLIDES;
        o.hitbox = this.hitbox;

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
        if (Project.OBJECT_TEXTURE_NAMES.contains(this.texture)) {
            texture = Project.OBJECT_TEXTURES.get(Project.OBJECT_TEXTURE_NAMES.indexOf(this.texture));
        }
        
        if (isHitbox()) {
            g.setColor(new Color(50, 50, 100, 100));
            g.fillRect(
                    (int)(Project.ORIGIN_X+((int)world_x*Project.ZOOM)), 
                    (int)(Project.ORIGIN_Y+((int)world_y*Project.ZOOM)), 
                    (int)((int)world_w*Project.ZOOM),
                    (int)((int)world_h*Project.ZOOM));
        } else {
            if (texture != null && Project.OBJECT_TEXTURE_NAMES.contains(this.texture)) {
                g.setColor(Color.white);
                g.drawImage(texture.getScaledInstance((int)((int)world_w*Project.ZOOM),
                        (int)((int)world_h*Project.ZOOM), Image.SCALE_FAST), (int)(Project.ORIGIN_X+((int)world_x*Project.ZOOM)), 
                        (int)(Project.ORIGIN_Y+((int)world_y*Project.ZOOM)), null);
            } else {
                g.setColor(Color.red);
                g.drawRect(
                    (int)(Project.ORIGIN_X+((int)world_x*Project.ZOOM)), 
                    (int)(Project.ORIGIN_Y+((int)world_y*Project.ZOOM)), 
                    (int)((int)world_w*Project.ZOOM),
                    (int)((int)world_h*Project.ZOOM));
                g.drawLine((int)(Project.ORIGIN_X+((int)world_x*Project.ZOOM)), 
                    (int)(Project.ORIGIN_Y+((int)world_y*Project.ZOOM)),
                    (int)(Project.ORIGIN_X+((int)world_x*Project.ZOOM))+(int)((int)world_w*Project.ZOOM), 
                    (int)(Project.ORIGIN_Y+((int)world_y*Project.ZOOM))+(int)((int)world_h*Project.ZOOM));
                g.setColor(Color.white);
                Project.drawString(this.texture+".png", getOnscreenCoordinates()[0], getOnscreenCoordinates()[1], g);
            }
        }
        if (this.equals(Project.SELECTED_OBJECT)) {
            g.setColor(Color.cyan.darker());
            g.drawRect(
                (int)(Project.ORIGIN_X+((int)world_x*Project.ZOOM))-1, 
                (int)(Project.ORIGIN_Y+((int)world_y*Project.ZOOM))-1, 
                (int)((int)world_w*Project.ZOOM)+2,
                (int)((int)world_h*Project.ZOOM)+2);
        }
    }
    
}
