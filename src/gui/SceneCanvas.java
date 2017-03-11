package gui;

import java.awt.Color;
import project.Project;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import misc.Assets;
import misc.MiscMath;
import project.objects.SceneObject;

public class SceneCanvas extends JPanel {
    
    public static final int SELECT_TOOL = 0, CAMERA_TOOL = 1, MOVE_TOOL = 2, RESIZE_TOOL = 3;
    
    private int origin_x, origin_y, last_mouse_x, last_mouse_y;
    private SceneObject selected_object, active_edit_object;
    private int selected_tool = 1, zoom = 8;

    public SceneCanvas() {
        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mousePressed(MouseEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
        });
    }
    
    public void moveCamera(double x, double y) {
        origin_x += x; origin_y += y;
    }
    
    public void zoomCamera(int amount) {
        double w_x_before = ((last_mouse_x-origin_x)/zoom)+origin_x;
        double w_y_before = ((last_mouse_x-origin_y)/zoom)+origin_y;
        zoom+=amount;
        if (zoom < 1) {
            zoom = 1;
        }
        if (zoom > 12) {
            zoom = 12;
        }
        double w_x_after = ((last_mouse_x-origin_x)/zoom)+origin_x;
        double w_y_after = ((last_mouse_y-origin_y)/zoom)+origin_y;
        origin_x += (w_x_after-w_x_before)*zoom;
        origin_y += (w_y_after-w_y_before)*zoom;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, (int)getWidth(), (int)getHeight());
        if (current_level == null) return;
        double r1 = current_level.R1, g1 = current_level.G1, b1 = current_level.B1;
        double r2 = current_level.R2, g2 = current_level.G2, b2 = current_level.B2;
        int height = 10;
        int increments = (int)(getHeight()/height);
        double g_add = (g2-g1)/increments;
        double r_add = (r2-r1)/increments;
        double b_add = (b2-b1)/increments;
        for (int y = 0; y < 1+increments; y+=1) {
            r1+=r_add;g1+=g_add;b1+=b_add;
            if (r1 <= 255 && g1 <= 255 && b1 <= 255 && r1 >= 0 && g1 >= 0 && b1 >= 0) {
                g.setColor(new Color((int)r1, (int)g1, (int)b1));
            }
            g.fillRect(0, y*height, getWidth(), getHeight());
        
        for (SceneObject o: current_level.distant_objects) {
            if (MiscMath.rectanglesIntersect(o.getOnscreenCoordinates()[0], o.getOnscreenCoordinates()[1], 
                    o.getOnscreenWidth(), o.getOnscreenHeight(), 
                    0, 0, (int)getWidth(), (int)getHeight()) || o.getOnscreenHeight() > getHeight() || o.getOnscreenWidth() > getWidth()) {
                o.draw(g);
            }
        }
        for (SceneObject o: current_level.bg_objects) {
            if (MiscMath.rectanglesIntersect(o.getOnscreenCoordinates()[0], o.getOnscreenCoordinates()[1], 
                    o.getOnscreenWidth(), o.getOnscreenHeight(), 
                    0, 0, (int)getWidth(), (int)getHeight()) || o.getOnscreenHeight() > getHeight() || o.getOnscreenWidth() > getWidth()) {
                o.draw(g);
            }
        }
        for (SceneObject o: current_level.mid_objects) {
            if (MiscMath.rectanglesIntersect(o.getOnscreenCoordinates()[0], o.getOnscreenCoordinates()[1], 
                    o.getOnscreenWidth(), o.getOnscreenHeight(), 
                    0, 0, (int)getWidth(), (int)getHeight()) || o.getOnscreenHeight() > getHeight() || o.getOnscreenWidth() > getWidth()) {
                o.draw(g);
            }
        }
        for (SceneObject o: current_level.fg_objects) {
            if (MiscMath.rectanglesIntersect(o.getOnscreenCoordinates()[0], o.getOnscreenCoordinates()[1], 
                    o.getOnscreenWidth(), o.getOnscreenHeight(), 
                    0, 0, (int)getWidth(), (int)getHeight()) || o.getOnscreenHeight() > getHeight() || o.getOnscreenWidth() > getWidth()) {
                o.draw(g);
            }
        }
        g.setColor(new Color(current_level.R3, current_level.G3, current_level.B3, current_level.lighting_intensity));
        g.fillRect(0, 0, (int)getWidth(), (int)getHeight());
        
        g.setColor(Color.green);
        g.drawRect((int)origin_x, (int)origin_y, (int)current_level.width*ZOOM, (int)current_level.Level.this.height*ZOOM);
        g.setColor(Color.red);
        g.drawLine((int)Project.origin_x, 0, (int)Project.origin_x, 100000);
        g.setColor(Color.yellow);
        g.drawLine(0, (int)Project.origin_y, 100000, (int)Project.origin_y);
        g.setColor(Color.cyan);
        g.drawLine((int)(current_level.player_spawn[0]*ZOOM)+(int)origin_x-3, (int)(current_level.player_spawn[1]*ZOOM)+(int)origin_y-3, 
                (int)(current_level.player_spawn[0]*ZOOM)+(int)origin_x+3, (int)(current_level.player_spawn[1]*ZOOM)+(int)origin_y+3);
        g.drawLine((int)(current_level.player_spawn[0]*ZOOM)+(int)origin_x+3, (int)(current_level.player_spawn[1]*ZOOM)+(int)origin_y-3, 
                (int)(current_level.player_spawn[0]*ZOOM)+(int)origin_x-3, (int)(current_level.player_spawn[1]*ZOOM)+(int)origin_y+3);
        //cam coords
        g.fillRect((int)(current_level.camera_spawn[0]*ZOOM)+(int)origin_x-3, (int)(current_level.camera_spawn[1]*ZOOM)+(int)origin_y-3, 
                6, 6);
        
        g.setColor(Color.white);
        drawString("Mouse: "+(int)((last_mouse_x-origin_x)/Project.ZOOM)+", "+(int)((LAST_MOUSE_Y-origin_y)/Project.ZOOM), 8, (int)getHeight()-10, g);
        if (SELECTED_TOOL == MOVE_TOOL) {
            drawString("Arrow keys: precision movement", 8, (int)getHeight()-30, g);
        }
        if (SELECTED_TOOL == RESIZE_TOOL) {
            drawString("Arrow keys: precision resizing", 8, (int)getHeight()-30, g);
        }
        if (SELECTED_TOOL == CAMERA_TOOL) {
            drawString("Press C to move camera to origin", 8, (int)getHeight()-30, g);
            drawString("Press X to reset camera", 8, (int)getHeight()-50, g);
        }
    }  
    
    public static void drawString(String str, int x, int y, Graphics g) {
        g.setColor(Color.gray.darker());
        g.drawString(str, x+1, y+1);
        g.setColor(Color.white);
        g.drawString(str, x, y);
    }
    
}