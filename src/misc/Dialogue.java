package misc;

import java.util.ArrayList;
import scene.Scene;
import scene.SceneObject;

public class Dialogue {
    public String NAME = "";
    /*Queue types: Speak (this object), Speak (other object), Choice*/
    /*Choice types: Script, Dialogue, Speech (the other entity, not the player*/
    //Choice layout: {choice, trigger, trigger_type}
    public ArrayList<String> QUEUE, SPEECH;
    public ArrayList<String[]> CHOICES;
    public ArrayList<Boolean> WAIT;
    
    public static int SPEECH_TRIGGER = 0, SCRIPT_TRIGGER = 1, DIALOGUE_TRIGGER = 2, THIS_SPEAK = 3, OTHER_SPEAK = 4,
            PLAYER_CHOICE = 5;
    
    public String LINK_TO;
    public int LINK_TO_TYPE;
    
    SceneObject parent_object = null;
    
    public Dialogue() {
        QUEUE = new ArrayList<String>();
        SPEECH = new ArrayList<String>();
        CHOICES = new ArrayList<String[]>();
        WAIT = new ArrayList<Boolean>();
        LINK_TO = "";
        LINK_TO_TYPE = -1;
    }
    
    public void addEvent(int evt_type) {
        QUEUE.add(evt_type+"");
        SPEECH.add("");
        CHOICES.add(new String[]{"", "", ""});
        WAIT.add(false);
    }
    
    public void removeEvent(int index) {
        if (index <= -1 || index >= QUEUE.size()) return;
        QUEUE.remove(index);
        SPEECH.remove(index);
        CHOICES.remove(index);
        WAIT.remove(index);
    }
    
    /**
     * Refactors all of the matching instances, only if editing a gallery object.
     * Will only refactor a flow to match new_ if the instance is equal to compare_to.
     * @param new_ The instance to match the others with...
     * @param compare_to ...but only if the instance in question is equal to compare_to.
     */
    public static void refactorAll(Dialogue new_, Dialogue compare_to) {
        if (Scene.OBJECT_GALLERY.contains(Scene.ACTIVE_EDIT_OBJECT)) {
            for (SceneObject o: Scene.getObjectsByType(Scene.ACTIVE_EDIT_OBJECT.CLASS)) {
                for (Dialogue d: o.DIALOGUES) {
                    if (d.equalTo(compare_to)) {
                        new_.copyTo(d);
                    }
                }
            }
        }
    }
    
    public void setParent(SceneObject o) {
        parent_object = o;
    }
    
    public ArrayList<String> getErrors() {
        ArrayList<String> list = new ArrayList<String>();

        return list;
    }
    
    public boolean equalTo(Dialogue d) {
        if (!NAME.equals(d.NAME)) return false;
        if (QUEUE.size() != d.QUEUE.size()) return false;
        if (LINK_TO_TYPE != d.LINK_TO_TYPE) return false;
        if (!LINK_TO.equals(LINK_TO));
        for (int i = 0; i != QUEUE.size(); i++) {
            if (!QUEUE.get(i).equals(d.QUEUE.get(i))) return false;
        }
        //Player responses and event lists should be the same size, always.
        for (int i = 0; i != QUEUE.size(); i++) {
            if (!SPEECH.get(i).equals(d.SPEECH.get(i))) return false;
            if (WAIT.get(i) != d.WAIT.get(i)) return false;
            for (int ii = 0; ii != 3; ii++) {
                if (!CHOICES.get(i)[ii].equals(d.CHOICES.get(i)[ii])) return false;
            }
        }
        return true;
    }
    
    public void copyTo(Dialogue new_d) {
        new_d.NAME = NAME;
        new_d.LINK_TO = LINK_TO;
        new_d.LINK_TO_TYPE = LINK_TO_TYPE;
        new_d.CHOICES.clear();
        new_d.QUEUE.clear();
        new_d.SPEECH.clear();
        new_d.QUEUE.addAll(QUEUE);
        new_d.SPEECH.addAll(SPEECH);
        new_d.WAIT.addAll(WAIT);
        for (int i = 0; i != QUEUE.size(); i++) {
            for (int ii = 0; ii != 3; ii++) {
                new_d.CHOICES.set(i, new String[]{CHOICES.get(i)[0], CHOICES.get(i)[1], CHOICES.get(i)[2]});
            }
        }
    }
    
}
