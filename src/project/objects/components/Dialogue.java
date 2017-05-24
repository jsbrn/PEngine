package project.objects.components;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import project.objects.SceneObject;

public class Dialogue {
    private String name = "";
    /*Queue types: Speak (this object), Speak (other object), Choice*/
    /*Choice types: Script, Dialogue, Speech (the other entity, not the player*/
    //Choice layout: {choice, trigger, trigger_type}
    private ArrayList<String> queue, speech;
    private ArrayList<String[]> choices;
    private ArrayList<Boolean> wait;
    
    public static int SPEECH_TRIGGER = 0, SCRIPT_TRIGGER = 1, DIALOGUE_TRIGGER = 2, THIS_SPEAK = 3, OTHER_SPEAK = 4,
            PLAYER_CHOICE = 5;
    
    private boolean locked = false;
    
    private String link_to;
    private int link_to_type;
    
    private SceneObject parent_object = null;
    
    public Dialogue() {
        queue = new ArrayList<String>();
        speech = new ArrayList<String>();
        choices = new ArrayList<String[]>();
        wait = new ArrayList<Boolean>();
        link_to = "";
        link_to_type = -1;
    }

    public ArrayList<String> getQueue() {
        return queue;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void addEvent(int evt_type) {
        queue.add(evt_type+"");
        speech.add("");
        choices.add(new String[]{"", "", ""});
        wait.add(false);
    }
    
    public void removeEvent(int index) {
        if (index <= -1 || index >= queue.size()) return;
        queue.remove(index);
        speech.remove(index);
        choices.remove(index);
        wait.remove(index);
    }
    
    public void setParent(SceneObject o) {
        parent_object = o;
    }
    
    public void save(BufferedWriter bw) {
        System.err.print("Dialogue saving / loading not implemented.");
    }
    
    
    public boolean equalTo(Dialogue d) {
        if (!name.equals(d.name)) return false;
        if (queue.size() != d.queue.size()) return false;
        if (link_to_type != d.link_to_type) return false;
        if (!link_to.equals(link_to));
        for (int i = 0; i != queue.size(); i++) {
            if (!queue.get(i).equals(d.queue.get(i))) return false;
        }
        //Player responses and event lists should be the same size, always.
        for (int i = 0; i != queue.size(); i++) {
            if (!speech.get(i).equals(d.speech.get(i))) return false;
            if (wait.get(i) != d.wait.get(i)) return false;
            for (int ii = 0; ii != 3; ii++) {
                if (!choices.get(i)[ii].equals(d.choices.get(i)[ii])) return false;
            }
        }
        return true;
    }
    
    public void copyTo(Dialogue new_d) {
        new_d.name = name;
        new_d.link_to = link_to;
        new_d.link_to_type = link_to_type;
        new_d.choices.clear();
        new_d.queue.clear();
        new_d.speech.clear();
        new_d.queue.addAll(queue);
        new_d.speech.addAll(speech);
        new_d.wait.addAll(wait);
        for (int i = 0; i != queue.size(); i++) {
            for (int ii = 0; ii != 3; ii++) {
                new_d.choices.set(i, new String[]{choices.get(i)[0], choices.get(i)[1], choices.get(i)[2]});
            }
        }
    }
    
}
