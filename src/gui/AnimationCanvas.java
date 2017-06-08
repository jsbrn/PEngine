package gui;

import java.awt.Color;
import java.awt.GradientPaint;
import project.Project;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import misc.Assets;
import project.objects.components.Animation;

public class AnimationCanvas extends JPanel {
    
    private Animation animation;
    private int frame = 0, zoom = 3;
    private BufferedImage img;
    private AnimationThread thread;
    private boolean paused;
    private long last_time;
    
    public AnimationCanvas() {
        initThread();
        this.paused = false;
        this.last_time = System.currentTimeMillis();
    }
    
    public void setAnimation(Animation a) {
        animation = new Animation();
        a.copyTo(animation);
    }

    public Animation getAnimation() {
        return animation;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setLastTime(long last_time) {
        this.last_time = last_time;
    }

    public long getLastTime() {
        return last_time;
    }
    
    public void clearAnimation() {
        animation = null;
    }
    
    public void nextFrame() {
        if (animation == null) return;
        frame++;
        if (frame >= animation.frameCount()) frame = 0;
        Object asset = Assets.get(animation.getSpriteSheet());
        if (asset == null) return;
        BufferedImage temp_img = (BufferedImage)asset;
        int w = temp_img.getWidth() / animation.frameCount();
        img = temp_img.getSubimage(frame*w, 0, w, temp_img.getHeight());
    }
    
    public void addZoom(int z) {
        setZoom(zoom+z);
    }
    
    public void setZoom(int z) {
        zoom = zoom + z;
        zoom = zoom < 0 ? 1 : (zoom > 8 ? 8 : zoom);
        reset();
    }
    
    public void setFrame(int f) {
        frame = f < 0 ? 0 : f;
    }
    
    public void reset() {
        img = null;
        frame = -1; nextFrame();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Repainting! "+animation.getSpriteSheet()+" "+img);
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if (Project.getProject() == null) return;
        if (Project.getProject().getCurrentLevel() == null) return;
        
        Color top_color = Project.getProject().getCurrentLevel().getTopBGColor(), 
                bttm_color = Project.getProject().getCurrentLevel().getBottomBGColor();
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp1 = new GradientPaint(0, 0, top_color, 0, getHeight(), bttm_color, true);
        g2d.setPaint(gp1);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        if (animation == null) return;
        if (img != null) {
            System.out.println("Drawing image!");
            g.drawImage(img.getScaledInstance(img.getWidth()*zoom, 
                    img.getHeight()*zoom, Image.SCALE_SMOOTH), (getWidth() - img.getWidth()*zoom)/2, 
                    (getHeight() - img.getHeight()*zoom)/2, null);
        }
        
    }

    public void setPaused(boolean p) {
        paused = p;
    }
    
    private void initThread() {
        thread = new AnimationThread(this, new AnimationRunnable());
        thread.start();
    }
    
}

class AnimationThread extends Thread {
    
    private AnimationCanvas parent;
    
    public AnimationThread(AnimationCanvas parent, AnimationRunnable r) {
        super(r);
        this.parent = parent;
        r.setParent(this);
    }

    public AnimationCanvas getParent() {
        return parent;
    }
    
}

class AnimationRunnable implements Runnable {
    
    private AnimationThread parent;

    public void setParent(AnimationThread parent) {
        this.parent = parent;
    }
    
    @Override
    public void run() {
        while (true) {
            
            System.out.print("");
            
            if (parent.getParent().isPaused()) continue;
            if (parent.getParent().getAnimation() == null) continue;
            
            try {
                Thread.sleep(parent.getParent().getAnimation().getFrameDuration());
            } catch (InterruptedException ex) {
                Logger.getLogger(AnimationRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
                
            parent.getParent().nextFrame();
            parent.getParent().repaint();
            System.out.println(parent.getParent().getAnimation()+" -> nextFrame");
            
        }
    }
    
}