package project.objects.components;

import java.util.ArrayList;
import project.Level;
import project.Project;
import project.objects.SceneObject;

public class Flow {
    
    private ArrayList<Block> blocks;
    private Level parent_level = null;
    private SceneObject parent_object = null;
    
    private String name;
    private boolean run_on_spawn, locked = false;
    
    public Flow() {
        this.blocks = new ArrayList<Block>();
        this.run_on_spawn = false;
        this.name = "";
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
        System.out.println("Added Block "+b.category +" "+ b.title+" to Flow "+this.name);
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
        if (!name.equals(f.name)) return false;
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
        f.name = name;
        f.blocks.clear();
        for (Block b: blocks) {
            Block new_b = new Block();
            b.copyTo(new_b);
            f.blocks.add(new_b);
        }
    }
    
}
