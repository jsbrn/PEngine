package gui;

import java.awt.Color;
import scene.Scene;
import java.awt.Graphics;
import javax.swing.JPanel;
import misc.Block;
import misc.Flow;
import misc.MiscMath;

public class FlowCanvas extends JPanel {

    public static int ORIGIN_X = 0, ORIGIN_Y = 0, LAST_MOUSE_X = 0, LAST_MOUSE_Y = 0;
    public static Block SELECTED_BLOCK;
    public static int SELECTED_DOT = -1;
    public static int LAST_MOUSE_CLICK_X = 0, LAST_MOUSE_CLICK_Y = 0;
    
    public FlowCanvas() {

    }
    
    public static Block getBlock(int x, int y) {
        Flow f = Scene.ACTIVE_EDIT_OBJECT.FLOWS.get(GUI.flowChooser.getSelectedIndex());
        for (int i = 0; i != f.blockCount(); i++) {
            Block b = f.getBlock(i);
            if (MiscMath.pointIntersects(x, y, b.getCoords()[0]-20, b.getCoords()[1]-20, b.dimensions()[0]+40, b.dimensions()[1]+40)) {
                return b;
            }
        }
        return null;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        if (ORIGIN_X > 0) ORIGIN_X = 0; if (ORIGIN_Y > 0) ORIGIN_Y = 0;
        int width = (this.getWidth()+(20*5))-ORIGIN_X;
        int height = (this.getHeight()+(20*5))-ORIGIN_Y;
        for (int i = 0; i != width/20; i++) {
            for (int j = 0; j != height/20; j++) {
                g.drawRect((i*20)+ORIGIN_X, (j*20)+ORIGIN_Y, 2, 2);
            }
        }

        if (Scene.ACTIVE_EDIT_OBJECT == null) return;
        int flow_index = GUI.flowChooser.getSelectedIndex();
        if (flow_index > -1) {
            Flow f = Scene.ACTIVE_EDIT_OBJECT.FLOWS.get(flow_index);
            g.setColor(Color.black);
            for (int i = 0; i != f.blockCount(); i++) {
                Block b = f.getBlock(i);
                int b_width = b.dimensions()[0], b_height = b.dimensions()[1];
                int r_x = ORIGIN_X+b.getCoords()[0], r_y = ORIGIN_Y+b.getCoords()[1];
                g.setColor(new Color(0, 0, 0, 100));
                g.fillRect(r_x+3, r_y+3, b_width, b_height);
                g.setColor(Color.white);
                g.fillRect(r_x, r_y, b_width, b_height);
                g.setColor(Color.black);
                g.drawString(b.title(), r_x + 5, r_y + 15);
                String values = "Values: "; 
                for (int p = 0; p != b.paramCount(); p++) { 
                    String value = b.getParametre(p, 1)+": "+b.getParametre(p, 0);
                    g.drawString(value, r_x + 5, r_y + 30 + (20*p));
                }
                g.drawRect(r_x, r_y, b_width, b_height);
                if (b.equals(SELECTED_BLOCK)) {
                    g.setColor(Color.red);
                    g.drawRect(ORIGIN_X + b.getCoords()[0] - 21, ORIGIN_Y + b.getCoords()[1] - 21, b.dimensions()[0] + 42, 
                            b.dimensions()[1] + 42);
                }
                //draw the IN/OUT/YES/NO/OK dots
                if (b.dots()[0]) {
                    g.setColor(Color.gray);
                    g.fillRect(r_x+5, r_y-20, 20, 20);
                    g.setColor(Color.black);
                    g.drawRect(r_x+5, r_y-20, 20, 20);
                    g.setColor(Color.white);
                    g.drawString("In", r_x+11, r_y-5);
                }
                if (b.dots()[1]) {
                    g.setColor(Color.BLUE.darker());
                    g.fillRect(r_x+30, r_y+b_height, 20, 20);
                    g.setColor(Color.black);
                    g.drawRect(r_x+30, r_y+b_height, 20, 20);
                    g.setColor(Color.white);
                    g.drawString("Out", r_x+7+25, r_y+b_height + 15);
                    Block dest = f.getBlockByID(b.dotConns()[1]);
                    if (dest != null) {
                        g.setColor(Color.BLUE.darker());
                        g.drawLine(r_x+40, r_y+b_height+10, ORIGIN_X+dest.getCoords()[0]+15, ORIGIN_Y+dest.getCoords()[1]-10);
                    }
                }
                if (b.dots()[2]) {
                    g.setColor(Color.green.darker());
                    g.fillRect(r_x+b_width-25, r_y+b_height, 20, 20);
                    g.setColor(Color.black);
                    g.drawRect(r_x+b_width-25, r_y+b_height, 20, 20);
                    g.setColor(Color.white);
                    g.drawString("Yes", r_x+b_width-17-6, r_y+b_height + 15);
                    Block dest = f.getBlockByID(b.dotConns()[2]);
                    if (dest != null) {
                        g.setColor(Color.GREEN.darker());
                        g.drawLine(r_x+b_width-15, r_y+b_height+10, ORIGIN_X+dest.getCoords()[0]+15, ORIGIN_Y+dest.getCoords()[1]-10);
                    }
                }
                if (b.dots()[3]) {
                    g.setColor(Color.red.darker());
                    g.fillRect(r_x+b_width, r_y+b_height-25, 20, 20);
                    g.setColor(Color.black);
                    g.drawRect(r_x+b_width, r_y+b_height-25, 20, 20);
                    g.setColor(Color.white);
                    g.drawString("No", r_x+b_width+5, r_y+b_height-10);
                    Block dest = f.getBlockByID(b.dotConns()[3]);
                    if (dest != null) {
                        g.setColor(Color.red.darker());
                        g.drawLine(r_x+b_width+10, r_y+b_height-15, ORIGIN_X+dest.getCoords()[0]+15, ORIGIN_Y+dest.getCoords()[1]-10);
                    }
                }
                if (b.dots()[4]) {
                    g.setColor(Color.white);
                    g.fillRect(r_x+5, r_y+b_height, 20, 20);
                    g.setColor(Color.black);
                    g.drawRect(r_x+5, r_y+b_height, 20, 20);
                    g.setColor(Color.black);
                    g.drawString("OK", r_x+9, r_y+b_height + 15);
                    Block dest = f.getBlockByID(b.dotConns()[4]);
                    if (dest != null) {
                        g.setColor(Color.black);
                        g.drawLine(r_x+15, r_y+b_height+10, ORIGIN_X+dest.getCoords()[0]+15, ORIGIN_Y+dest.getCoords()[1]-10);
                    }
                }
                for (int p = 0; p != b.paramCount(); p++) {
                    g.setColor(Color.yellow.darker());
                    g.fillRect(r_x-20, r_y+(p*20)+5, 20, 20);
                    g.setColor(Color.black);
                    g.drawRect(r_x-20, r_y+5+(p*20), 20, 20);
                    g.setColor(Color.white);
                    g.drawString("P"+(p+1), r_x - 15, r_y + 20 + (p*20));
                    g.setColor(Color.yellow.darker());
                    Block pb = f.getBlockByID(b.getParametreConnection(p));
                    if (pb != null) g.drawLine(r_x-10, r_y+(p*20)+15, ORIGIN_X+pb.getCoords()[0]+40, ORIGIN_Y+pb.getCoords()[1]+pb.dimensions()[1]+10);
                    if (MiscMath.pointIntersects(LAST_MOUSE_X, LAST_MOUSE_Y, r_x-20, r_y+(p*20)+5, 20, 20)) {
                        g.setColor(new Color(0, 0, 0, 255));
                        String desc = b.getParametre(p, 1)+" ("+b.getParametre(p, 2)+")";
                        g.fillRect(LAST_MOUSE_X+10, LAST_MOUSE_Y-30, 15+desc.length()*5, 25);
                        g.setColor(Color.white);
                        g.drawString(desc, LAST_MOUSE_X+15, LAST_MOUSE_Y-15);
                    }
                }
                
            }
            
            //draw the line
            if (SELECTED_BLOCK != null && FlowCanvas.SELECTED_DOT > -1) {
                g.setColor(new Color(0, 0, 0, 100));
                g.drawLine(ORIGIN_X + LAST_MOUSE_CLICK_X, ORIGIN_Y + LAST_MOUSE_CLICK_Y, LAST_MOUSE_X, LAST_MOUSE_Y);
            }
            
        }
    }  
}