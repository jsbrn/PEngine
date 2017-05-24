package threads;

import gui.GUI;
import project.objects.components.Animation;

public class PreviewThread extends Thread {
    
    long last_time = 0;
    boolean stop, paused;
    
    Animation anim, newest_anim;
    
    int index = 0;
    int zoom = 1;
    int duration = 0;
    
    public PreviewThread() {
        last_time = System.currentTimeMillis();
        index = 0;
        stop = false;
        paused = true;
    }

    public void setPaused(boolean p) {
        paused = p;
        System.out.println("Setting PreviewThread paused state to "+p);
    }
    
    public void setAnimation(Animation a) {
        newest_anim = a;
        anim = new Animation();
        anim.getWidths().addAll(newest_anim.getWidths());
        anim.getHeights().addAll(newest_anim.getHeights());
        anim.setSpriteSheet(newest_anim.getSpriteSheet());
        reset();
    }
    
    public void reset() {
        index = 0;
        last_time = System.currentTimeMillis();
        if (anim.getWidths().isEmpty() == false) {
            duration = anim.getWidths().get(index);
        } else {
            duration = 0;
        }
    }
    
    public void setZoom(int zoom) {
        this.zoom = zoom;
    }
    
    /* CODE TO SET THE LABEL ICON TO THE IMAGE
     * int asset_index = Scene.ASSET_NAMES.indexOf(anim.TEXTURE_NAMES.get(index));
        System.out.println("Asset index: "+asset_index);
        if (asset_index > -1 && asset_index < Scene.ASSET_NAMES.size()) {
            BufferedImage img = Scene.ASSETS.get(asset_index);
            int width = img.getWidth()*zoom;
            int height = img.getHeight()*zoom;
            ImageIcon icon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_FAST));
            preview_pane.setIcon(icon);
        } else {
            preview_pane.setIcon(new ImageIcon());
            preview_pane.setText(anim.TEXTURE_NAMES.get(index));
        }]
        * 
        * 
        * 
        * 
        * 
        * 
        * 
        * 
        * 
        * if (asset_index > -1 && asset_index < Scene.ASSET_NAMES.size()) {
                            setIcon(Scene.ASSETS.get(asset_index).getSubimage(x, 0, anim.WIDTHS.get(index), anim.HEIGHTS.get(index)));
                        } else {
                            preview_pane.setIcon(new ImageIcon());
                        }
     */
    
    
    public void run() {
        while (true) {
            System.out.print(""); //for some reason if it does not do this, the thread simply does not work
            if (!paused) {
                System.out.println("Index: "+index+", duration: "+duration);
                if (System.currentTimeMillis() >= last_time+duration) {
                    if (index >= anim.getWidths().size()) {
                        reset();
                    } else { 
                        duration = anim.getFrameDuration();
                        last_time = System.currentTimeMillis();
                        index++;
                        if (index >= anim.getWidths().size()) {
                            reset();
                        }
                        GUI.animationFrameChooser.setSelectedIndex(index);
                    }
                }
            }
        }
    }
    
}
