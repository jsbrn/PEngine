package misc;

public class BlockList {
    
    static Block[] blocks;
    
    public static void init() {
        
        Block setcampos = new Block("Set camera position", "set_cam_pos", "Actions", "ttttt", "",
                new String[][]{ {"", "x", "number"}, {"", "y", "number"} });
        
        blocks = new Block[]{setcampos};
        
    }
    
    public static int size() { return blocks.length; }
    
    public static Block getBlock(int index) {
        if (index > -1 && index < blocks.length) return blocks[index];
        return null;
    }
    
    public static Block getBlock(String title, String category) {
        for (Block b: blocks) {
            if (b.title.equals(title) && b.category.contains(category)) return b;
        }
        return null;
    }
    
}
