package gui;

import java.awt.Color;
import java.awt.GradientPaint;
import project.Project;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import misc.Assets;
import project.objects.components.Animation;

public class AnimationCanvas extends JPanel {
    
    Animation animation;
    int frame = 0, zoom = 3;
    BufferedImage img;
    
    public AnimationCanvas() {

    }
    
    public void setAnimation(Animation a) {
        animation = a;
    }
    
    public void nextFrame() {
        if (animation == null) return;
        frame++;
        if (frame >= animation.getWidths().size()) frame = 0;
        int img_index = Assets.ANIMATION_TEXTURE_NAMES.indexOf(animation.getSpriteSheet());
        if (img_index < 0 || img_index >= Assets.ANIMATION_TEXTURES.size()) return;
        img = Assets.ANIMATION_TEXTURES.get(img_index);
        if (img != null) {
            img = img.getSubimage(getCropXOffset(), 0, animation.getWidths().get(frame), animation.getHeights().get(frame));
            BufferedImage resized = new BufferedImage(img.getWidth()*zoom, img.getHeight()*zoom, img.getType());
            Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            g.drawImage(img, 0, 0, img.getWidth()*zoom, img.getHeight()*zoom, 0, 0, img.getWidth(),
                img.getHeight(), null);
            g.dispose();
            img = resized;
        }
    }
    
    public void addZoom(int z) {
        setZoom(zoom+z);
    }
    
    public void setZoom(int z) {
        zoom = zoom + z;
        zoom = zoom < 0 ? 1 : (zoom > 8 ? 8 : zoom);
        reset();
    }
    
    public void reset() {
        frame = -1; nextFrame();
    }
    
    private int getCropXOffset() {
        if (animation == null) return 0;
        int x = 0;
        for (int i = 0; i < animation.getWidths().size(); i++) {
            if (i == frame) break;
            int w = animation.getWidths().get(0);
            x += w;
        }
        return x;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
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
        if (img == null) return;
        g.drawImage(img, (getWidth() - img.getWidth())/2, 
                (getHeight() - img.getHeight())/2, null);
        
    }  
}