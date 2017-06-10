package project.objects.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import misc.Assets;
import project.objects.SceneObject;

public class Flow {
        
    private ArrayList<Block> blocks;
    private boolean locked;
    
    private String name;
    
    public Flow() {
        this.locked = false;
        this.blocks = new ArrayList<Block>();
        this.name = "";
    }
    
    public void autoName(SceneObject o) {
        int i = 1;
        while (true) {
            String n = "flow"+(i > 1 ? ""+i : "");
            if (!o.containsFlow(n)) { setName(n); break; }
            i++;
        }
    }
    
    public String getName() { return name; }
    
    public void setName(String new_) { name = new_; }
    
    public void setLocked(boolean l) {
        locked = l;
    }
    
    public boolean isLocked() { return locked; }
    
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
        if (blocks.contains(b) == false) {
            blocks.add(b);
            b.setParent(this);
        } else return;
        System.out.println("Added Block "+ b.getTitle()+" to Flow "+name+" ("+b.getCoords()[0]+", "+b.getCoords()[1]+")");
    }
    
    public Block getBlockByID(int id) {
        for (Block b: blocks) {
            if (b.getID() == id) {
                return b;
            }
        }
        return null;
    }
    
    public boolean removeBlock(int index) {
        if (index < 0 || index >= blocks.size()) return false;
        Block b = blocks.get(index);
        b.clearAllConnections();
        return blocks.remove(index) != null;
    }
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("f\n");
            bw.write("id="+name+"\n");
            for (Block b: blocks) b.save(bw);
            bw.write("/f\n");
        } catch (IOException ex) {
            Logger.getLogger(Flow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean load(BufferedReader br) {
        System.out.println("Loading flow...");
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                if (line.equals("/f")) return true;
                if (line.indexOf("id=") == 0) name = line.trim().replace("id=", "");
                if (line.equals("b")) {
                    Block b = new Block();
                    if (b.load(br)) blocks.add(b);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Flow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Flow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * Copies the contents of this flow to flow F.
     * @param f The flow, stupid.
     */
    public void copyTo(Flow f, boolean copy_name) {
        if (copy_name) f.name = name;
        f.blocks.clear();
        for (Block b: blocks) {
            Block new_b = new Block();
            b.copyTo(new_b, true);
            f.addBlock(new_b);
        }
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
}
