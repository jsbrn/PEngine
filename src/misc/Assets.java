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
        blocks = new Block[12];
        blocks[0] = new Block("Start", "@id", "s", "ftff", 
                new Object[][]{{"id", Types.TEXT, ""}}, null);
        blocks[1] = new Block("Wait", "@duration", "w", "ttff", 
                new Object[][]{{"duration", Types.NUMBER, ""}}, null);
        blocks[2] = new Block("Print to console", "@message", "p", "ttff", 
                new Object[][]{{"message", Types.TEXT, ""}}, null);
        blocks[3] = new Block("Switch to level", "@level", "stl", "ttff", 
                new Object[][]{{"level", Types.LEVEL, ""}}, null);
        blocks[4] = new Block("Set animation", "Set @object animation to @animation", "sa", "ttff", 
                new Object[][]{{"object", Types.OBJECT, ""}, {"animation", Types.ANIM, ""}}, null);
        blocks[5] = new Block("Add", "@number1 + @number2", "adn", "ttff", 
                new Object[][]{{"number1", Types.NUMBER, ""}, {"number2", Types.NUMBER, ""}}, new Object[][]{{"sum", Types.NUMBER, ""}});
        blocks[6] = new Block("Set variable", "Set @var to @value", "sv", "ttff", 
                new Object[][]{{"value", Types.ANY, ""}}, new Object[][]{{"var", Types.ANY, ""}});
        blocks[7] = new Block("Add force", "Add force @name to @object: @angle degrees, @magnitude px/s", "af", "ttff", 
                new Object[][]{{"object", Types.OBJECT, ""}, {"name", Types.TEXT, ""}, 
                    {"angle", Types.NUMBER, ""}, {"acceleration", Types.NUMBER, ""}, {"magnitude", Types.NUMBER, ""}}, null);
        blocks[8] = new Block("Remove force", "Remove force @name from @object", "rf", "ttff", 
                new Object[][]{{"object", Types.OBJECT, ""}, {"name", Types.TEXT, ""}}, null);
        blocks[9] = new Block("Is key pressed", "@key", "ikp", "tftt", 
                new Object[][]{{"key", Types.TEXT, ""}}, null);
        blocks[10] = new Block("Await key press", "@key", "akp", "ttff", 
                new Object[][]{{"key", Types.TEXT, ""}}, null);
        blocks[11] = new Block("Await key release", "@key", "akr", "ttff", 
                new Object[][]{{"key", Types.TEXT, ""}}, null);
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
