package project.objects.components;

import java.util.ArrayList;

class Node {

    private String value, name;
    private int value_type;
    private int[][] incoming, outgoing;
    private Block parent;
    
    public static int INCOMING = 0, OUTGOING = 1;
    
    public Node(int in, int out, String name, int value_type, String default_value) {
        this.incoming = new int[in][2];
        this.outgoing = new int[out][2];
        this.name = name;
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
    
    public int[][] connections(int direction) {
        return (direction == Node.INCOMING ? incoming : outgoing);
    }
    
    public boolean removeConnection(int index, int direction) {
        int[][] list = (direction == Node.INCOMING ? incoming : outgoing);
        if (index < 0 || index >= list.length) return false;
        list[index] = null; return true;
    }
    
    public int addConnection(int block_id, int node_index, int direction) {
        int[][] list = (direction == Node.INCOMING ? incoming : outgoing);
        for (int i = 0; i < list.length; i++)
            if (list[i] == null) { list[i] = new int[]{block_id, node_index}; return i; }
        return -1;
    }
    
    public void copyTo(Node n) {
        n.value = value;
        n.parent = parent;
        n.value_type = value_type;
        n.incoming = new int[incoming.length][2];
        n.outgoing = new int[outgoing.length][2];
        for (int i = 0; i < incoming.length; i++) 
            n.incoming[i] = new int[]{incoming[i][0], incoming[i][1]};
        for (int i = 0; i < outgoing.length; i++) 
            n.outgoing[i] = new int[]{outgoing[i][0], outgoing[i][1]};
    }

}