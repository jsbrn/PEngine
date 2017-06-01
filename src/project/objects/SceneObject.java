package project.objects;

import gui.GUI;
import gui.SceneCanvas;
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

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
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
        return new int[]{(int)(GUI.getSceneCanvas().getOriginX()+(world_x*GUI.getSceneCanvas().getZoom())), 
                (int)(GUI.getSceneCanvas().getOriginY()+(world_y*GUI.getSceneCanvas().getZoom()))};
    }
    
    public int getOnscreenWidth() {
        return (int)(world_w*GUI.getSceneCanvas().getZoom());
    }
    
    public int getOnscreenHeight() {
        return (int)(world_h*GUI.getSceneCanvas().getZoom());
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
     * Copies all properties and values (except for the unique name) to the specified object.
     * @param o The specified object.
     */
    public void copyTo(SceneObject o) {
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
    }
    
    public void draw(Graphics g) {
        BufferedImage texture = null;
        if (Assets.OBJECT_TEXTURE_NAMES.contains(this.texture)) {
            texture = Assets.OBJECT_TEXTURES.get(Assets.OBJECT_TEXTURE_NAMES.indexOf(this.texture));
        }
        
        SceneCanvas canvas = GUI.getSceneCanvas();
        
        if (isHitbox()) {
            g.setColor(new Color(50, 50, 100, 100));
            g.fillRect(
                    (int)(canvas.getOriginX()+((int)world_x*canvas.getZoom())), 
                    (int)(canvas.getOriginY()+((int)world_y*canvas.getZoom())), 
                    (int)((int)world_w*canvas.getZoom()),
                    (int)((int)world_h*canvas.getZoom()));
        } else {
            if (texture != null && Assets.OBJECT_TEXTURE_NAMES.contains(this.texture)) {
                g.setColor(Color.white);
                g.drawImage(texture.getScaledInstance((int)((int)world_w*canvas.getZoom()),
                        (int)((int)world_h*canvas.getZoom()), Image.SCALE_FAST), (int)(canvas.getOriginX()+((int)world_x*canvas.getZoom())), 
                        (int)(canvas.getOriginY()+((int)world_y*canvas.getZoom())), null);
            } else {
                g.setColor(Color.red);
                g.drawRect(
                    (int)(canvas.getOriginX()+((int)world_x*canvas.getZoom())), 
                    (int)(canvas.getOriginY()+((int)world_y*canvas.getZoom())), 
                    (int)((int)world_w*canvas.getZoom()),
                    (int)((int)world_h*canvas.getZoom()));
                g.drawLine((int)(canvas.getOriginX()+((int)world_x*canvas.getZoom())), 
                    (int)(canvas.getOriginY()+((int)world_y*canvas.getZoom())),
                    (int)(canvas.getOriginX()+((int)world_x*canvas.getZoom()))+(int)((int)world_w*canvas.getZoom()), 
                    (int)(canvas.getOriginY()+((int)world_y*canvas.getZoom()))+(int)((int)world_h*canvas.getZoom()));
                g.setColor(Color.white);
                canvas.drawString(this.texture+".png", getOnscreenCoordinates()[0], getOnscreenCoordinates()[1], g);
            }
        }
        if (this.equals(canvas.getSelectedObject())) {
            g.setColor(Color.cyan.darker());
            g.drawRect(
                (int)(canvas.getOriginX()+((int)world_x*canvas.getZoom()))-1, 
                (int)(canvas.getOriginY()+((int)world_y*canvas.getZoom()))-1, 
                (int)((int)world_w*canvas.getZoom())+2,
                (int)((int)world_h*canvas.getZoom())+2);
        }
    }
    
    public ArrayList<Animation> getAnimations() { return animations; }

    public ArrayList<Dialogue> getDialogues() { return dialogues; }

    public ArrayList<Flow> getFlows() { return flows; }
    
    @Override
    public String toString() {
        return Project.getProject().containsGalleryObject(this) ? this.getType() : this.getName();
    }
    
}
