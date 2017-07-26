package gui;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import project.Level;
import project.Project;
import project.objects.SceneObject;
import project.objects.components.Flow;

public class Navigator {
    
    public static final int LEVEL = 0, OBJECT = 1, FLOW = 2;
    
    private static JTree tree;
    
    public static void init(JTree nav) {
        tree = nav;
        refresh();
    }
    
    public static void refresh() {
        DefaultMutableTreeNode root = newNode("Project");
        
        DefaultMutableTreeNode levels = newNode("Levels");
        for (Level l: Project.getProject().getLevels()) {
            DefaultMutableTreeNode level = newNode(l.getName());
            DefaultMutableTreeNode objects = newNode("Objects");
            for (SceneObject o: l.getObjects(Level.ALL_OBJECTS)) {
                DefaultMutableTreeNode obj = newNode(l.getName());
                for (Flow f: o.getFlows()) {
                    obj.add(newNode(f.getName()));
                }
                objects.add(obj);
            }
            level.add(objects);
            levels.add(level);
        }
        root.add(levels);
        
        DefaultTreeModel treemodel = new DefaultTreeModel(root);
        tree.setModel(treemodel);
    }
    
    private static DefaultMutableTreeNode newNode(String name) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
        return node;
    }
    
}
