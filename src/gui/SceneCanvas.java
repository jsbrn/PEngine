package gui;

import java.awt.Color;
import project.Project;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JPanel;
import misc.MiscMath;
import project.Level;
import project.objects.SceneObject;

public class SceneCanvas extends JPanel {
    
    public static final int SELECT_TOOL = 0, CAMERA_TOOL = 1, MOVE_TOOL = 2, RESIZE_TOOL = 3;
    
    private int origin_x, origin_y, last_mouse_x, last_mouse_y;
    private SceneObject selected_object, active_object;
    private int selected_tool = 1, zoom = 8;
    
    public void setLastMousePosition(int x, int y) { last_mouse_x = x; last_mouse_y = y; }
    
    public int getSelectedTool() {
        return selected_tool;
    }
    
    public void setSelectedTool(int tool) { this.selected_tool = tool; }
    
    public void setSelectedObject(SceneObject o) {
        selected_object = o;
    }
    
    public SceneObject getSelectedObject() { return selected_object; }
    
    public void setActiveObject(SceneObject o) {
        active_object = o;
    }
    
    public SceneObject getActiveObject() { return active_object; }
    
    public int getOriginX() { return origin_x; }
    public int getOriginY() { return origin_y; }
    
    public void moveCamera(double x, double y) {
        origin_x += x; origin_y += y;
    }
    
    public void zoomCamera(int amount) {
        double w_x_before = ((last_mouse_x-origin_x)/zoom)+origin_x;
        double w_y_before = ((last_mouse_y-origin_y)/zoom)+origin_y;
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
    
    public double getZoom() { return zoom; }

    /*
     * DRAWING THE COMPONENT
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, (int)getWidth(), (int)getHeight());
        
        if (Project.getProject() == null) return;
        
        Level current_level = Project.getProject().getCurrentLevel();
        
        if (current_level == null) return;
        Color top_color = current_level.getTopBGColor(), bttm_color = current_level.getBottomBGColor();
        int top[] = new int[]{top_color.getRed(), top_color.getGreen(), top_color.getBlue()},
                bottom[] = new int[]{bttm_color.getRed(), bttm_color.getGreen(), bttm_color.getBlue()};
        int height = 10;
        int increments = (int)(getHeight()/height);
        double g_add = (top[1]-bottom[1])/increments;
        double r_add = (top[0]-bottom[0])/increments;
        double b_add = (top[2]-bottom[2])/increments;
        for (int y = 0; y < 1+increments; y+=1) {
            top[0]+=r_add;top[1]+=g_add;top[2]+=b_add;
            if (top[0] <= 255 && top[1] <= 255 && top[2] <= 255 && top[0] >= 0 && top[1] >= 0 && top[2] >= 0) {
                g.setColor(new Color((int)top[0], (int)top[1], (int)top[2]));
            }
            g.fillRect(0, y*height, getWidth(), getHeight());
        }
        
        //draw all objects
        for (int layer = 1; layer <= Level.FOREGROUND_OBJECTS; layer++) {
            for (SceneObject o: current_level.getObjects(layer)) {
                if (MiscMath.rectanglesIntersect(o.getOnscreenCoordinates()[0], o.getOnscreenCoordinates()[1], 
                        o.getOnscreenWidth(), o.getOnscreenHeight(), 
                        0, 0, (int)getWidth(), (int)getHeight()) || o.getOnscreenHeight() > getHeight() || o.getOnscreenWidth() > getWidth()) {
                    o.draw(g);
                }
            }
        }
        
        Color light_color = current_level.getLightingColor();
        int[] light = new int[]{light_color.getRed(), light_color.getGreen(), light_color.getBlue()};
        g.setColor(new Color(light[0], light[1], light[2], (float)current_level.getLightIntensity()));
        g.fillRect(0, 0, (int)getWidth(), (int)getHeight());
        
        g.setColor(Color.green);
        g.drawRect(origin_x, origin_y, (int)current_level.dimensions()[0]*zoom, current_level.dimensions()[1]*zoom);
        g.setColor(Color.red);
        g.drawLine(origin_x, 0, origin_x, Integer.MAX_VALUE);
        g.setColor(Color.yellow);
        g.drawLine(0, origin_y, Integer.MAX_VALUE, origin_y);
        g.setColor(Color.cyan);
        g.drawLine((int)(current_level.playerSpawn()[0]*zoom)+(int)origin_x-3, (int)(current_level.playerSpawn()[1]*zoom)+(int)origin_y-3, 
                (int)(current_level.playerSpawn()[0]*zoom)+(int)origin_x+3, (int)(current_level.playerSpawn()[1]*zoom)+(int)origin_y+3);
        g.drawLine((int)(current_level.playerSpawn()[0]*zoom)+(int)origin_x+3, (int)(current_level.playerSpawn()[1]*zoom)+(int)origin_y-3, 
                (int)(current_level.playerSpawn()[0]*zoom)+(int)origin_x-3, (int)(current_level.playerSpawn()[1]*zoom)+(int)origin_y+3);
        //cam coords
        g.fillRect((int)(current_level.cameraSpawn()[0]*zoom)+(int)origin_x-3, (int)(current_level.cameraSpawn()[1]*zoom)+(int)origin_y-3, 
                6, 6);
        
        g.setColor(Color.white);
        drawString("Mouse: "+(int)((last_mouse_x-origin_x)/zoom)+", "+(int)((last_mouse_y-origin_y)/zoom), 8, (int)getHeight()-10, g);
        if (selected_tool == MOVE_TOOL) {
            drawString("Arrow keys: precision movement", 8, (int)getHeight()-30, g);
        }
        if (selected_tool == RESIZE_TOOL) {
            drawString("Arrow keys: precision resizing", 8, (int)getHeight()-30, g);
        }
        if (selected_tool == CAMERA_TOOL) {
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
    /**
     * Event Handlers
     */
    
    public void handleMouseMovement(MouseEvent e) {
        repaint();
        last_mouse_x = e.getX();
        last_mouse_y = e.getY();
    }
    
    public void handleMouseDrag(MouseEvent e) {
        grabFocus();
        switch (selected_tool) {
            case CAMERA_TOOL:
                moveCamera(e.getX()-last_mouse_x, e.getY()-last_mouse_y);
                break;
            case MOVE_TOOL:
                if (selected_object == null) selected_object = Project.getProject().getCurrentLevel().getObject(e.getX(), e.getY());
                GUI.refreshObjectProperties();
                if (selected_object != null) {
                    double move_x = (e.getX()-last_mouse_x)/(double)zoom;
                    double move_y = (e.getY()-last_mouse_y)/(double)zoom;
                    selected_object.move(move_x, move_y);
                } else {
                    selected_object = Project.getProject().getCurrentLevel().getObject(e.getX(), e.getY());
                }   break;
            case RESIZE_TOOL:
                if (selected_object != null) {
                    if (selected_object.isHitbox()) {
                        double move_x = (e.getX()-last_mouse_x)/(double)zoom;
                        double move_y = (e.getY()-last_mouse_y)/(double)zoom;
                        selected_object.resize(move_x, move_y);
                    }
                } else {
                    double move_x = (e.getX()-last_mouse_x)/(double)zoom;
                    double move_y = (e.getY()-last_mouse_y)/(double)zoom;
                    Project.getProject().getCurrentLevel().resize(move_x, move_y);
                }   break;
            default:
                break;
        }
        repaint();            
        setLastMousePosition(e.getX(), e.getY());
    }
    
    public void handleMouseClick(MouseEvent e) {
        System.out.println("SceneCanvas: Mouse click at "+e.getX()+", "+e.getY());
        selected_object = Project.getProject().getCurrentLevel().getObject(e.getX(), e.getY());
        System.out.print(selected_object != null ? "Selected object: "+selected_object.getName()+"\n" : "");
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
        SceneObject object = getSelectedObject();
        

        if (object != null) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                Project.getProject().getCurrentLevel().removeObject(object);
            }
        }

