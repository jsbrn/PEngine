package gui;

import java.awt.Color;
import java.awt.GradientPaint;
import project.Project;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import project.objects.SceneObject;

public class SceneObjectCanvas extends JPanel {
    
    public SceneObjectCanvas() {

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if (Project.getProject() == null) return;
        if (Project.getProject().getCurrentLevel() == null) return;
        
        Color top_color = Project.getProject().getCurrentLevel().getTopBGColor(), 
                bttm_color = Project.getProject().getCurrentLevel().getBottomBGColor();
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp1 = new GradientPaint(0, 0, top_color, 0, getHeight(), bttm_color, true);
        g2d.setPaint(gp1);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        SceneObject o = GUI.getSceneCanvas().getActiveObject();
        if (o == null) return;
        
        if (o == null) return;
        int z = (getWidth() - 150 > getHeight() ? (int)(getWidth() - 150) / o.getDimensions()[0]
                : getHeight() / o.getDimensions()[1]) / 2;
        if (z < 1) z = 1;
        int w = o.getDimensions()[0] * z, h = o.getDimensions()[1] * z;
        o.draw((int)(getWidth()/2 + 150) - w/2, getHeight()/2 - h/2, z, g);
  
    }  
}