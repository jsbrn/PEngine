package gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import project.objects.components.Block;
import project.objects.components.Flow;
import misc.MiscMath;

public class FlowCanvas extends JPanel {

    private double camera_x, camera_y, last_mouse_x, last_mouse_y;
    private Block selected_block; private Flow selected_flow;
    private int selected_node, last_mouse_click_x, last_mouse_click_y;
    
    public void setLastMousePosition(int x, int y) { last_mouse_x = x; last_mouse_y = y; }
    
    public FlowCanvas() {
        selected_node = -1;
    }
    
    public void setSelectedBlock(Block o) {
        selected_block = o;
    }
    
    public void setSelectedFlow(Flow f) { selected_flow = f; }
    public Block getSelectedBlock() { return selected_block; }
    
    public double getCameraX() { return camera_x; }
    public double getCameraY() { return camera_y; }
    
    public void resetCamera() {
        camera_x = 0;
        camera_y = 0;
    }
    
    public void moveCamera(double x, double y) {
        camera_x += x; camera_y += y;
        //camera_x = camera_x < 0 ? 0 : camera_x; camera_y = camera_y < 0 ? 0 : camera_y;
    }
    
    public void setCamera(int x, int y) {
        camera_x = x; camera_y = y;
    }
    
    public Block getBlock(int x, int y) {
        if (selected_flow == null) return null;
        for (int i = 0; i != selected_flow.blockCount(); i++) {
            Block b = selected_flow.getBlock(i);
            if (MiscMath.pointIntersects(x, y, b.getRenderCoords()[0]-20, b.getRenderCoords()[1]-20, 
                    b.dimensions()[0]+40, b.dimensions()[1]+40)) {
                return b;
            }
        }
        return null;
    }
    
    public double[] getWorldCoords(int onscreen_x, int onscreen_y) {
        return new double[]{((onscreen_x - (getWidth() / 2))) + getCameraX(),
                ((onscreen_y - (getHeight() / 2))) + getCameraY()};
    }
    
    public int[] getOnscreenCoords(double world_x, double world_y) {
        world_x = MiscMath.round(world_x, 1);
        world_y = MiscMath.round(world_y, 1);
        return new int[]{(int) MiscMath.round((world_x - getCameraX()), 1) + (getWidth() / 2),
                (int) MiscMath.round((world_y - getCameraY()), 1) + (getHeight() / 2)};
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        //draw gradient background
        Color top_color = new Color(25, 75, 120), bttm_color = new Color(10, 85, 140);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp1 = new GradientPaint(0, 0, top_color, 0, getHeight(), bttm_color, true);
        g2d.setPaint(gp1);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(top_color.brighter());
        
        int[] oosc = getOnscreenCoords(0, 0);
        
        //draw grid
        int w = (int)(getWidth());
        for (int i = -w; i <= w; i++) {
            g.setColor(i == 0 ? Color.white : top_color.brighter());
            int[] osc = new int[]{oosc[0] + (i*32), oosc[1] + (i*32)};            
            if (!(osc[0] < 0 || osc[0] > getWidth())) g.drawLine(osc[0], 0, osc[0], Integer.MAX_VALUE);
            if (!(osc[1] < 0 || osc[1] > getHeight())) g.drawLine(0, osc[1], Integer.MAX_VALUE, osc[1]);
        }
        
        double[] wc = getWorldCoords((int)last_mouse_x, (int)last_mouse_y);
        drawString("Mouse: "+wc[0]+", "+wc[1], 10, 50, g);
        drawString("Camera: "+camera_x+", "+camera_y, 10, 70, g);
        
        g.setColor(Color.white);
        g.drawRect(oosc[0]-2, oosc[1]-2, 4, 4);
        
        if (selected_flow == null) return;
        
        String[] text = new String[]{"Input", "Output", "Yes", "No"};
        
        for (int i = 0; i < selected_flow.blockCount(); i++) {
            Block b = selected_flow.getBlock(i);
            b.draw(g);
            int node = b.getNode(last_mouse_x, last_mouse_y);
            String t = node < 0 ? "" : (node < text.length ? text[node] : b.getParametre(node-Block.NODE_COUNT)[0]
                    +" ("+Block.TYPE_NAMES[(int)b.getParametre(node-Block.NODE_COUNT)[1]]+")");
            g.setColor(new Color(0, 0, 0, 100));
            int[] rect = new int[]{(int)last_mouse_x + 20, (int)last_mouse_y, t.length() > 0 ? t.length()*6 + 10 : 0, 20};
            g.fillRect(rect[0], rect[1], rect[2], rect[3]);
            g.setColor(Color.white);
            g.drawString(t, rect[0] + 5, rect[1] + 15);
        }
            
        //draw the line
        /*if (selected_block != null && selected_node > -1) {
            g.setColor(new Color(0, 0, 0, 100));
            
            g.drawLine(ORIGIN_X + LAST_MOUSE_CLICK_X, ORIGIN_Y + LAST_MOUSE_CLICK_Y, LAST_MOUSE_X, LAST_MOUSE_Y);
        }*/
        
    }  
    
