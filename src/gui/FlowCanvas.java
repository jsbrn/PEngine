package gui;

import java.awt.Color;
import project.Project;
import java.awt.Graphics;
import javax.swing.JPanel;
import project.objects.components.Block;
import project.objects.components.Flow;
import misc.MiscMath;

public class FlowCanvas extends JPanel {

    private int origin_x = 0, origin_y = 0, last_mouse_x = 0, last_mouse_y = 0;
    private int selected_dot = -1;
    private int last_mouse_click_x = 0, last_mouse_click_y = 0;
    private Block selected_block;
    
    public FlowCanvas() {

    }
    
    public static Block getBlock(int x, int y) {
        Flow f = GUI.getSceneCanvas().getActiveObject().get(GUI.flowChooser.getSelectedIndex());
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
        if (origin_x > 0) origin_x = 0; if (origin_y > 0) origin_y = 0;
        int width = (this.getWidth()+(20*5))-origin_x;
        int height = (this.getHeight()+(20*5))-origin_y;
        for (int i = 0; i != width/20; i++) {
            for (int j = 0; j != height/20; j++) {
                g.drawRect((i*20)+origin_x, (j*20)+origin_y, 2, 2);
            }
        }

        if (GUI.getSceneCanvas().getActiveObject() == null) return;
        int flow_index = GUI.flowChooser.getSelectedIndex();
        if (flow_index > -1) {
            Flow f = GUI.getSceneCanvas().getActiveObject().FLOWS.get(flow_index);
            g.setColor(Color.black);
            for (int i = 0; i != f.blockCount(); i++) {
                Block b = f.getBlock(i);
                int b_width = b.dimensions()[0], b_height = b.dimensions()[1];
                int r_x = origin_x+b.getCoords()[0], r_y = origin_y+b.getCoords()[1];
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
                if (b.equals(selected_block)) {
                    g.setColor(Color.red);
                    g.drawRect(origin_x + b.getCoords()[0] - 21, origin_y + b.getCoords()[1] - 21, b.dimensions()[0] + 42, 
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
                        g.drawLine(r_x+40, r_y+b_height+10, origin_x+dest.getCoords()[0]+15, origin_y+dest.getCoords()[1]-10);
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
                        g.drawLine(r_x+b_width-15, r_y+b_height+10, origin_x+dest.getCoords()[0]+15, origin_y+dest.getCoords()[1]-10);
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
                        g.drawLine(r_x+b_width+10, r_y+b_height-15, origin_x+dest.getCoords()[0]+15, origin_y+dest.getCoords()[1]-10);
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
                        g.drawLine(r_x+15, r_y+b_height+10, origin_x+dest.getCoords()[0]+15, origin_y+dest.getCoords()[1]-10);
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
                    if (pb != null) g.drawLine(r_x-10, r_y+(p*20)+15, origin_x+pb.getCoords()[0]+40, origin_y+pb.getCoords()[1]+pb.dimensions()[1]+10);
                    if (MiscMath.pointIntersects(last_mouse_x, last_mouse_y, r_x-20, r_y+(p*20)+5, 20, 20)) {
                        g.setColor(new Color(0, 0, 0, 255));
                        String desc = b.getParametre(p, 1)+" ("+b.getParametre(p, 2)+")";
                        g.fillRect(last_mouse_x+10, last_mouse_y-30, 15+desc.length()*5, 25);
                        g.setColor(Color.white);
                        g.drawString(desc, last_mouse_x+15, last_mouse_y-15);
                    }
                }
                
            }
            
            //draw the line
            if (selected_block != null && selected_dot > -1) {
                g.setColor(new Color(0, 0, 0, 100));
                g.drawLine(origin_x + last_mouse_click_x, origin_y + last_mouse_click_y, last_mouse_x, last_mouse_y);
            }
            
        }
    }  
}