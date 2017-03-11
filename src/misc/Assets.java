package misc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import project.objects.components.Block;

public class Assets {
    
    public static String PROJECT_NAME = "", USER_HOME;
    public static ArrayList<String> OBJECT_TEXTURE_NAMES = new ArrayList<String>(),
            ANIMATION_TEXTURE_NAMES = new ArrayList<String>();
    public static ArrayList<BufferedImage> OBJECT_TEXTURES = new ArrayList<BufferedImage>(),
            ANIMATION_TEXTURES = new ArrayList<BufferedImage>();
    
    private static Block[] blocks;
    
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
    
    /**
     * Loads all assets from the project's 'objects' folder, into the OBJECT_TEXTURES list. Clears all previously loaded assets
     * first. Uses Scene.PROJECT_NAME in the directory.
     */
    public void load() {
        File object_textures = new File(USER_HOME+"/level_editor/projects/"+PROJECT_NAME+"/assets/textures/objects");
        File anim_textures = new File(USER_HOME+"/level_editor/projects/"+PROJECT_NAME+"/assets/textures/animations");
        OBJECT_TEXTURES.clear();
        OBJECT_TEXTURE_NAMES.clear();
        ANIMATION_TEXTURES.clear();
        ANIMATION_TEXTURE_NAMES.clear();
        addToAssets(object_textures, OBJECT_TEXTURE_NAMES, OBJECT_TEXTURES);
        addToAssets(anim_textures, ANIMATION_TEXTURE_NAMES, ANIMATION_TEXTURES);
        initBlockList();
    }
    
    private static void initBlockList() {   
        Block setcampos = new Block("Set camera position", "set_cam_pos", "Actions", "ttttt", "",
                new String[][]{ {"", "x", "number"}, {"", "y", "number"} });
        blocks = new Block[]{setcampos};
    }
    
    private static void addToAssets(File textures, ArrayList<String> names, ArrayList<BufferedImage> imgs) {
        if (textures.isDirectory()) {
            File[] files = textures.listFiles();
            for (File f: files) {
                if (f.getName().contains(".png")) {
                    BufferedImage img;
                    try {
                        img = ImageIO.read(f);
                        imgs.add(img);
                        names.add(f.getName().replace(".png", ""));
                        System.out.println("Loaded asset "+names.get(names.size()-1)+".png");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Error loading assets!\n"+e.getLocalizedMessage());
                        names.clear();
                        imgs.clear();
                        break;
                    }
                }
            }
        }
    }

}
