package gui;

import java.awt.Color;
import project.Project;
import java.awt.Graphics;
import javax.swing.JPanel;
import project.objects.components.Block;
import project.objects.components.Flow;
import misc.MiscMath;

public class FlowCanvas extends JPanel {

    public static int ORIGIN_X = 0, ORIGIN_Y = 0, LAST_MOUSE_X = 0, LAST_MOUSE_Y = 0;
    public static int SELECTED_DOT = -1;
    public static int LAST_MOUSE_CLICK_X = 0, LAST_MOUSE_CLICK_Y = 0;
    public static Block SELECTED_BLOCK;
    
    public FlowCanvas() {

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
    }  
}