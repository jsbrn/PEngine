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
import misc.Assets;
import misc.MiscMath;
import project.Level;
import project.objects.components.Flow;
import project.Project;

public class SceneObject {
    
    private double world_x, world_y, world_w, world_h;
    private String name, type;
    private int layer = Level.MIDDLE_OBJECTS;
    private boolean grav, collides, locked;
    
    public ArrayList<Animation> animations = new ArrayList<Animation>();
    private ArrayList<Flow> flows = new ArrayList<Flow>();
    
    boolean hitbox = false;
    
    public String texture;
    
    public SceneObject() {
        this.layer = Level.MIDDLE_OBJECTS;
        this.grav = false;
        this.type = "";
        this.collides = true;
        this.name = "";
        this.texture = "";
        this.world_w = 16;
        this.world_h = 16;
    }
    
    public void setHitbox(boolean b) {
        hitbox = b;
    }
    
    public boolean isHitbox() {
        return hitbox;
    }
    
    public void move(double x, double y) {
        world_x += x; world_y += y;
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
        world_w = world_w < 2 ? 2 : world_w;
        world_h = world_h < 2 ? 2 : world_h;
    }
    
    public void setLayer(int l) { layer = l; }
    
    public void setWidth(int w) {
        world_w = w;
    }
    
    public void setHeight(int h) {
        world_h = h;
    }
    
    public void setWorldX(int x) {
        world_x = x;
    }
    
    public void setWorldY(int y) {
        world_y = y;
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
        return new int[]{(int)MiscMath.round(world_x, 1), (int)MiscMath.round(world_y, 1)};
    }
    
    public int[] getOnscreenCoords() {
        int[] wc = getWorldCoordinates();
        int[] osc = MiscMath.getOnscreenCoords(wc[0], wc[1]);
        //osc[0] = (int)MiscMath.round(osc[0], GUI.getSceneCanvas().getZoom());
        //osc[1] = (int)MiscMath.round(osc[1], GUI.getSceneCanvas().getZoom());
        return osc;
    }
    
    public int getOnscreenWidth() {
        int[] dims = getDimensions();
        return (int)(dims[0]*GUI.getSceneCanvas().getZoom());
    }
    
    public int getOnscreenHeight() {
        int[] dims = getDimensions();
        return (int)(dims[1]*GUI.getSceneCanvas().getZoom());
    }
    
    public int[] getDimensions() {
        return new int[]{(int)MiscMath.round(world_w, 1), (int)MiscMath.round(world_h, 1)};
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
            
            bw.write("/so");
            
        } catch (IOException ex) {
            ex.printStackTrace();
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
        int[] osc = getOnscreenCoords();
        draw(osc[0], osc[1], (int)GUI.getSceneCanvas().getZoom(), g);
    }
    
    public void draw(int x, int y, int z, Graphics g) {
        BufferedImage texture = null;
        if (Assets.OBJECT_TEXTURE_NAMES.contains(this.texture)) {
            texture = Assets.OBJECT_TEXTURES.get(Assets.OBJECT_TEXTURE_NAMES.indexOf(this.texture));
        }
        
        SceneCanvas canvas = GUI.getSceneCanvas();
        
        int[] osc = new int[]{x, y};
        int w = getDimensions()[0]*z;
        int h = getDimensions()[1]*z;
        
        if (isHitbox()) {
            g.setColor(new Color(50, 50, 100, 100));
            g.fillRect(osc[0], osc[1], w, h);
        } else {
            if (texture != null && Assets.OBJECT_TEXTURE_NAMES.contains(this.texture)) {
                g.setColor(Color.white);
                g.drawImage(texture.getScaledInstance(w, h, Image.SCALE_FAST), osc[0], osc[1], null);
            } else {
                g.setColor(Color.red);
                g.drawRect(osc[0], osc[1], w, h);
                g.drawLine(osc[0], osc[1], (int)(osc[0])+w, (int)(osc[1])+h);
                g.setColor(Color.white);
                canvas.drawString(this.texture+".png", osc[0], osc[1], g);
            }
        }
        if (this.equals(canvas.getSelectedObject())) {
            g.setColor(Color.white);
            g.drawRect(osc[0]-1, osc[1]-1, w+2, h+2);
            g.setColor(Color.black);
            g.drawRect(osc[0]-2, osc[1]-2, w+4, h+4);
        }
    }
    
    public ArrayList<Animation> getAnimations() { return animations; }
    public ArrayList<Flow> getFlows() { return flows; }
    
    @Override
    public String toString() {
        return Project.getProject().containsGalleryObject(this) ? this.getType() : this.getName();
    }
    
}
