package project.objects.components;

import gui.FlowCanvas;
import gui.GUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import misc.Assets;
import misc.MiscMath;

public class Block {
    
    public static final int NODE_COUNT = 4, IN = 0, OUT = 1, YES = 2, NO = 3;
    public static final int FUNCTION_BLOCK = 0, CONDITIONAL_BLOCK = 1,
            EVENT_BLOCK = 2, VARIABLE_BLOCK = 3;
    public static final int TYPE_NONE = 0, TYPE_ANY = 1, TYPE_NUMBER = 2, TYPE_STRING = 3;
    public static final String[] TYPE_NAMES = {"None", "Any", "Number", "Text"};
    
    private boolean[] nodes; //in, out, yes, no
    private int[][] connections; //the first 4 are for the nodes, the rest are for params. {id, other_port}
    
    private Object[][] parametres; //a list of {name, type, default_value} entries
    
    private int output_type; //the type of value that the OUT connection supplies
    
    private Flow parent;
    
    private String title, type;
    private int id;
    
    private int x = 50, y = 50;
    
    private static final Font font = new Font("Arial", Font.BOLD, 11);
    private int title_width;
    
    public static Block create(String type) {
        Block template = Assets.getBlock(type);
        if (template == null) {
            System.err.println("Cannot find a block by type \""+type+"\"");
            return null;
        }
        Block new_b = new Block();
        template.copyTo(new_b, false);
        return new_b;
    }
    
    public Block() {
        this.id = Math.abs(new Random().nextInt());
        this.title = "";
        this.type = "";
        this.nodes = new boolean[NODE_COUNT];
        this.connections = new int[NODE_COUNT][2];
        this.output_type = TYPE_NONE;
        this.parametres = new Object[NODE_COUNT][3]; //name, type, default value
        this.title_width = 75;
    }
    
    /**
     * Creates a new flowchart block for the list of template blocks.
     * @param name The title visible in the menu and in the editor.
     * @param block_type An integer representing the block's type (which determines its form).
     * @param output_type The type of value this block outputs.
     * @param params Specify the list of parametres and their starting values.
     */
    public Block(String name, String type, int block_type, int output_type, Object[][] params) {
        
        this.id = Math.abs(new Random().nextInt());
        this.title = name;
        this.type = type;
        this.nodes = new boolean[]{
        
            (block_type == FUNCTION_BLOCK && output_type == TYPE_NONE) //in
                || block_type == CONDITIONAL_BLOCK || block_type == VARIABLE_BLOCK,
            block_type != CONDITIONAL_BLOCK, //out
            block_type == CONDITIONAL_BLOCK, //yes
            block_type == CONDITIONAL_BLOCK //no
            
        };
        this.connections = new int[NODE_COUNT+params.length][2];
        this.output_type = output_type;
        this.parametres = params;
        
    }    
    
    
    /**
     * Connects this block to the specified block via the specified nodes.
     * @param b The other block to connect to. It will overwrite any existing connections on
     * the two specified nodes, and will always clear IN on the target block, to disable
     * tracking and allow multiple OUT -> IN connections.<br><br>It will also respect the connection
     * rules (for example, YES -> OUT is forbidden).
     * @param to Which node on the other block?
     * @param from Which node on this block?
     * @return A boolean indicating success.
     */
    public boolean connectTo(Block b, int to, int from) {
        if (b == null) return false;
        boolean accept_connection = false;
        //accept parameter connections from OUT to PARAMS
        if (to >= Block.NODE_COUNT) {
            accept_connection = from == Block.OUT
                    && (int)b.getParametre(to-NODE_COUNT)[2] == (int)getParametre(from-NODE_COUNT)[2];
        }
        //accept out, yes and no to IN
        if (to == Block.IN) accept_connection = from == Block.OUT || from == Block.YES || from == Block.NO;
        if (!accept_connection) return false;
        
        if (to < 0 || to >= b.connections.length) return false;
        if (from < 0 || from >= connections.length) return false;
        
        if (b.connections[to][0] > 0) b.clearConnection(to, true);
        if (connections[from][0] > 0) clearConnection(from, true);
        b.connections[to] = new int[]{id, from};
        connections[from] = new int[]{b.id, to};
        
        if (to == IN) b.connections[to] = new int[]{0, -1};
        
        return true;
    }
    
