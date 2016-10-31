package misc;

import java.util.ArrayList;
import scene.Scene;
import scene.SceneObject;

public class Flow {
    
    ArrayList<Block> blocks;
    Level parent_level = null;
    SceneObject parent_object = null;
    
    public String NAME;
    public boolean RUN_ON_SPAWN;
    
    public Flow() {
        this.blocks = new ArrayList<Block>();
        this.RUN_ON_SPAWN = false;
        this.NAME = "";
    }
    
    /**
     * Refactors all of the matching instances of Flow, only if editing a gallery object.
     * Will only refactor a flow to match new_flow if the flow is equal to compare_to.
     * @param new_flow The flow to match the others with...
     * @param compare_to ...but only if the flow in question is equal to compare_to.
     */
    public static void refactor(Flow new_flow, Flow compare_to) {
        System.out.println("Refactoring all valid flows.");
        if (Scene.OBJECT_GALLERY.contains(Scene.ACTIVE_EDIT_OBJECT)) {
            for (SceneObject o: Scene.getObjectsByType(Scene.ACTIVE_EDIT_OBJECT.CLASS)) {
                for (Flow f: o.FLOWS) {
                    if (f.equalTo(compare_to)) {
                        new_flow.copyTo(f);
                    }
                }
            }
        }
    }
    
    public Block getBlock(int index) {
        return blocks.get(index);
    }
    
    public int blockCount() {
        return blocks.size();
    }
    
    public int indexOf(Block b) {
        return blocks.indexOf(b);
    }
    
    public void addBlock(Block b) {
        if (blocks.contains(b) == false) blocks.add(b);
        System.out.println("Added Block "+b.category +" "+ b.title+" to Flow "+this.NAME);
    }
    
    public Block getBlockByID(int id) {
        for (Block b: blocks) {
            if (b.getID() == id) {
                return b;
            }
        }
        return null;
    }
    
    public void removeBlock(int index) {
        blocks.remove(index);
    }
    
    public void setParent(Level l) {
        parent_level = l;
        parent_object = null;
    }
    
    public void setParent(SceneObject o) {
        parent_level = null;
        parent_object = o;
    }
    
    public boolean equalTo(Flow f) {
        if (!NAME.equals(f.NAME)) return false;
        for (Block b: blocks) {
            if (!b.equalTo(b)) return false;
        }
        return true;
    }
    
    /**
     * Copies the contents of this flow to flow F.
     * @param f The flow, stupid.
     */
    public void copyTo(Flow f) {
        f.parent_level = parent_level;
        f.parent_object = parent_object;
        f.NAME = NAME;
        f.blocks.clear();
        for (Block b: blocks) {
            Block new_b = new Block();
            b.copyTo(new_b);
            f.blocks.add(new_b);
        }
    }
    
}
