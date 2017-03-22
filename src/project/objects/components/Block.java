package project.objects.components;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import misc.MiscMath;
import project.Project;

public class Block {
    
    private boolean[] dots; //in, out, yes, no, ok
    private int[] dot_conns, param_conns; //the actual connections
    
    private String[][] values; //parametres use this. a value is {value, name, type}
    String output_type; //the type of value that the OUT connection supplies
    
    String title, category, type;
    int id;
    
    int x = 50, y = 50;
    
    
    public Block() {
        this.id = Math.abs(new Random().nextInt());
        this.title = "";
        this.category = "";
        this.type = "";
        this.dots = new boolean[5];
        this.dot_conns = new int[5];
        this.param_conns = new int[0];
        this.values = new String[0][3];
        this.output_type = "";
    }
    
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the ID of the block to i. This will break any connections referencing the old ID.
     * Should only be used in saving/loading block data.
     */
    public void setID(int i) {
        id = i;
    }
    
    public void breakConnections() {
        for (int i = 0; i != param_conns.length; i++) { param_conns[i] = 0; }
        for (int i = 0; i != dot_conns.length; i++) { dot_conns[i] = 0; }
    }
    
    /**
     * Returns an integer describing the dot that was clicked. 0-4 represents the in/out/yes/no/etc dots,
     * while anything above represents the parametre dots.
     * @param x Coordinates from the origin.
     * @param y Coordinates from the origin.
     * @return An integer (see above). Returns -1 if no dot was clicked.
     */
    public int getDot(int x, int y) {
        //check for parametres
        for (int i = 0; i != paramCount(); i++) {
            if (MiscMath.pointIntersects(x, y, this.x-20, this.y+5+(i*20), 20, 20)) {
                return i+5;
            }
        }
        if (MiscMath.pointIntersects(x, y, this.x+5, this.y-20, 20, 20) && dots[0]) {
            return 0; //IN
        }
        if (MiscMath.pointIntersects(x, y, this.x+30, this.y+dimensions()[1], 20, 20) && dots[1]) {
            return 1; //OUT
        }
        if (MiscMath.pointIntersects(x, y, this.x+dimensions()[0]-25, this.y+dimensions()[1], 20, 20) && dots[2]) {
            return 2; //YES
        }
        if (MiscMath.pointIntersects(x, y, this.x+dimensions()[0], this.y+dimensions()[1]-25, 20, 20) && dots[3]) {
            return 3; //NO
        }
        if (MiscMath.pointIntersects(x, y, this.x+5, this.y+dimensions()[1], 20, 20) && dots[4]) {
            return 4; //OK
        }
        return -1;
    }
    
    public void setX(int new_x) { x = new_x; if (x < 0) x = 0; }
    public void setY(int new_y) { y = new_y; if (y < 0) y = 0; }
    
    public boolean[] dots() {
        return dots;
    }
    
    public int[] dotConns() {
        return dot_conns;
    }
    
    public int getID() {
        return id;
    }
    
    public int[] getCoords() {
        return new int[]{x, y};
    }
    
    public int paramCount() {
        return values.length;
    }
    
    public int[] dimensions() {
        int b_width = 50+(title().length()*5), b_height = 25 + (20*paramCount());
        return new int[]{b_width, b_height};
    }
    
    /**
     * Creates a new flowchart block.
     * @param title The title visible in the menu and in the editor.
     * @param type The type of the block (i.e. "set_color") for the game to recognize.
     * @param category Where it belongs in the block chooser menu (ex. "All Blocks/Actions"). Only used by the editor.
     * @param input_map A String that describes which I/Os are active (ex. fffft means only the OK output is active).
     * @param param_count How many parametres does this block have?
     */
    public Block(String title, String type, String category, String input_map, String output_type, String[][] values) {
        
        this.id = Math.abs(new Random().nextInt());
        this.title = title;
        this.category = category;
        this.type = type;
        for (int i = 0; i != input_map.length(); i++) {
            if (input_map.charAt(i) != 't' && input_map.charAt(i) != 'f') {
                input_map = input_map.replace(input_map.charAt(i)+"", "");
            }
        }
        if (input_map.length() != 5) {
            System.err.println("Block "+title+": input map "+input_map+" must be 5 chars in length,"
                    + " and must consist of characters t and f only!");
            this.dots = new boolean[]{false, false, false, false, false};
        } else {
            String input = (input_map+"").replace("t", "true\n").replace("f", "false\n");
            ArrayList<String> inputs = Project.parseString(input);
            this.dots = new boolean[]{Boolean.parseBoolean(inputs.get(0)),
                Boolean.parseBoolean(inputs.get(1)),
                Boolean.parseBoolean(inputs.get(2)),
                Boolean.parseBoolean(inputs.get(3)),
                Boolean.parseBoolean(inputs.get(4))};
        }
        this.dot_conns = new int[5];
        this.param_conns = new int[values.length];
        this.values = values;
        
    }
    