        selected_tool = e.getKeyCode();
        /*GUI.selectButton.setEnabled(selected_tool == 1);
        GUI.cameraButton.setEnabled(selected_tool == 2);
        GUI.moveButton.setEnabled(selected_tool == 3);
        GUI.resizeButton.setEnabled(selected_tool == 4);*/

        //zoom if zoom key pressed
        zoomCamera(e.getKeyChar() == '=' ? 1 : (e.getKeyChar() == '-' ? -1 : 0));

        switch (selected_tool) {
            case CAMERA_TOOL:
                if (e.getKeyChar() == 'x') {
                    origin_x = 10;
                    origin_y = 10;
                    zoom = 8;
                } else if (e.getKeyChar() == 'c') {
                    origin_x = 10;
                    origin_y = 10;
                }   break;
            case MOVE_TOOL:
                if (selected_object != null) {
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        selected_object.move(1, 0);
                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        selected_object.move(-1, 0);
                    }
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        selected_object.move(0, -1);
                    }
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        selected_object.move(0, 1);
                    }
                }   break;
            case RESIZE_TOOL:
                if (selected_object != null) {
                    if (selected_object.isHitbox()) {
                        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                            selected_object.resize(1, 0);
                        }
                        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                            selected_object.resize(-1, 0);
                        }
                        if (e.getKeyCode() == KeyEvent.VK_UP) {
                            selected_object.resize(0, -1);
                        }
                        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                            selected_object.resize(0, 1);
                        }
                    }
                }   break;
            default:
                break;
        }
        repaint();
    }
    
}