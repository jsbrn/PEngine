package gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import misc.MiscMath;
import project.Level;
import project.Project;
import project.objects.SceneObject;

public class SceneCanvas extends JPanel {

    private double camera_x, camera_y, last_mouse_x, last_mouse_y;
    private SceneObject selected_object, active_object;
    private int zoom = 3, skip = 0;
    
    private boolean show_grid = false;
    
    public void setLastMousePosition(int x, int y) { last_mouse_x = x; last_mouse_y = y; }
    
    public void setShowGrid(boolean b) {
        show_grid = b;
    }
    
    public void setSelectedObject(SceneObject o) {
        selected_object = o;
    }
    
    public SceneObject getSelectedObject() { return selected_object; }
    
    public void setActiveObject(SceneObject o) {
        active_object = o;
    }
    
    public SceneObject getActiveObject() { return active_object; }
    
    public double getCameraX() { return camera_x; }
    public double getCameraY() { return camera_y; }
    
    public void resetCamera() {
        camera_x = 0;
        camera_y = 0;
        zoom = 3;
    }
    
    public void moveCamera(double x, double y) {
        camera_x += x; camera_y += y;
        //camera_x = camera_x < 0 ? 0 : camera_x; camera_y = camera_y < 0 ? 0 : camera_y;
    }
    
    public void setCamera(int x, int y) {
        camera_x = x; camera_y = y;
    }
    
    public void setZoom(int z) {
        zoom = 0; addZoom(z);
    }
    
    private void addZoom(int amount) { zoom += amount; zoom = zoom > 8 ? 8 : (zoom < 1 ? 1 : zoom); }
    
    public void zoomCamera(int amount) {
        double[] wc_old = MiscMath.getWorldCoords((int)last_mouse_x, (int)last_mouse_y);
        addZoom(amount);
        double[] wc_new = MiscMath.getWorldCoords((int)last_mouse_x, (int)last_mouse_y);
        camera_x += wc_old[0] - wc_new[0];
        camera_y += wc_old[1] - wc_new[1];
    }
    
    public double getZoom() { return zoom; }
    
    /*
     * DRAWING THE COMPONENT
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, (int)getWidth(), (int)getHeight());
        
        if (Project.getProject() == null) return;
        
        Level current_level = Project.getProject().getCurrentLevel();
        
        if (current_level == null) return;
        
        //draw gradient background
        Color top_color = current_level.getTopBGColor(), bttm_color = current_level.getBottomBGColor();
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp1 = new GradientPaint(0, 0, top_color, 0, getHeight(), bttm_color, true);
        g2d.setPaint(gp1);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.DARK_GRAY);
        //draw grid
        int w = (int)(getWidth() / getZoom());
        for (int i = -w; i < getWidth() && show_grid && getZoom() >= 3; i++) {
            int[] osc = MiscMath.getOnscreenCoords(i+camera_x, i+camera_y);            
            if (!(osc[0] < 0 || osc[0] > getWidth())) g.drawLine(osc[0], 0, osc[0], Integer.MAX_VALUE);
            if (!(osc[1] < 0 || osc[1] > getHeight())) g.drawLine(0, osc[1], Integer.MAX_VALUE, osc[1]);
        }
        
        //draw all objects
        for (int layer = Level.DISTANT_LAYER; layer <= Level.FOREGROUND_LAYER; layer++) {
            for (SceneObject o: current_level.getObjects(layer)) {
                if (MiscMath.rectanglesIntersect(o.getOnscreenCoords()[0], o.getOnscreenCoords()[1], 
                        o.getOnscreenWidth(), o.getOnscreenHeight(), 
                        0, 0, (int)getWidth(), (int)getHeight()) || o.getOnscreenHeight() > getHeight() || o.getOnscreenWidth() > getWidth()) {
                    o.draw(g);
                }
            }
        }
        
        //draw ambient light (gotta be a better way to do this)
        Color light_color = current_level.getLightingColor();
        int[] light = new int[]{light_color.getRed(), light_color.getGreen(), light_color.getBlue()};
        g.setColor(new Color((int)MiscMath.clamp(light[0], 0, 255), 
                (int)MiscMath.clamp(light[1], 0, 255), (int)MiscMath.clamp(light[2], 0, 255), 
                (int)MiscMath.clamp(current_level.getLightIntensity()*255, 0, 255)));
        g.fillRect(0, 0, (int)getWidth(), (int)getHeight());
        
        int[] origin = MiscMath.getOnscreenCoords(0, 0);
        g.setColor(Color.green);
        int[] bounds = current_level.bounds();
        int[] osbc = MiscMath.getOnscreenCoords(bounds[0], bounds[1]);
        g.drawRect(osbc[0], osbc[1], (bounds[2]-bounds[0])*zoom, (bounds[3]-bounds[1])*zoom);
        g.setColor(Color.red);
        g.drawLine(origin[0], 0, origin[0], Integer.MAX_VALUE);
        g.setColor(Color.yellow);
        g.drawLine(0, origin[1], Integer.MAX_VALUE, origin[1]);
        g.setColor(Color.cyan);
        if (current_level.allowPlayer()) {
            g.drawLine((int)(current_level.playerSpawn()[0]*zoom)+(int)origin[0]-3, (int)(current_level.playerSpawn()[1]*zoom)+(int)origin[1]-3, 
                    (int)(current_level.playerSpawn()[0]*zoom)+(int)origin[0]+3, (int)(current_level.playerSpawn()[1]*zoom)+(int)origin[1]+3);
            g.drawLine((int)(current_level.playerSpawn()[0]*zoom)+(int)origin[0]+3, (int)(current_level.playerSpawn()[1]*zoom)+(int)origin[1]-3, 
                    (int)(current_level.playerSpawn()[0]*zoom)+(int)origin[0]-3, (int)(current_level.playerSpawn()[1]*zoom)+(int)origin[1]+3);
        }
        //cam coords
        g.fillRect((int)(current_level.cameraSpawn()[0]*zoom)+(int)origin[0]-3, (int)(current_level.cameraSpawn()[1]*zoom)+(int)origin[1]-3, 
                6, 6);

        //draw the info text in the corner
        ArrayList<String> information = new ArrayList<String>();
        information.add("Mouse: "+(int)((last_mouse_x-origin[0])/zoom)+", "+(int)((last_mouse_y-origin[1])/zoom));
        information.add("Zoom: "+getZoom());
        for (int i = 0; i < information.size(); i++) {
            drawString(information.get(i), 10, (int)getHeight() + 5 - ((information.size() - i) * 16), g);
        }
        
        //draw selected object highlight
        if (selected_object != null) {
            int osc[] = selected_object.getOnscreenCoords();
            int width = selected_object.getOnscreenWidth();
            int height = selected_object.getOnscreenHeight();
            g.setColor(Color.white);
            g.drawRect(osc[0]-1, osc[1]-1, width+2, height+2);
            g.setColor(Color.black);
            g.drawRect(osc[0]-2, osc[1]-2, width+4, height+4);
        }
        
    }
    
    public static void drawString(String str, int x, int y, Graphics g) {
        g.setColor(Color.gray.darker());
        g.drawString(str, x+1, y+1);
        g.setColor(Color.white);
        g.drawString(str, x, y);
    }
    
    /**
     * Event Handlers
     */
    