    public void drawString(String str, int x, int y, Graphics g) {
        g.setColor(Color.gray.darker());
        g.drawString(str, x+1, y+1);
        g.setColor(Color.white);
        g.drawString(str, x, y);
    }
    
    public void setFlow(Flow f) {
        selected_flow = f;
        if (f == null) {
            selected_block = null;
            selected_node = -1;
        }
    }
    
    /**
     * EVENT HANDLERS
     */
    
    public void handleMouseMovement(MouseEvent e) {
        repaint();
        last_mouse_x = e.getX();
        last_mouse_y = e.getY();
    }
    
    public void handleMouseDrag(MouseEvent e) {
        if (selected_flow == null) return;
        grabFocus();
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (selected_block != null) {
                int[] osc = selected_block.getRenderCoords();
                int[] osd = new int[]{osc[0], osc[1], selected_block.dimensions()[0], selected_block.dimensions()[1]};
                double move_x = (e.getX()-last_mouse_x);
                double move_y = (e.getY()-last_mouse_y);
                if (MiscMath.pointIntersects(last_mouse_x, last_mouse_y, osd[0], osd[1], osd[2], osd[3])) {
                    selected_block.move(move_x, move_y);
                }
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            moveCamera((last_mouse_x-e.getX()), (last_mouse_y-e.getY()));
        }
        repaint();            
        setLastMousePosition(e.getX(), e.getY());
    }
    
    public void handleMouseClick(MouseEvent e) {
        /*selected_block = getBlock(e.getX(), e.getY());
        if (selected_block == null) {
            selected_node = -1;
        } else {
            selected_node = selected_block.getNode(e.getX(), e.getY());
        }
        
        System.out.println("Selected block "+selected_block+", node "+selected_node)*/
        
        if (selected_flow == null) return;
        Block b = getBlock(e.getX(), e.getY());
        int d = b == null ? -1 : b.getNode(e.getX(), e.getY());
        
        if (b != null) {
            if (d > -1) {
                if (selected_node < 0) {
                    //do not allow the "from" to be param or IN
                    //this is handled in block.connectTo but I don't want the editor to allow
                    //you to start a connection from these nodes
                    if (d < Block.NODE_COUNT && d != Block.IN) selected_node = d;
                } else {
                    int to = d, from = selected_node;
                    boolean accept_connection = selected_block.connectTo(b, to, from);
                    System.out.println("Connection: ["+selected_block.getType()+", "+from+"] <-> "
                                +"["+b.getType()+", "+to+"]"+(accept_connection ? " accepted" : " rejected"));
                    if (accept_connection) {
                        selected_block = null;
                        selected_node = -1;
                    }
                }
            } else {
                selected_block = b;
                selected_node = -1;
            }
            selected_block = b;
        } else {
            selected_block = null;
            selected_node = -1;
        }
        
        last_mouse_click_x = e.getX();
        last_mouse_click_y = e.getY();
        
        repaint();
        grabFocus();
    }
    
}