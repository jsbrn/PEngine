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
        /*ArrayList<Block> list = new ArrayList<Block>();
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("/assets/blocks.txt");
        if (in == null) return;
        System.out.println("Loading from file: " + f.getAbsoluteFile().getAbsolutePath());
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                line = line.trim();
                String block[] = line.split("\t");
                Block b = new Block(block[0], block[1], block[2], block[3]);
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        blocks = new Block[10];
        blocks[0] = new Block("Set coordinates", "(@x, @y)", "sp", "ttff", 
                new Object[][]{{"x", Types.NUMBER, ""}, {"y", Types.NUMBER, ""}}, new Object[][]{});
        blocks[1] = new Block("Get coordinates", "@x = X, @y = Y", "gxp", "ttff", 
                new Object[][]{}, new Object[][]{{"x", Types.NUMBER, ""}, {"y", Types.NUMBER, ""}});
        blocks[2] = new Block("Start", "@id", "s", "ftff", 
                new Object[][]{{"id", Types.TEXT, ""}}, new Object[][]{});
        blocks[3] = new Block("Equal?", "If @x == @y", "se", "tftt", 
                new Object[][]{{"x", Types.ANY, ""}, {"y", Types.ANY, ""}}, new Object[][]{});
        blocks[4] = new Block("Wait", "Until @x == @y", "wxy", "ttff", 
                new Object[][]{{"x", Types.TEXT, ""}, {"y", Types.TEXT, ""}}, new Object[][]{});
        blocks[5] = new Block("Set animation", "@anim", "sa", "ttff", 
                new Object[][]{{"anim", Types.ANIM, ""}}, new Object[][]{});
        blocks[6] = new Block("Switch to level", "@level", "stl", "ttff", 
                new Object[][]{{"level", Types.LEVEL, ""}}, new Object[][]{});
        blocks[7] = new Block("Delete object", "@obj", "dso", "ttff", 
                new Object[][]{{"obj", Types.OBJECT, ""}}, new Object[][]{});
        blocks[8] = new Block("Load asset", "@ass", "la", "ttff", 
                new Object[][]{{"ass", Types.ASSET, ""}}, new Object[][]{});
        blocks[9] = new Block("Start flow", "@flow", "sf", "ttff", 
                new Object[][]{{"flow", Types.FLOW, ""}}, new Object[][]{});
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