    public String getOutputType() {
        return output_type;
    }
    
    public void setParametreConnection(int index, int value) {
        if (index <= -1 || index >= param_conns.length) return;
        System.out.println("Parametre "+index+" is now "+value);
        param_conns[index] = value;
    }
    
    /**
     * Returns a property of the specified parametre. For instance, passing (0, 0) will give you
     * the starting value of the 0th param. (0, 1) will give you the name of the param. (0, 2), the
     * type. Valid types are:
     * <br>
     * <li>number<br>
     * <li>text<br>
     * <li>object<br>
     * <li>level<br>
     * <li>flowchart<br>
     * <li>animation<br>
     * <li>dialogue<br>
     */
    public String getParametre(int index, int dindex) {
        if (dindex < 0 || dindex > 2) return null;
        if (index <= -1 || index >= values.length) return null;
        System.out.println();
        return values[index][dindex];
    }
    
    public void randomID() {
        this.id = Math.abs(new Random().nextInt());
    }
    
    public int getConnection(int index) {
        if (index <= -1 || index >= dot_conns.length) return -1;
        return dot_conns[index];
    }
    
    public int getParametreConnection(int index) {
        if (index <= -1 || index >= param_conns.length) return -1;
        return param_conns[index];
    }
    
    public void setConnection(int index, int block_id) {
        if (index <= -1 || index >= dot_conns.length) return;
        dot_conns[index] = block_id;
    }
    
    public void setParametre(int pindex, int dindex, String new_val) {
        if (dindex < 0 || dindex > 2) return;
        if (pindex <= -1 || pindex >= values.length) return;
        values[pindex][dindex] = new_val;
    }
    
    public String title() {
        return title;
    }
    
    public void copyTo(Block b) {
        b.title = title;
        b.category = category;
        b.type = type;
        b.id = id;
        b.x = x;
        b.y = y;
        b.output_type = output_type;
        b.dots = new boolean[dots.length];
        b.dot_conns = new int[dot_conns.length];
        b.param_conns = new int[param_conns.length];
        b.values = new String[values.length][3];
        for (int i = 0; i != dots.length; i++) {
            b.dots[i] = dots[i];
            b.dot_conns[i] = dot_conns[i];
        }
        for (int i = 0; i != values.length; i++) {
            b.values[i] = new String[]{values[i][0], values[i][1], values[i][2]};
            b.param_conns[i] = param_conns[i];
        }
    }
    
    public String getCategory() {
        return category;
    }
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("b\n");
            bw.write("id="+id+"\n");
            
            String dc = ""; for (int i : dot_conns) dc += i+" ";
            bw.write("dc="+dc);
            
            String pc = ""; for (int i : param_conns) pc += i+" ";
            bw.write("pc="+pc+"\n");
            
            for (String[] o : values) {
                bw.write("v="+o[0]+"\n");
            }
            
            bw.write("/b");
            
        } catch (IOException ex) {
            Logger.getLogger(Animation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean equalTo(Block b) {
        if (!b.title.equals(title)) return false;
        if (!b.category.equals(category)) return false;
        if (!b.type.equals(type)) return false;
        if (b.id != id) return false;
        for (int i = 0; i != dots.length; i++) {
            if (b.dots[i] != dots[i]) return false;
            if (b.dot_conns[i] != (dot_conns[i])) return false;
        }
        for (int i = 0; i != values.length; i++) {
            if (!b.values[i][0].equals(values[i][0]) 
                    || !b.values[i][1].equals(values[i][1])
                    || !b.values[i][2].equals(values[i][2])) return false;
            if (b.param_conns[i] != (param_conns[i])) return false;
        }
        return true;
    }
    
    public void setDots(boolean in, boolean out, boolean yes, boolean no, boolean ok) {
        dots = new boolean[]{in, out, yes, no, ok};
    }
}