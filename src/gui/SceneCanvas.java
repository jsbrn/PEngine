package gui;

import scene.Scene;
import java.awt.Graphics;
import javax.swing.JPanel;

public class SceneCanvas extends JPanel {

    public SceneCanvas() {

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Scene.CANVAS_WIDTH = this.getWidth();
        Scene.CANVAS_HEIGHT = this.getHeight();
        Scene.draw(g);
    }  
}