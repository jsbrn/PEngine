package project.objects;

import gui.GUI;
import gui.SceneCanvas;
import project.objects.components.Animation;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
    private boolean gravity, collides, locked;
    
    public ArrayList<Animation> animations = new ArrayList<Animation>();
    private ArrayList<Flow> flows = new ArrayList<Flow>();
    
    boolean hitbox = false;
    
    public String texture;
    
    public SceneObject() {
        this.layer = Level.MIDDLE_OBJECTS;
        this.gravity = false;
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
        if (!hitbox) return;
        world_w += x; world_h += y;
        world_w = world_w < 2 ? 2 : world_w;
        world_h = world_h < 2 ? 2 : world_h;
    }
    
    public void setLayer(int l) { layer = l; }
    
    public void setWidth(int w) {
        world_w = w < 2 ? 2 : w;
    }
    
    public void setHeight(int h) {
        world_h = h < 2 ? 2 : h;
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
    
    public boolean gravity() { return gravity; }
    
    public boolean collides() { return collides; }
    
    public void setGravity(boolean g) { this.gravity = g; }
    
    public void setCollides(boolean c) { this.collides = c; }
    
    public int[] getWorldCoords() {
        return new int[]{(int)MiscMath.round(world_x, 1), (int)MiscMath.round(world_y, 1)};
    }
    
    public int[] getOnscreenCoords() {
        int[] wc = getWorldCoords();
        int[] osc = MiscMath.getOnscreenCoords(wc[0], wc[1]);
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
        Object o = Assets.get(texture);
        if (o != null) {
            BufferedImage img = (BufferedImage)o;
            return new int[]{img.getWidth(), img.getHeight()};
        }
        return new int[]{(int)MiscMath.round(world_w, 1), (int)MiscMath.round(world_h, 1)};
    }
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("so\n");
                bw.write("xy="+getWorldCoords()[0]+" "+getWorldCoords()[1]+"\n");
                bw.write("wh="+getDimensions()[0]+" "+getDimensions()[1]+"\n");
                bw.write("n="+name+"\n");
                bw.write("t="+type+"\n");
                bw.write("lk="+locked+"\n");
                bw.write("tx="+texture+"\n");
                bw.write("h="+hitbox+"\n");
                bw.write("g="+gravity+"\n");
                bw.write("c="+collides+"\n");
                bw.write("l="+layer+"\n");
                for (Flow f: flows) f.save(bw);
                for (Animation a: animations) a.save(bw);
            bw.write("/so\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean load(BufferedReader br) {
        System.out.println("Remember to load flows/blocks in SceneObject.");
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.equals("/so")) return true;
                
                if (line.indexOf("n=") == 0) name = line.substring(2);
                if (line.indexOf("t=") == 0) type = line.substring(2);
                if (line.indexOf("tx=") == 0) texture = line.substring(3);
                if (line.indexOf("lk=") == 0) locked = Boolean.parseBoolean(line.substring(3));
                if (line.indexOf("h=") == 0) hitbox = Boolean.parseBoolean(line.substring(2));
                if (line.indexOf("g=") == 0) gravity = Boolean.parseBoolean(line.substring(2));
                if (line.indexOf("c=") == 0) collides = Boolean.parseBoolean(line.substring(2));
                if (line.indexOf("l=") == 0) layer = Integer.parseInt(line.substring(2));
                
                if (line.indexOf("xy=") == 0) {
                    int[] coords = MiscMath.toIntArray(line.substring(3));
                    world_x = coords[0]; world_y = coords[1];
                    System.out.println("SceneObject "+name+" loaded at "+world_x+", "+world_y);
                }
                if (line.indexOf("wh=") == 0) {
                    int[] dims = MiscMath.toIntArray(line.substring(3));
                    world_w = dims[0]; world_h = dims[1];
                }
                
                if (line.equals("a")) {
                    Animation a = new Animation();
                    if (a.load(br)) animations.add(a);
                }
                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
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
        o.gravity = this.gravity;
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
        
        o.setWorldX(this.getWorldCoords()[0] + 5);
        o.setWorldY(this.getWorldCoords()[1] + 5);
        o.setWidth(this.getDimensions()[0]);
        o.setHeight(this.getDimensions()[1]);
        
    }
    
    public void newFlow() {
        Flow f = new Flow();
        f.setName("animation"+new Random().nextInt(100000));
        flows.add(f);
    }
    
    public void newAnimation() {
        Animation a = new Animation();
        a.setName("animation"+new Random().nextInt(100000));
        animations.add(a);
    }
    
    public void draw(Graphics g) {
        int[] osc = getOnscreenCoords();
        draw(osc[0], osc[1], (int)GUI.getSceneCanvas().getZoom(), g);
    }
    
    public void draw(int x, int y, int z, Graphics g) {
        
        Object asset = Assets.get(texture);
        BufferedImage img = (BufferedImage)asset;
        
        SceneCanvas canvas = GUI.getSceneCanvas();
        
        int[] osc = new int[]{x, y};
        int w = getDimensions()[0]*z;
        int h = getDimensions()[1]*z;
        
        if (isHitbox()) {
            g.setColor(new Color(50, 50, 100, 100));
            g.fillRect(osc[0], osc[1], w, h);
        } else {
            if (img != null) {
                g.setColor(Color.white);
                g.drawImage(img.getScaledInstance(w, h, Image.SCALE_FAST), osc[0], osc[1], null);
            } else {
                g.setColor(Color.red);
                g.drawRect(osc[0], osc[1], w, h);
                g.drawLine(osc[0], osc[1], (int)(osc[0])+w, (int)(osc[1])+h);
                g.setColor(Color.white);
                canvas.drawString(this.texture, osc[0], osc[1], g);
            }
        }
        //draw highlight if selected
        if (equals(canvas.getSelectedObject())) {
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
