package project.objects.components;

import gui.GUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
    
    public static final int NODE_COUNT = 4, NODE_IN = 0, NODE_OUT = 1, NODE_YES = 2, NODE_NO = 3;
    public static final int ENTRY_BLOCK = 0, FUNCTION_BLOCK = 1,
            VARIABLE_BLOCK = 2, CONDITIONAL_BLOCK = 3, WHEN_BLOCK = 4;
    public static final int TYPE_NONE = 0, TYPE_ANY = 1, TYPE_NUMBER = 2, TYPE_STRING = 3;
    public static final String[] TYPE_NAMES = {"None", "Any", "Number", "Text"};
    
    private Node[] nodes; //in, out, yes, no
    private int block_type;
    private Flow parent;
    private String title, type;
    private int id;
    private int x = 50, y = 50;
    
    private static final Font font = new Font("Arial", Font.BOLD, 11);
    private static int title_width = 75;
    
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
        this.nodes = new Node[NODE_COUNT];
    }
    
    /**
     * Creates a new flowchart block for the list of template blocks.
     * @param name The title visible in the menu and in the editor.
     * @param block_type An integer representing the block's type (which determines its form).
     * @param output_type The type of value this block outputs.
     * @param params Specify the list of parametres and their starting values.
     */
    public Block(String name, String type, int block_type, int output_type, Object[] params) {
        this.id = Math.abs(new Random().nextInt());
        this.title = name;
        this.type = type;
        this.block_type = block_type;
        
        /**
         * this.nodes = new boolean[]{
        
            (block_type == FUNCTION_BLOCK && output_type == TYPE_NONE) //in
                || block_type == CONDITIONAL_BLOCK || block_type == VARIABLE_BLOCK
                || block_type == WHEN_BLOCK,
            block_type != CONDITIONAL_BLOCK, //out
            block_type == CONDITIONAL_BLOCK, //yes
            block_type == CONDITIONAL_BLOCK //no
            
        };
         */
        
        this.nodes = new Node[NODE_COUNT + (params == null ? 0 : params.length)];
        this.nodes[NODE_IN] = ((block_type == FUNCTION_BLOCK && output_type == TYPE_NONE)
                || block_type == CONDITIONAL_BLOCK) ? new Node(-1, 0, null, TYPE_NONE) : null;
        this.nodes[NODE_OUT] = block_type != CONDITIONAL_BLOCK ? new Node(0, -1, null, output_type) : null; 
        this.nodes[NODE_YES] = block_type == CONDITIONAL_BLOCK ? new Node(0, -1, null, TYPE_NONE) : null; 
        this.nodes[NODE_NO] = block_type == CONDITIONAL_BLOCK ? new Node(0, -1, null, TYPE_NONE) : null; 

        for (int n = NODE_COUNT; n < nodes.length; n++) 
            this.nodes[n] = new Node();
        
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
        
        
        if (!canConnectTo(b, to, from)) return false;
        
        Node b_to = b.getNode(to);
        Node t_from = getNode(from);
        if (b_to == null || t_from == null) return false;
        
        int to_index = b_to.addConnection(new Connection(getID(), from), Node.INCOMING);
        int from_index = t_from.addConnection(new Connection(b.getID(), to), Node.OUTGOING);
        
        if (to_index == -1 || from_index == -1) return false;
        
        return true;
    }
    
    private boolean canConnectTo(Block b, int to, int from) {
        if (b == null) return false;
        boolean accept_connection = true;
        
        return accept_connection;
    }
    
    public Node getNode(int index) {
        return nodes[index];
    }
    
    public void move(double x, double y) {
        this.x += x; this.y += y;
    }
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("b\n");
            bw.write("t="+title+"\n");
            bw.write("id="+id+"\n");
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
     * while anything above represents the parameter dots. Will not return the parameter nodes if
     * this block is a VARIABLE_BLOCK (they are not to be shown in the editor).
     * @param x Coordinates from the origin.
     * @param y Coordinates from the origin.
     * @return An integer (see above). Returns -1 if no dot was clicked.
     */
    public int getNodeIndex(double x, double y) {
        //check for parametres
        for (int i = 0; i < NODE_COUNT + paramCount(); i++) {
            if (i < nodes.length) if (nodes[i] == null) continue;
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
    
    public Node[] nodes() {
        return nodes;
    }
    
    public int getID() { return id; }
    
    public int[] getCoords() { return new int[]{x, y}; }
    
    public int paramCount() {
        return nodes.length - NODE_COUNT;
    }
    
    public String getTitle() { return title; }
    
    public int[] dimensions() {
        int b_width = title_width + 10, 
                b_height = paramCount() > 0 ? 20*paramCount() + 10 : 30;
        if (b_width < 75) b_width = 75;
        return new int[]{b_width, b_height};
    }
    
    /**
     * Sets the default value of the parameter. If the block is a VARIABLE_BLOCK,
     * then the value will be used as the return value for that specific index.
     * @param n_index
     * @param new_value 
     */
    public void setValue(int n_index, String new_value) {
        nodes[n_index].setValue(new_value);
    }
    
    public void randomID() {
        this.id = Math.abs(new Random().nextInt());
    }
    
    public void copyTo(Block b, boolean copy_id) {
        b.title = title;
        b.type = type;
        if (copy_id) b.id = id;
        b.x = x;
        b.y = y;
        b.nodes = new Node[nodes.length];
        for (int n = 0; n < nodes.length; n++) {
            b.nodes[n] = new Node(0, 0, "", 0);
            nodes[n].copyTo(b.nodes[n]);
        }
    }
    
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        
        int[] dims = dimensions();
        
        int[] rc = getRenderCoords();
        
        Color[] b_colors = {Color.lightGray, Color.blue, Color.green, Color.red, Color.yellow.darker()};
        for (int i = 0; i < NODE_COUNT + (block_type != VARIABLE_BLOCK ? paramCount() : 0); i++) {
            
            if (i < nodes.length) if (nodes[i] == null) continue;
            int[] offset = getNodeOffset(i);
            
            Color from_color = b_colors[i > 4 ? 4 : i];
            g2.setColor(from_color);
            g2.fillRect(rc[0] + offset[0], rc[1] + offset[1], 20, 20);
            g2.setColor(from_color.darker());
            g2.drawRect(rc[0] + offset[0], rc[1] + offset[1], 20, 20);
            
            /*Connection c = nodes[i].
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
            
            g2.draw(line2d);*/
            
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
        if (index == NODE_IN) return new int[]{dims[0]/2 - 10, -20};
        if (index == NODE_OUT) return new int[]{dims[0]/2 - 10, dims[1]};
        if (index == NODE_YES) return new int[]{10, dims[1]};
        if (index == NODE_NO) return new int[]{dims[0] - 30, dims[1]};
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
    
    @Override
    public String toString() {
        return getTitle()+" ("+getType()+")";
    }
    
}

class Node {

    private String value;
    private int value_type;
    private Connection[] incoming, outgoing;
    private Block parent;
    
    public static int INCOMING = 0, OUTGOING = 1;
    
    public Node(int in, int out, String default_value, int value_type) {
        this.incoming = new Connection[in];
        this.outgoing = new Connection[out];
        this.value_type = value_type;
        this.value = default_value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    public Block getParent() {
        return parent;
    }

    public void setParent(Block parent) {
        this.parent = parent;
    }
    
    public boolean removeConnection(int index, int direction) {
        Connection[] list = (direction == Node.INCOMING ? incoming : outgoing);
        if (index < 0 || index >= list.length) return false;
        list[index] = null; return true;
    }
    
    public int addConnection(Connection c, int direction) {
        Connection[] list = (direction == Node.INCOMING ? incoming : outgoing);
        for (int i = 0; i < list.length; i++)
            if (list[i] == null) { list[i] = c; return i; }
        return -1;
    }
    
    public void copyTo(Node n) {
        n.value = value;
        n.parent = parent;
        n.value_type = value_type;
        n.incoming = new Connection[incoming.length];
        n.outgoing = new Connection[outgoing.length];
        for (int i = 0; i < incoming.length; i++) 
            n.incoming[i] = new Connection(incoming[i].block_id, incoming[i].node_index);
        for (int i = 0; i < outgoing.length; i++) 
            n.outgoing[i] = new Connection(outgoing[i].block_id, outgoing[i].node_index);
    }

}

class Connection {
    
    int block_id, node_index;
    public Connection(int block_id, int node_index) {
        this.block_id = block_id;
        this.node_index = node_index;
    }
    
    public int blockID() { return block_id; }
    public int nodeIndex() { return node_index; }
    
}
