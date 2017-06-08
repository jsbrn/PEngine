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
    
    public void setLastMousePosition(int x, int y) { last_mouse_x = x; last_mouse_y = y; }
    
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
    
    public int[] getOnscreenCoords(double world_x, double world_y) {
        world_x = MiscMath.round(world_x, 1);
        world_y = MiscMath.round(world_y, 1);
        return new int[]{(int) MiscMath.round((world_x - getCameraX()) * 32, 1) + (getWidth() / 2),
                (int) MiscMath.round((world_y - getCameraY()) * 32, 1) + (getHeight() / 2)};
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        //draw gradient background
        Color top_color = new Color(0, 76, 150), bttm_color = new Color(0, 25, 100);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp1 = new GradientPaint(0, 0, top_color, 0, getHeight(), bttm_color, true);
        g2d.setPaint(gp1);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(top_color.brighter());
        
        //draw grid
        int w = (int)(getWidth() / 32);
        for (int i = -w; i < getWidth(); i++) {
            int[] osc = getOnscreenCoords(i+camera_x, i+camera_y);            
            if (!(osc[0] < 0 || osc[0] > getWidth())) g.drawLine(osc[0], 0, osc[0], Integer.MAX_VALUE);
            if (!(osc[1] < 0 || osc[1] > getHeight())) g.drawLine(0, osc[1], Integer.MAX_VALUE, osc[1]);
        }
        
        if (selected_flow == null) return;
        
        String[] text = new String[]{"Input", "Output", "Yes", "No"};
        
        for (int i = 0; i < selected_flow.blockCount(); i++) {
            Block b = selected_flow.getBlock(i);
            //b.draw(g);
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
            //g.drawLine(ORIGIN_X + LAST_MOUSE_CLICK_X, ORIGIN_Y + LAST_MOUSE_CLICK_Y, LAST_MOUSE_X, LAST_MOUSE_Y);
        }*/
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
        grabFocus();
        if (SwingUtilities.isLeftMouseButton(e)) {
        } else if (SwingUtilities.isRightMouseButton(e)) {
            moveCamera((last_mouse_x-e.getX()), (last_mouse_y-e.getY()));
        }
        repaint();            
        setLastMousePosition(e.getX(), e.getY());
        System.out.println("Logic cam: "+camera_x+", "+camera_y);
    }
    
    public void handleMouseClick(MouseEvent e) {
        selected_block = getBlock(e.getX(), e.getY());
        repaint();
        grabFocus();
    }
    
}