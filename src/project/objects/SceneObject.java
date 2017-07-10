package project.objects;

import gui.GUI;
import gui.SceneCanvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import misc.Assets;
import misc.MiscMath;
import project.Level;
import project.Project;
import project.objects.components.Animation;
import project.objects.components.Block;
import project.objects.components.Flow;

public class SceneObject {
    
    private double world_x, world_y, world_w, world_h;
    private String name, type, texture;
    private int layer;
    private boolean gravity, collides;
    private boolean[] locked; //texture, gravity, collides
    
    public ArrayList<Animation> animations;
    private ArrayList<Flow> flows;
    
    boolean hitbox = false;
    
    public SceneObject() {
        this.layer = Level.NORMAL_LAYER;
        this.gravity = false;
        this.type = "";
        this.collides = true;
        this.name = "";
        this.texture = "";
        this.world_w = 16;
        this.world_h = 16;
        this.animations = new ArrayList<Animation>();
        this.flows = new ArrayList<Flow>();
        this.locked = new boolean[]{false, false, false};
    }
    
    public void autoName(String prefix, Level l) {
        int i = 1;
        while (true) {
            String n = prefix+(i > 1 ? ""+i : "");
            if (l == null) if (!Project.getProject().containsGalleryObject(n)) { setType(n); break; }
            if (l != null) if (!l.containsObject(n)) { setName(n); break; }
            i++;
        }
    }
    
    public static boolean isValidName(String name) {
        if (name == null) return false;
        if (name.trim().length() == 0) return false;
        if (name.trim().equals("true") || name.trim().equals("false")
                || name.trim().toLowerCase().equals("player")) return false;
        return name.replaceAll("^[a-zA-Z_$][a-zA-Z_$0-9]*$", "").equals("");
    }
    
    public void setLocked(int index, boolean l) {
        locked[index] = l;
    }
    
    public boolean isLocked(int index) {
        return locked[index];
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
    
    public void save(BufferedWriter bw, boolean game) {
        try {
            bw.write("so\n");
                
                if (game) {
                    double game_x = getWorldCoords()[0] + ((double)getDimensions()[0]/2);
                    double game_y = getWorldCoords()[1] + ((double)getDimensions()[1]/2);
                    bw.write("xy="+game_x+" "+game_y+"\n");
                } else {
                    bw.write("xy="+getWorldCoords()[0]+" "+getWorldCoords()[1]+"\n");
                }
                bw.write("wh="+getDimensions()[0]+" "+getDimensions()[1]+"\n");
                bw.write("n="+name+"\n");
                bw.write("t="+type+"\n");
                bw.write("lk="+locked[0]+" "+locked[1]+" "+locked[2]+"\n");
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
                if (line.indexOf("lk=") == 0) locked = MiscMath.toBooleanArray(line.substring(3));
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
                
                if (line.equals("f")) {
                    Flow a = new Flow();
                    if (a.load(br)) flows.add(a);
                }
                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Copies all properties and values (except for the name and coordinates) to the specified object.
     * @param o The specified object.
     */
    public void copyTo(SceneObject o, boolean copy_name, boolean respect_locks) {
        if (!o.locked[0] || !respect_locks) o.texture = this.texture;
        o.type = this.type;
        if (copy_name) o.name = this.name;
        o.layer = this.layer;
        if (!o.locked[1] || !respect_locks) o.gravity = this.gravity;
        if (!o.locked[2] || !respect_locks) o.collides = this.collides;
        o.hitbox = this.hitbox;

        /*ArrayList<Animation> new_list = new ArrayList<Animation>();
        for (Animation a: o.animations) {
            Animation in_source = this.getAnimation(a.getName());
            if (in_source != null) {
                if (!a.isLocked()) in_source.copyTo(a);
            }
        }*/
        //DELETE any unlocked anims in O
        for (int i = o.animations.size() - 1; i > -1; i--) 
            if (!o.animations.get(i).isLocked() || !respect_locks) o.animations.remove(i);
        //copy over all anims in this obj that are not in O
        for (Animation a: animations) {
            if (o.getAnimation(a.getName()) == null) {
                Animation copy_over = new Animation();
                a.copyTo(copy_over, true);
                o.animations.add(copy_over);
            }
        }
        
        //DELETE any unlocked anims in O
        for (int i = o.flows.size() - 1; i > -1; i--) 
            if (!o.flows.get(i).isLocked() || !respect_locks) o.flows.remove(i);
        //copy over all anims in this obj that are not in O
        for (Flow a: flows) {
            if (o.getFlow(a.getName()) == null) {
                Flow copy_over = new Flow();
                a.copyTo(copy_over, true);
            }
        }
        
        o.setWidth(this.getDimensions()[0]);
        o.setHeight(this.getDimensions()[1]);
        
    }
    
    public Animation getAnimation(String name) {
        for (Animation a: animations) {
            if (a.getName().equals(name)) return a;
        }
        return null;
    }
    
    public Flow getFlow(String name) {
        for (Flow f: flows) {
            if (f.getName().equals(name)) return f;
        }
        return null;
    }
    
    public void newFlow() {
        Flow f = new Flow();
        f.setName("flow"+new Random().nextInt(100000));
        f.autoName(this);
        Block start = Block.create("s");
        start.setX(0); start.setY(0);
        start.getInput(0)[2] = "\"default\"";
        f.addBlock(start); //add a start block
        flows.add(f);
    }
    
    public void newAnimation() {
        Animation a = new Animation();
        a.autoName(this);
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
    }
    
    public ArrayList<Animation> getAnimations() { return animations; }
    public ArrayList<Flow> getFlows() { return flows; }
    
    @Override
    public String toString() {
        return Project.getProject().containsGalleryObject(this) ? this.getType() : this.getName();
    }
    
}
