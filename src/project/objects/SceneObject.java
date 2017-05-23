package project.objects;

import gui.GUI;
import project.objects.components.Animation;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import misc.Assets;
import project.objects.components.Dialogue;
import project.objects.components.Flow;
import project.Project;

public class SceneObject {
    
    private double world_x, world_y, world_w, world_h;
    private String name, type;
    private int layer = 1;
    private boolean grav, collides, locked;
    
    public ArrayList<Animation> animations = new ArrayList<Animation>();
    public ArrayList<Dialogue> dialogues = new ArrayList<Dialogue>();
    private ArrayList<Flow> flows = new ArrayList<Flow>();
    
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
        for (Dialogue o: dialogues) {
            if (o.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsAnimation(String name) {
        for (Animation o: animations) {
            if (o.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsFlow(String name) {
        for (Flow o: flows) {
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
    
    public String getType() { return type; }
    
    public void setType(String type) { this.type = type; }
    
    public void setName(String name) { this.name = name; }
    
    public String getName() { return name; }
    
    public int getLayer() { return layer; }
    
    public boolean gravity() { return grav; }
    
    public boolean collides() { return collides; }
    
    public void setGravity(boolean g) { this.grav = g; }
    
    public void setCollides(boolean c) { this.collides = c; }
    
    public int[] getWorldCoordinates() {
        return new int[]{(int)world_x, (int)world_y};
    }
    
    public int[] getOnscreenCoordinates() {
        return new int[]{(int)(GUI.getSceneCanvas().getOriginX()+(world_x*GUI.getSceneCanvas().ZOOM)), 
                (int)(GUI.getSceneCanvas().getOriginY()+(world_y*GUI.getSceneCanvas().ZOOM))};
    }
    
    public int getOnscreenWidth() {
        return (int)(world_w*GUI.getSceneCanvas().ZOOM);
    }
    
    public int getOnscreenHeight() {
        return (int)(world_h*GUI.getSceneCanvas().ZOOM);
    }
    
    public int[] getDimensions() {
        return new int[]{(int)world_w, (int)world_h};
    }
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("so\n");
            
            bw.write("x="+world_x+"\n");
            bw.write("y="+world_y+"\n");
            bw.write("w="+world_w+"\n");
            bw.write("h="+world_h+"\n");
            bw.write("n="+name+"\n");
            bw.write("t="+type+"\n");
            bw.write("lk="+locked+"\n");
            bw.write("tx="+texture+"\n");
            bw.write("h="+hitbox+"\n");
            bw.write("g="+grav+"\n");
            bw.write("c="+collides+"\n");
            bw.write("l="+layer+"\n");
            
            for (Flow f: flows) f.save(bw);
            for (Animation a: animations) a.save(bw);
            for (Dialogue d: dialogues) d.save(bw);
            
            bw.write("/so");
            
        } catch (IOException ex) {
            Logger.getLogger(Animation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Creates a new object with exactly the same content, except for the name.
     * @return SceneObject
     */
    public SceneObject copy() {
        SceneObject o = new SceneObject();
        o.texture = this.texture;
        o.type = this.type;
        o.name = this.name+Math.abs(new Random().nextInt() % 10000);
        o.layer = this.layer;
        o.grav = this.grav;
        o.collides = this.collides;
        o.hitbox = this.hitbox;

        o.animations.clear();
        for (Animation a: this.animations) {
            Animation new_a = new Animation();
            a.copyTo(new_a);
            o.animations.add(new_a);
        }
        
        o.dialogues.clear();
        for (Dialogue d: this.dialogues) {
            Dialogue new_d = new Dialogue();
            d.copyTo(new_d);
            o.dialogues.add(new_d);
            new_d.setParent(o);
            System.out.println("Adding "+new_d+" to "+o);
        }
        
        o.flows.clear();
        for (Flow f: this.flows) {
            Flow new_f = new Flow();
            f.copyTo(new_f);
            o.flows.add(new_f);
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
        if (Assets.OBJECT_TEXTURE_NAMES.contains(this.texture)) {
            texture = Assets.OBJECT_TEXTURES.get(Assets.OBJECT_TEXTURE_NAMES.indexOf(this.texture));
        }
        
        if (isHitbox()) {
            g.setColor(new Color(50, 50, 100, 100));
            g.fillRect(
                    (int)(GUI.getSceneCanvas().ORIGIN_X+((int)world_x*GUI.getSceneCanvas().ZOOM)), 
                    (int)(GUI.getSceneCanvas().ORIGIN_Y+((int)world_y*GUI.getSceneCanvas().ZOOM)), 
                    (int)((int)world_w*GUI.getSceneCanvas().ZOOM),
                    (int)((int)world_h*GUI.getSceneCanvas().ZOOM));
        } else {
            if (texture != null && Assets.OBJECT_TEXTURE_NAMES.contains(this.texture)) {
                g.setColor(Color.white);
                g.drawImage(texture.getScaledInstance((int)((int)world_w*Project.ZOOM),
                        (int)((int)world_h*Project.ZOOM), Image.SCALE_FAST), (int)(Project.ORIGIN_X+((int)world_x*Project.ZOOM)), 
                        (int)(Project.ORIGIN_Y+((int)world_y*Project.ZOOM)), null);
            } else {
                g.setColor(Color.red);
                g.drawRect(
                    (int)(GUI.getSceneCanvas().ORIGIN_X+((int)world_x*GUI.getSceneCanvas().ZOOM)), 
                    (int)(GUI.getSceneCanvas().ORIGIN_Y+((int)world_y*GUI.getSceneCanvas().ZOOM)), 
                    (int)((int)world_w*GUI.getSceneCanvas().ZOOM),
                    (int)((int)world_h*GUI.getSceneCanvas().ZOOM));
                g.drawLine((int)(GUI.getSceneCanvas().ORIGIN_X+((int)world_x*GUI.getSceneCanvas().ZOOM)), 
                    (int)(GUI.getSceneCanvas().ORIGIN_Y+((int)world_y*GUI.getSceneCanvas().ZOOM)),
                    (int)(GUI.getSceneCanvas().ORIGIN_X+((int)world_x*GUI.getSceneCanvas().ZOOM))+(int)((int)world_w*GUI.getSceneCanvas().ZOOM), 
                    (int)(GUI.getSceneCanvas().ORIGIN_Y+((int)world_y*GUI.getSceneCanvas().ZOOM))+(int)((int)world_h*GUI.getSceneCanvas().ZOOM));
                g.setColor(Color.white);
                GUI.getSceneCanvas().drawString(this.texture+".png", getOnscreenCoordinates()[0], getOnscreenCoordinates()[1], g);
            }
        }
        if (this.equals(GUI.getSceneCanvas().getSelectedObject())) {
            g.setColor(Color.cyan.darker());
            g.drawRect(
                (int)(GUI.getSceneCanvas().ORIGIN_X+((int)world_x*GUI.getSceneCanvas().ZOOM))-1, 
                (int)(GUI.getSceneCanvas().ORIGIN_Y+((int)world_y*GUI.getSceneCanvas().ZOOM))-1, 
                (int)((int)world_w*GUI.getSceneCanvas().ZOOM)+2,
                (int)((int)world_h*GUI.getSceneCanvas().ZOOM)+2);
        }
    }
    
    public ArrayList<Animation> getAnimations() { return animations; }

    public ArrayList<Dialogue> getDialogues() { return dialogues; }

    public ArrayList<Flow> getFlows() { return flows; }
    
    
}
