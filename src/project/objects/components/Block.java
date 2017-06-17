package project.objects.components;

import gui.GUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import misc.Assets;
import misc.MiscMath;
import project.Level;
import project.Project;
import project.objects.SceneObject;

public class Block {
    
    public static final int NODE_COUNT = 4, NODE_IN = 0, NODE_OUT = 1, NODE_YES = 2, NODE_NO = 3;
    
    private boolean[] nodes; //in, out, yes, no
    private int[][] conns;
    private Object[][] inputs, outputs;
    private Flow parent;
    private String name, type, summary;
    private int id;
    private int x = 50, y = 50;
    
    private static final Font font = new Font("Arial", Font.BOLD, 11);
    private int title_width = 75, summary_width = 75;

    /**
     * Creates a new Block from the specified template block in Assets.
     * @param type The type/class of the block.
     * @return null if no block by that type, else the new Block.
     */
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
        this.id = Math.abs(new Random().nextInt()-1)+1;
        this.name = "";
        this.type = "";
        this.nodes = new boolean[NODE_COUNT];
        this.conns = new int[NODE_COUNT][2];
    }
    
    public Flow getParent() { return parent; }
    
    /**
     * Creates a new flowchart block.
     * @param name The title visible in the menu and in the editor.
     * @param type The internal identifier class of this block.
     * @param node_str A string ("tttt", "ffff", etc.) that determines which nodes are enabled.
     * @param inputs The inputs (a list of {name, type} object arrays). null will initialize to an empty array.
     * @param outputs See inputs.
     */
    public Block(String name, String summary, String type, String node_str, Object[][] inputs, Object[][] outputs) {
        this.id = Math.abs(new Random().nextInt()-1)+1;
        this.name = name;
        this.type = type;
        this.summary = summary == null ? "" : summary;
        this.nodes = MiscMath.toBooleanArray(node_str.replace("t", "true ").replace("f", "false "));
        this.conns = new int[NODE_COUNT][2];
        this.inputs = inputs != null ? inputs : new Object[0][3];
        this.outputs = outputs != null ? outputs : new Object[0][3];
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
        boolean accept = false;
        if (from == NODE_OUT || from == NODE_YES || from == NODE_NO) accept = to == NODE_IN;
        if (accept) { conns[from] = new int[]{b.getID(), to}; return true; }
        return false;
    }
    
    public boolean disconnect(int node_index) {
        if (node_index < 0 || node_index >= nodes.length) return false;
        conns[node_index] = new int[]{0, 0};
        return true;
    }
    
    public void move(double x, double y) {
        this.x += x; this.y += y;
    }
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("b\n");
            bw.write("t="+type+"\n");
            bw.write("id="+id+"\n");
            bw.write("x="+x+"\n");
            bw.write("y="+y+"\n");
            String c = ""; for (int[] conn: conns) c += conn[0]+" "+conn[1]+" ";
            bw.write("conns="+c+"\n");
            c = "";
            for (Object[] input: inputs) c += "{"+(String)input[2]+"}";
            bw.write("inputs="+c+"\n");
            c = ""; for (Object[] output: outputs) c += "{"+(String)output[2]+"}";
            bw.write("outputs="+c+"\n");
            bw.write("/b\n");
        } catch (IOException ex) {
            ex.printStackTrace();
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
                if (line.indexOf("t=") == 0) { 
                    Block template = Assets.getBlock(line.substring(2));
                    if (template != null) {
                        template.copyTo(this, false);
                        System.out.println("Block: "+name+" ("+type+")");
                    }
                }
                if (line.indexOf("id=") == 0) id = Integer.parseInt(line.trim().replace("id=", ""));
                if (line.indexOf("x=") == 0) x = Integer.parseInt(line.trim().replace("x=", ""));
                if (line.indexOf("y=") == 0) y = Integer.parseInt(line.trim().replace("y=", ""));
                if (line.indexOf("conns=") == 0) {
                    String[] conns_list = line.substring(6).split(" ");
                    for (int i = 0; i < conns.length; i++) {
                        conns[i][0] = Integer.parseInt(conns_list[i*2]);
                        conns[i][1] = Integer.parseInt(conns_list[(i*2)+1]);
                    }
                }
                if (line.indexOf("inputs=") == 0) {
                    String[] inputs_list = line.substring(7).split("\\}\\{");
                    for (int i = 0; i < inputs_list.length; i++) {
                        if (i >= inputs.length) break;
                        inputs[i][2] = inputs_list[i].replaceAll("[{}]", "");
                    }
                }
                if (line.indexOf("outputs=") == 0) {
                    String[] outputs_list = line.substring(8).split("\\}\\{");
                    for (int i = 0; i < outputs_list.length; i++) {
                        if (i >= outputs.length) break;
                        outputs[i][2] = outputs_list[i].replaceAll("[{}]", "");
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
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
        for (int i = 0; i < NODE_COUNT; i++) {
            if (i < nodes.length) if (!nodes[i]) continue;
            int[] rc = getRenderCoords();
            int[] offset = getNodeOffset(i);
            if (MiscMath.pointIntersects(x, y, rc[0]+offset[0], rc[1]+offset[1], 20, 20)) return i;
        }
        return -1;
    }
    
    public String getType() { return type; }
    
    public String getSummary() {
        String s = summary+"";
        for (Object[] input: inputs) {
            String val = (String)input[2];
            val = val.trim().length() == 0 ? "?" : val;
            s = s.replaceAll("@"+(String)input[0], val);
        }
        for (Object[] output: outputs) {
            String val = (String)output[2];
            val = val.trim().length() == 0 ? "?" : val;
            s = s.replaceAll("@"+(String)output[0], val);
        }
        return s;
    }
    
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
    
    public int getID() { return id; }
    
    public int[] getCoords() { return new int[]{x, y}; }
    
    public String getTitle() { return name; }
    
    public int inputCount() { return inputs.length; }
    public int outputCount() { return outputs.length; }
    
    public Object[] getInput(int index) {
        return inputs[index];
    }
    
    public Object[] getOutput(int index) {
        return outputs[index];
    }
    
    public int[] dimensions() {
        int b_width = (title_width > summary_width ? title_width : summary_width) + 10, 
                b_height = 40;
        if (b_width < 75) b_width = 75;
        return new int[]{b_width, b_height};
    }
    
    public void randomID() {
        this.id = Math.abs(new Random().nextInt());
    }
    
    public void copyTo(Block b, boolean copy_id) {
        b.name = name;
        b.type = type;
        b.summary = summary;
        if (copy_id) b.id = id;
        b.x = x; b.y = y;
        b.nodes = new boolean[nodes.length];
        b.conns = new int[conns.length][2];
        b.inputs = new Object[inputs.length][3];
        b.outputs = new Object[outputs.length][3];
        System.arraycopy(nodes, 0, b.nodes, 0, nodes.length);
        for (int i = 0; i < conns.length; i++) b.conns[i] = new int[]{conns[i][0], conns[i][1]};
        for (int i = 0; i < b.inputs.length; i++) b.inputs[i] = 
                new Object[]{inputs[i][0], inputs[i][1], inputs[i][2]};
        for (int i = 0; i < b.outputs.length; i++) b.outputs[i] = 
                new Object[]{outputs[i][0], outputs[i][1], outputs[i][2]};
    }
    
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        
        int[] dims = dimensions();
        
        int[] rc = getRenderCoords();
        
        Color[] b_colors = {Color.lightGray, Color.blue, Color.green, Color.red};
        for (int i = 0; i < nodes.length; i++) {
            if (!nodes[i]) continue;
            int[] offset = getNodeOffset(i);
            
            Color line_color = b_colors[i > 4 ? 4 : i];
            g2.setColor(line_color);
            g2.fillRect(rc[0] + offset[0], rc[1] + offset[1], 20, 20);
            g2.setColor(line_color.darker());
            g2.drawRect(rc[0] + offset[0], rc[1] + offset[1], 20, 20);
            
            int[] conn = conns[i];
            
            Block b_conn = parent.getBlockByID(conn[0]);
            if (b_conn == null) continue;
            int[] brc = b_conn.getRenderCoords();
            int[] bno = b_conn.getNodeOffset(conn[1]);

            int[] line = new int[]{rc[0]+offset[0]+10, rc[1]+offset[1]+10, brc[0]+bno[0]+10, brc[1]+bno[1]+10};

            g2.setStroke(new BasicStroke(4));
            g2.setColor(Color.black);
            g2.drawLine(line[0], line[1], line[2], line[3]);
            g2.setStroke(new BasicStroke(2));
            g2.setColor(line_color);
            g2.drawLine(line[0], line[1], line[2], line[3]);
            
        }
        
        g2.setColor(Color.white);
        g2.fillRect(rc[0], rc[1], dims[0], dims[1]);
        g2.setColor(Color.black);
        g2.drawRect(rc[0], rc[1], dims[0], dims[1]);
        g2.setFont(font);
        title_width = (int)g2.getFontMetrics().getStringBounds(name, g2).getWidth();
        g2.drawString(getTitle(), rc[0] + 5, rc[1] + 15);
        g2.setFont(font.deriveFont(Font.PLAIN));
        summary_width = (int)g2.getFontMetrics().getStringBounds(getSummary(), g2).getWidth();
        g2.drawString(getSummary(), rc[0] + 5, rc[1] + 30);
    }
    
    public int[] getRenderCoords() { 
        return GUI.getFlowCanvas().getOnscreenCoords(MiscMath.round(x, 32)-(dimensions()[0]/2), MiscMath.round(y, 32)-(dimensions()[1]/2)); 
    }
    
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
        return new int[]{0, 0};
    }
    
    @Override
    public String toString() {
        return getTitle()+" ("+getType()+")";
    }
    
    protected void clean() {
        for (int[] conn: conns) if (parent.getBlockByID(conn[0]) == null) { conn[0] = 0; conn[1] = 0; }
    }
    
}