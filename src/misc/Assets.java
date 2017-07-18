package misc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import project.Project;
import project.objects.components.Block;

public class Assets {
    
    public static String USER_HOME;
    private static HashMap asset_map;
    
    private static Block[] blocks;
    
    public static int size() { return blocks.length; }
    public static Block getBlock(int index) {
        if (index > -1 && index < blocks.length) return blocks[index];
        return null;
    }
    public static Block getBlock(String type) {
        for (Block b: blocks)
            if (b.getType().equals(type)) return b;
        return null;
    }
    public static Block[] getBlocks() { return blocks; }
    
    /**
     * Loads all assets from the project's assets folder. Clears all previously loaded assets
     * first. Should be called on every new project load.
     */
    public static void load() {
        if (asset_map == null) asset_map = new HashMap();
        asset_map.clear();
        File assets = new File(Project.getProject().getDirectory()+"/assets");
        addToAssets(assets);
        initBlockList();
    }
    
    public static void delete(File asset) {
        if (asset.isDirectory()) {
            File[] list = asset.listFiles();
            for (File f: list) {
                if (f.isDirectory()) delete(f); else f.delete();
            }
        }
        asset.delete();
    }
    
    public static int assetCount() { return asset_map.size(); }
    
    public static void mkdirs() {
        USER_HOME = System.getProperty("user.home");
        new File(Assets.USER_HOME+"/.pengine/").mkdir();
        new File(Assets.USER_HOME+"/.pengine/jars").mkdir();
        new File(Assets.USER_HOME+"/.pengine/projects").mkdir();
    }
    