    /**
     * Clears the connection at <i>index</i>. Does not clear for the block 
     * on the other end.
     * @param index The index.
     */
    public void clearConnection(int index, boolean two_sided) {
        if (index < 0 || index >= connections.length) return;
        System.out.print("Cleared ["+getType()+", "+index+"] ... ");
        if (two_sided) {
            Block other = parent.getBlockByID(connections[index][0]);
            if (other != null) other.clearConnection(connections[index][1], false);
            System.out.println();
        }
        connections[index] = new int[]{0, -1};
    }
    
    public void clearAllConnections() {
        for (int i = 0; i < connections.length; i++) clearConnection(i, true);
    }
    
    public void move(double x, double y) {
        this.x += x; this.y += y;
    }
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("b\n");
            bw.write("t="+title+"\n");
            bw.write("id="+id+"\n");
            String conns = ""; for (int c[]: connections) conns+=c[0]+" "+c[1]+"\t";
            bw.write("conns="+conns.trim()+"\n");
            bw.write("x="+x+"\n");
            bw.write("y="+y+"\n");
            bw.write("/b\n");
        } catch (IOException ex) {
            Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean load(BufferedReader br) {
        System.out.println("Loading block...");
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.equals("/b")) return true;
                if (line.indexOf("id=") == 0) id = Integer.parseInt(line.trim().replace("id=", ""));
                if (line.indexOf("t=") == 0) { 
                    title = line.trim().replace("t=", "");
                    //Block copy = getBlock(title);
                    //if (copy != null) copy.copyTo(this);
                }
                if (line.indexOf("x=") == 0) x = Integer.parseInt(line.trim().replace("x=", ""));
                if (line.indexOf("y=") == 0) y = Integer.parseInt(line.trim().replace("y=", ""));
                if (line.indexOf("conns=") == 0) {
                    String[] b = line.split("\t");
                    connections = new int[NODE_COUNT][2];
                    for (int i = 0; i != b.length; i++) connections[i] = MiscMath.toIntArray(b[i], NODE_COUNT);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    
    /**
     * Sets the ID of the block to i. This will break any connections referencing the old ID.
     * Should only be used in saving/loading block data.
     */
    public void setID(int i) {
        id = i;
    }
    
    public void setParent(Flow f) {
        parent = f;
    }
    
    /**
     * Returns an integer describing the dot that was clicked. 0-4 represents the in/out/yes/no/etc dots,
     * while anything above represents the parameter dots.
     * @param x Coordinates from the origin.
     * @param y Coordinates from the origin.
     * @return An integer (see above). Returns -1 if no dot was clicked.
     */
    public int getNode(double x, double y) {
        //check for parametres
        for (int i = 0; i < NODE_COUNT + paramCount(); i++) {
            if (i < nodes.length) if (!nodes[i]) continue;
            int[] rc = getRenderCoords();
            int[] offset = getNodeOffset(i);
            if (MiscMath.pointIntersects(x, y, rc[0]+offset[0], rc[1]+offset[1], 20, 20)) return i;
        }
        return -1;
    }
    
    public String getType() { return type; }
    
    public void setX(int new_x) { x = new_x; }
    public void setY(int new_y) { y = new_y; }
    
    /**
     * Pass in the graphics context that is being used to draw this block,
     * and a String, and get back the actual width of the rendered text.
     * @param s
     * @param g
     * @return An integer.
     */
    public int getFontWidth(String s, Graphics g) {
        return (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
    }
    
    public boolean[] nodes() {
        return nodes;
    }
    
    public int getID() {
        return id;
    }
    
    public int[] getCoords() {
        return new int[]{x, y};
    }
    
    public int paramCount() {
        return connections.length - NODE_COUNT;
    }
    
    public String getTitle() { return title; }
    
    public int[] dimensions() {
        int b_width = title_width + 10, 
                b_height = paramCount() > 0 ? 20*paramCount() + 10 : 30;
        if (b_width < 75) b_width = 75;
        return new int[]{b_width, b_height};
    }
    
    public int getOutputType() {
        return output_type;
    }
    
    public void setDefaultValue(int p_index, String new_value) {
        parametres[p_index][2] = new_value;
    }
    
    public Object[] getParametre(int index) {
        if (index < 0 || index >= paramCount()) return new Object[]{null, null};
        return parametres[index];
    }
    
    public void randomID() {
        this.id = Math.abs(new Random().nextInt());
    }
    
    public int getConnection(int index) {
        if (index <= -1 || index >= connections.length) return -1;
        return connections[index][0];
    }
    
    public void copyTo(Block b, boolean copy_id) {
        b.title = title;
        b.type = type;
        if (copy_id) b.id = id;
        b.x = x;
        b.y = y;
        b.output_type = output_type;
        b.nodes = new boolean[nodes.length];
        b.connections = new int[connections.length][2];
        b.parametres = new Object[parametres.length][3];
        for (int i = 0; i != nodes.length; i++) {
            b.nodes[i] = nodes[i];
        }
        for (int i = 0; i != connections.length; i++) {
            b.connections[i] = new int[]{connections[i][0], connections[i][1]};
        }
        for (int i = 0; i != parametres.length; i++) {
            b.parametres[i] = new Object[]{parametres[i][0], parametres[i][1], parametres[i][2]};
        }
        b.parametres = parametres;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        
        int[] dims = dimensions();
        
        int[] rc = getRenderCoords();
        
        Color[] b_colors = {Color.lightGray, Color.blue, Color.green, Color.red, Color.yellow.darker()};
        for (int i = 0; i < NODE_COUNT + paramCount(); i++) {
            
            if (i < nodes.length) if (!nodes[i]) continue;
            int[] offset = getNodeOffset(i);
            
            Color from_color = b_colors[i > 4 ? 4 : i];
            g2.setColor(from_color);
            g2.fillRect(rc[0] + offset[0], rc[1] + offset[1], 20, 20);
            g2.setColor(from_color.darker());
            g2.drawRect(rc[0] + offset[0], rc[1] + offset[1], 20, 20);
            
            int[] conn = connections[i];
            Block b_conn = parent.getBlockByID(conn[0]);
            if (b_conn == null) continue;
            int[] brc = b_conn.getRenderCoords();
            int[] bno = b_conn.getNodeOffset(conn[1]);
            
            Color to_color = b_colors[conn[1] > 4 ? 4 : conn[1]];
            
            int[] line = new int[]{rc[0]+offset[0]+10, rc[1]+offset[1]+10, brc[0]+bno[0]+10, brc[1]+bno[1]+10};
            Line2D line2d = new Line2D.Float(line[0], line[1], line[2], line[3]);
            
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gp1 = new GradientPaint(line[0], line[1], from_color, line[2], line[3], to_color, false);
            g2d.setPaint(gp1);
            
            g2.draw(line2d);
            
        }
        
        g2.setColor(Color.white);
        g2.fillRect(rc[0], rc[1], dims[0], dims[1]);
        g2.setColor(Color.black);
        g2.drawRect(rc[0], rc[1], dims[0], dims[1]);
        g2.setFont(font);
        title_width = (int)g2.getFontMetrics().getStringBounds(title, g2).getWidth();
        g2.drawString(getTitle(), rc[0] + 5, rc[1] + 15);
        
    }
    
    //TODO: Implement this!!!
    public int[] getRenderCoords() { return GUI.getFlowCanvas().getOnscreenCoords(x-dimensions()[0], y-dimensions()[1]); }
    
    /**
     * Returns the offset position of the node relative to the origin at
     * the top left of the block. The origin of the node is top left as well.
     * @param index IN, OUT, YES, NO (or anything higher to mean a parameter.
     * @return An int[] of size 2.
     */
    public int[] getNodeOffset(int index) {
        int[] dims = dimensions();
        //assuming a "node" is 20px by 20px on the screen
        if (index == IN) return new int[]{dims[0]/2 - 10, -20};
        if (index == OUT) return new int[]{dims[0]/2 - 10, dims[1]};
        if (index == YES) return new int[]{10, dims[1]};
        if (index == NO) return new int[]{dims[0] - 30, dims[1]};
        int p_h = 20*paramCount();
        if (index >= NODE_COUNT) return new int[]{-20, 
            (dims[1]/2) + (20*(index-NODE_COUNT)) - (p_h / 2)}; //params
        return new int[]{0, 0};
    }
    
    /*public boolean equalTo(Block b) {
        if (!b.title.equals(title)) return false;
        if (b.id != id) return false;
        for (int i = 0; i != nodes.length; i++) {
            if (b.nodes[i] != nodes[i]) return false;
            if (b.dot_conns[i] != (dot_conns[i])) return false;
        }
        for (int i = 0; i != values.length; i++) {
            if (!b.values[i][0].equals(values[i][0]) 
                    || !b.values[i][1].equals(values[i][1])
                    || !b.values[i][2].equals(values[i][2])) return false;
            if (b.param_conns[i] != (param_conns[i])) return false;
        }
        return true;
    }*/
    
    public void setNodes(boolean in, boolean out, boolean yes, boolean no, boolean ok) {
        nodes = new boolean[]{in, out, yes, no, ok};
    }
    
    @Override
    public String toString() {
        return getTitle()+" ("+getType()+")";
    }
    
}