    public void handleMouseMovement(MouseEvent e) {
        repaint();
        if (MiscMath.distanceBetween(e.getX(), e.getY(), last_mouse_x, last_mouse_y) > 4) {
            skip = 0;
        }
        last_mouse_x = e.getX();
        last_mouse_y = e.getY();
    }
    
    public void handleMouseDrag(MouseEvent e) {
        grabFocus();
        if (SwingUtilities.isLeftMouseButton(e)) {
            GUI.refreshObjectProperties();
            if (selected_object != null) {
                int[] osc = selected_object.getOnscreenCoords();
                int[] osd = new int[]{osc[0], osc[1], selected_object.getOnscreenWidth(), selected_object.getOnscreenHeight()};
                int resize = (int)(osd[2] <= getZoom() * 2 ? getZoom() : getZoom()*2);
                double move_x = (e.getX()-last_mouse_x) / getZoom();
                double move_y = (e.getY()-last_mouse_y) / getZoom();
                if (MiscMath.pointIntersects(last_mouse_x, last_mouse_y, osd[0]+osd[2]-resize, 
                        osd[1]+osd[3]-resize, resize, resize)) {
                    selected_object.resize(move_x, move_y);
                } else if (MiscMath.pointIntersects(last_mouse_x, last_mouse_y, osd[0], osd[1], osd[2], osd[3])) {
                    selected_object.move(move_x, move_y);
                }
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            moveCamera((last_mouse_x-e.getX()) / getZoom(), (last_mouse_y-e.getY()) / getZoom());
        }
        repaint();            
        setLastMousePosition(e.getX(), e.getY());
    }
    
    public void handleMouseClick(MouseEvent e) {
        selected_object = Project.getProject().getCurrentLevel().getObject(e.getX(), e.getY(), skip);
        skip++; if (selected_object == null) skip = 0;
        GUI.refreshObjectProperties();
        repaint();
        grabFocus();
    }
    
    public void handleMouseWheel(MouseWheelEvent e) {
        zoomCamera(-e.getWheelRotation());
        repaint();
        grabFocus();
    }
    
    public void handleKeyPress(KeyEvent e) {
        
    }
    
}