    private static void initBlockList() {   
        blocks = new Block[39];
        blocks[0] = new Block("Start", "@id", "s", "ftff", 
                new Object[][]{{"id", Types.TEXT, ""}}, null);
        blocks[1] = new Block("Wait", "@duration", "w", "ttff", 
                new Object[][]{{"duration", Types.NUMBER, ""}}, null);
        blocks[2] = new Block("Print to console", "@message", "p", "ttff", 
                new Object[][]{{"message", Types.ANY, ""}}, null);
        blocks[3] = new Block("Switch to level", "@level", "stl", "ttff", 
                new Object[][]{{"level", Types.LEVEL, ""}}, null);
        
        blocks[4] = new Block("Add", "@a + @b", "adn", "ttff", 
                new Object[][]{{"a", Types.NUMBER, ""}, {"b", Types.NUMBER, ""}}, new Object[][]{{"result", Types.NUMBER, ""}});
        blocks[5] = new Block("Multiply", "@a * @b", "mun", "ttff", 
                new Object[][]{{"a", Types.NUMBER, ""}, {"b", Types.NUMBER, ""}}, new Object[][]{{"result", Types.NUMBER, ""}});
        blocks[6] = new Block("Divide", "@a / @b", "din", "ttff", 
                new Object[][]{{"a", Types.NUMBER, ""}, {"number2", Types.NUMBER, ""}}, new Object[][]{{"result", Types.NUMBER, ""}});
        blocks[7] = new Block("Power", "@a ^ @b", "pon", "ttff", 
                new Object[][]{{"a", Types.NUMBER, ""}, {"b", Types.NUMBER, ""}}, new Object[][]{{"result", Types.NUMBER, ""}});
        blocks[8] = new Block("Random number", "From 0 to @max", "rnm", "ttff", 
                new Object[][]{{"max", Types.NUMBER, ""}}, new Object[][]{{"result", Types.NUMBER, ""}});
        blocks[9] = new Block("Random number", "From @min to @max", "rnmm", "ttff", 
                new Object[][]{{"min", Types.NUMBER, ""}, {"max", Types.NUMBER, ""}}, new Object[][]{{"result", Types.NUMBER, ""}});
        blocks[10] = new Block("Concatenate", "@a + @b", "conc", "ttff", 
                new Object[][]{{"a", Types.TEXT, ""}, {"b", Types.TEXT, ""}}, new Object[][]{{"result", Types.TEXT, ""}});
        
        blocks[11] = new Block("Set variable", "Set @variable in @flow to @value", "sv", "ttff", 
                new Object[][]{{"flow", Types.FLOW, ""}, {"variable", Types.TEXT, ""}, {"value", Types.ANY, ""}}, null);
        blocks[12] = new Block("Get variable", "Get value of @variable in @flow", "gv", "ttff", 
                new Object[][]{{"variable", Types.TEXT, ""}, {"variable", Types.TEXT, ""}}, new Object[][]{{"value", Types.ANY, ""}});
        
        blocks[13] = new Block("OR Gate", "@a OR @b", "or", "tftt", 
                new Object[][]{{"a", Types.BOOLEAN, ""}, {"b", Types.BOOLEAN, ""}}, new Object[][]{{"result", Types.BOOLEAN, ""}});
        blocks[14] = new Block("AND Gate", "@a AND @b", "and", "tftt", 
                new Object[][]{{"a", Types.BOOLEAN, ""}, {"b", Types.BOOLEAN, ""}}, new Object[][]{{"result", Types.BOOLEAN, ""}});
        blocks[15] = new Block("NOR Gate", "NOT @a", "not", "tftt", 
                new Object[][]{{"a", Types.BOOLEAN, ""}}, new Object[][]{{"result", Types.BOOLEAN, ""}});
        
        blocks[16] = new Block("Set level background", "to @top, @bottom in @time (ms)ms", "slb", "ttff", 
                new Object[][]{{"top", Types.NUMBER_LIST, ""}, {"bottom", Types.NUMBER_LIST, ""}, {"time (ms)", Types.NUMBER, ""}}, null);
        blocks[17] = new Block("Get camera spawn", "@level", "gcs", "ttff", 
                new Object[][]{{"level", Types.LEVEL, ""}}, new Object[][]{{"x", Types.NUMBER, ""}, {"y", Types.NUMBER, ""}});
        blocks[18] = new Block("Get player spawn", "@level", "gps", "ttff", 
                new Object[][]{{"level", Types.LEVEL, ""}}, new Object[][]{{"x", Types.NUMBER, ""}, {"y", Types.NUMBER, ""}});
        blocks[19] = new Block("Set camera position", "(@x, @y)", "scp", "ttff", 
                new Object[][]{{"object", Types.OBJECT, ""}, {"x", Types.NUMBER, ""}, {"y", Types.NUMBER, ""}}, null);
        blocks[20] = new Block("Set camera target", "(@x, @y)", "sctxy", "ttff", 
                new Object[][]{{"x", Types.NUMBER, ""}, {"y", Types.NUMBER, ""}}, null);
        blocks[21] = new Block("Set camera target", "@object", "scto", "ttff", 
                new Object[][]{{"object", Types.OBJECT, ""}}, null);
        blocks[22] = new Block("Set camera zoom", "@zoomx zoom", "scz", "ttff", 
                new Object[][]{{"zoom", Types.NUMBER, ""}}, null);
        blocks[23] = new Block("Remove object", "Remove @object from game", "ro", "ttff", 
                new Object[][]{{"object", Types.OBJECT, "Object()"}}, null);
        
        blocks[24] = new Block("Get object position", "@object", "gop", "ttff", 
                new Object[][]{{"object", Types.OBJECT, ""}}, new Object[][]{{"x", Types.NUMBER, ""}, {"y", Types.NUMBER, ""}});
        blocks[25] = new Block("Set object position", "@object to (@x, @y)", "sop", "ttff", 
                new Object[][]{{"object", Types.OBJECT, ""}, {"x", Types.NUMBER, ""}, {"y", Types.NUMBER, ""}}, null);
        blocks[26] = new Block("Anchor to", "@object anchors to @object2", "anch", "ttff", 
                new Object[][]{{"object", Types.OBJECT, ""}, {"object2", Types.OBJECT, ""}}, null);
        
        blocks[27] = new Block("Add force", "Add force @name to @object: @angle degrees, @magnitude px/s", "af", "ttff", 
                new Object[][]{{"object", Types.OBJECT, "Object()"}, {"name", Types.TEXT, ""}, 
                    {"angle", Types.NUMBER, "0"}, {"acceleration", Types.NUMBER, "0"}, {"magnitude", Types.NUMBER, "0"}}, null);
        blocks[28] = new Block("Remove force", "Remove force @name from @object", "rf", "ttff", 
                new Object[][]{{"object", Types.OBJECT, "Object()"}, {"name", Types.TEXT, ""}}, null);
        blocks[29] = new Block("Set animation", "Set @object animation to @animation", "sa", "ttff", 
                new Object[][]{{"object", Types.OBJECT, "Object()"}, {"animation", Types.ANIM, ""}}, null);
        blocks[30] = new Block("Start flow", "@flow at @start_id", "stf", "ttff", 
                new Object[][]{{"flow", Types.FLOW, ""}, {"start_id", Types.TEXT, ""}}, null);
        blocks[31] = new Block("Stop flow", "Set @object animation to @animation", "spf", "ttff", 
                new Object[][]{{"flow", Types.FLOW, "Flow()"}}, null);
        
        blocks[32] = new Block("Is key pressed", "@key", "ikp", "tftt", 
                new Object[][]{{"key", Types.TEXT, ""}}, null);
        blocks[33] = new Block("Await key press", "@key", "akp", "ttff", 
                new Object[][]{{"key", Types.TEXT, ""}}, null);
        blocks[34] = new Block("Await key release", "@key", "akr", "ttff", 
                new Object[][]{{"key", Types.TEXT, ""}}, null);
        
        blocks[35] = new Block("Await collision", "Between @object1 & @object2", "ac", "ttff", 
                new Object[][]{{"object1", Types.OBJECT, ""}, {"object2", Types.OBJECT, ""}}, null);
        blocks[36] = new Block("If objects intersect", "@object1 & @object2", "ioac", "tftt", 
                new Object[][]{{"object1", Types.OBJECT, ""}, {"object2", Types.OBJECT, ""}}, null);
       
        blocks[37] = new Block("Say", "@object says: @message", "say", "ttff", 
                new Object[][]{{"object", Types.OBJECT, "Object()"}, {"message", Types.TEXT, ""},
                    {"lifespan (mills)", Types.NUMBER, "1000"}, {"require keypress", Types.BOOLEAN, "false"}}, null);
        blocks[38] = new Block("Await player choice", "@choices", "apc", "ttff", 
                new Object[][]{{"choices", Types.TEXT_LIST, "[\"Choice 1\"][\"Choice 2\"][\"Choice 3\"]"}}, 
                new Object[][]{{"choice", Types.NUMBER, ""}});
    }
    
    public static Object get(String key) {
        key = key.replaceAll("[/\\\\]", File.separator);
        if (!asset_map.containsKey(key)) return null;
        return asset_map.get(key);
    }
    
    private static void addToAssets(File assets) {
        if (assets.isDirectory()) {
            File[] files = assets.listFiles();
            for (File f: files) {
                if (f.isDirectory()) {
                    addToAssets(f);
                } else {
                    if (f.getName().contains(".png")) {
                        BufferedImage img;
                        try {
                            img = ImageIO.read(f);
                            String key = f.getAbsolutePath().replace(assets.getAbsolutePath()+File.separator, "")
                                    .replaceAll("[/\\\\]", File.separator); //sanitize
                            asset_map.put(key, img);
                            System.out.println("Loaded asset "+key+"!");
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, "Error loading assets!\n"+e.getLocalizedMessage());
                            break;
                        }
                    }
                }
            }
        }
    }

}
