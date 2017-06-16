package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import project.objects.components.*;
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
        if (o == null) selected_node = -1;
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
        for (int i = selected_flow.blockCount()-1; i > -1; i--) {
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
        String hover_text = ""; int hover_width = 0;
        
        for (int i = 0; i < selected_flow.blockCount(); i++) {
            Block b = selected_flow.getBlock(i);
            b.draw(g);
        }
            
        //draw the line
        if (selected_block != null && selected_node > -1) {
            g.setColor(new Color(0, 0, 0, 100));
            int[] rc = selected_block.getRenderCoords();
            int[] n = selected_block.getNodeOffset(selected_node);
            int[] p1 = new int[]{rc[0]+n[0]+10, rc[1]+n[1]+10};
            
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g2.draw(new Line2D.Float(p1[0], p1[1], (int)last_mouse_x, (int)last_mouse_y));

        }
        
        if (selected_block != null) {
            int osc[] = selected_block.getRenderCoords();
            int width = selected_block.dimensions()[0];
            int height = selected_block.dimensions()[1];
            g.setColor(Color.white);
            g.drawRect(osc[0]-1, osc[1]-1, width+2, height+2);
            g.setColor(Color.black);
            g.drawRect(osc[0]-2, osc[1]-2, width+4, height+4);
        }
        
        g.setColor(new Color(0, 0, 0, 100));
        int[] rect = new int[]{(int)last_mouse_x + 20, 
            (int)last_mouse_y, hover_text.length() > 0 ? hover_width + 10 : 0, 20};
        g.fillRect(rect[0], rect[1], rect[2], rect[3]);
        g.setColor(Color.white);
        g.drawString(hover_text, rect[0] + 5, rect[1] + 15);
        
    }  
    
    public void drawString(String str, int x, int y, Graphics g) {
        g.setColor(Color.gray.darker());
        g.drawString(str, x+1, y+1);
        g.setColor(Color.white);
        g.drawString(str, x, y);
    }
    
    public Flow getFlow() {
        return selected_flow;
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
        
        if (selected_flow == null) return;
        Block b = getBlock(e.getX(), e.getY());
        int d = b == null ? -1 : b.getNodeIndex(e.getX(), e.getY());
        
        if (b != null) {
            if (d > -1) {
                if (selected_node < 0) {
                    //do not allow the "from" to be param or IN
                    //this is handled in block.connectTo but I don't want the editor to allow
                    //you to start a connection from these nodes
                    if (d < Block.NODE_COUNT && d != Block.NODE_IN) selected_node = d;
                } else {
                    int to = d, from = selected_node;
                    boolean accept_connection = selected_block.connectTo(b, to, from);
                    System.out.println("Connection: ["+selected_block.getType()+", "+from+"] <-> "
                                +"["+b.getType()+", "+to+"]"+(accept_connection ? " accepted" : " rejected"));
                    selected_block = null;
                    selected_node = -1;
                }
            } else {
                selected_block = b;
                selected_node = -1;
            }
            selected_block = b;
        } else {
            //disconnect the connection if you click off
            if (selected_block != null && selected_node > -1) selected_block.disconnect(selected_node);
            //reset
            selected_block = null;
            selected_node = -1;
        }
        
        last_mouse_click_x = e.getX();
        last_mouse_click_y = e.getY();
        
        repaint();
        grabFocus();
        GUI.refreshFlowOptions();
    }
    
}