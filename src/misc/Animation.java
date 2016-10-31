package misc;

import java.io.File;
import java.util.ArrayList;
import scene.Scene;
import scene.SceneObject;

public class Animation {
    
    public ArrayList<Integer> WIDTHS = new ArrayList<Integer>(), HEIGHTS = new ArrayList<Integer>();
    public String NAME = "", SPRITE_NAME = "";
    public boolean LOOP = false;
    public int FRAME_DURATION = 100;
    
    public Animation() {
        
    }
    
    /**
     * Refactors all of the matching instances, only if editing a gallery object.
     * Will only refactor an instance to match new_ if the instance is equal to compare_to.
     * @param new_ The instance to match the others with...
     * @param compare_to ...but only if the instance in question is equal to compare_to.
     */
    public static void refactorAll(Animation new_, Animation compare_to) {
        if (Scene.OBJECT_GALLERY.contains(Scene.ACTIVE_EDIT_OBJECT)) {
            for (SceneObject o: Scene.getObjectsByType(Scene.ACTIVE_EDIT_OBJECT.CLASS)) {
                for (Animation d: o.ANIMATIONS) {
                    if (d.equalTo(compare_to)) {
                        new_.copyTo(d);
                    }
                }
            }
        }
    }
    
    public void removeFrame(int i) {
        WIDTHS.remove(i);
        HEIGHTS.remove(i);
    }
    
    public ArrayList<String> getErrors() {
        ArrayList<String> list = new ArrayList<String>();
        if (new File(Scene.USER_HOME+"/level_editor/"+Scene.PROJECT_NAME+"/assets/textures/animations/"+SPRITE_NAME+".png")
                .exists() == false) {list.add("        Animation "+NAME+"'s sprite texture can not be found in /assets/textures/animations\n");}
        int x = 0;
        for (int i = 0; i != WIDTHS.size(); i++) {
            x+=WIDTHS.get(0);
             /**
             * Create the image file and check its dimensions. You'll need to have an ANIMATIONS texture list in Scene.
             */
        }
        //REMEMBER TO TEST THE HEIGHT AS WELL.
        return list;
    }
    
    public boolean equalTo(Animation a) {
        if (!(NAME.equals(a.NAME))) return false;
        if (!(SPRITE_NAME.equals(a.SPRITE_NAME))) return false;
        if (LOOP != a.LOOP) return false;
        for (int i = 0; i != WIDTHS.size(); i++) {
            if (WIDTHS.get(i) != a.WIDTHS.get(i)) return false;
            if (HEIGHTS.get(i) != a.HEIGHTS.get(i)) return false;
        }
        return true;
    }
    
    public void copyTo(Animation new_a) {
        new_a.NAME = NAME;
        new_a.LOOP = LOOP;
        new_a.SPRITE_NAME = SPRITE_NAME;
        new_a.WIDTHS.addAll(WIDTHS);
        new_a.HEIGHTS.addAll(HEIGHTS);
    }
    
    /**
     * Adds a frame. If any of the parametres are 0, then this method will not work.
     */
    public void addFrame(int width, int height, int duration) {
        if (duration <= 0 || width <= 0 || height <= 0) {
            return;
        }
        WIDTHS.add(width);
        HEIGHTS.add(height);
    }
    
}
