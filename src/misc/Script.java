package misc;

import java.util.ArrayList;
import scene.Scene;
import scene.SceneObject;

public class Script {
    
    public String NAME;
    public ArrayList<String> CONTENTS;
    static CommandList COMMAND_LIST;
    
    Level parent_level = null;
    SceneObject parent_object = null;
    
    public Script() {
        this.NAME = "new_script";
        this.CONTENTS = new ArrayList<String>();
        //initialize the command list
        if (COMMAND_LIST == null) {
            COMMAND_LIST = new CommandList();
        }
    }
    
    public ArrayList<String> getErrors() {
        ArrayList<String> list = new ArrayList<String>();
        //TEST IF COMMANDS AND PARAMETRES ARE VALID. YOU'LL NEED TO FINISH THE DATABASE OF SCRIPTING COMMANDS FIRST.
        for (String s: CONTENTS) {
            String cmd = this.getCommandName(s);
            ArrayList<String> params = this.getParametres(s);
            Command c = COMMAND_LIST.get(cmd, params.size());
            if (c == null) {
                list.add("        Command '"+cmd+"' is not a valid command.");
                continue;
            }
            /*for (int i = 0; i != params.size(); i++) {
                String p = params.get(i);
                if (c.type(i).equals("script")) {
                    if (!isLevelScript()) {
                        if (!parent_object.scriptExists(p)) list.add("        Command '"+cmd+"' references a script that does not exist: "+p);
                    }
                }
            }*/
        }
        return list;
    }
    
    public boolean equalTo(Script s) {
        if (!NAME.equals(s.NAME)) return false;
        if (!Scene.mergeStrings(CONTENTS).equals(Scene.mergeStrings(s.CONTENTS))) return false;
        return true;
    }
    
    public void setParent(Level l) {
        parent_level = l;
        parent_object = null;
    }
    
    public void setParent(SceneObject o) {
        parent_object = o;
        parent_level = null;
    }
    
    /**
     * Checks all levels and their scripts (ignoring objects) to find this script.
     * @return True if the script is found. False otherwise.
     */
    public boolean isLevelScript() {
        return (parent_level != null && parent_object == null);
    }
    
    public boolean isObjectScript() {
        return !isLevelScript();
    }
    
    public void copyTo(Script new_s) {
        new_s.NAME = NAME; 
        if (isLevelScript()) new_s.setParent(parent_level); else new_s.setParent(parent_object);
        new_s.setContent(Scene.mergeStrings(CONTENTS));
    }
    
    public String getCommandName(String line) {
        int lb_index = line.indexOf("[");if (lb_index <= 0 || line.length() < 2) { return ""; }
        String inside_brackets = line.substring(lb_index+1, line.length()-1);
        String cmd_name = line.replace("["+inside_brackets+"]", "");
        return cmd_name;
    }
    
    public ArrayList<String> getParametres(String line) {
        ArrayList<String> params = new ArrayList<String>();
        int lb_index = line.indexOf("[");if (lb_index <= 0 || line.length() < 2) { return params; }
        String inside_brackets = line.substring(lb_index+1, line.length()-1);
        params = Scene.parseString(inside_brackets.replace("|", "\n")); //get the entire list of parametres
        return params;
    }
    
    public void rename(String new_name, boolean refactor) {
        String old_name = this.NAME;
        this.NAME = new_name;
        /**if (!refactor) return;
        if (isLevelScript()) {
            System.out.println("Searching parent level for scripts with matching params");
            for (Script s: parent_level.SCRIPTS) {
                System.out.println("Renaming parametres in level"+parent_level.NAME+", script "+s.NAME);
                s.renameParametres("runlevelscript", "script", old_name, new_name);
            }
            for (SceneObject o: parent_level.ALL_OBJECTS) {
                for (Script s: o.SCRIPTS) {
                    System.out.println("Renaming parametres in object "+o.NAME+", script "+s.NAME);
                    s.renameParametres("runlevelscript", "script", old_name, new_name);
                }
            }
        } else {
            //find the scope (aka all scripts in all objects in the level containing the parent,
            //plus all of its level scripts)
            ArrayList<Script> scope = new ArrayList<Script>();
            for (Level l: Scene.LEVELS) {
                if (!l.ALL_OBJECTS.contains(parent_object)) continue;
                for (Script s: l.SCRIPTS) {
                    scope.add(s);
                }
                for (SceneObject o: l.ALL_OBJECTS) {
                    for (Script s: o.SCRIPTS) {
                        scope.add(s);
                    }
                }
            }
            //iterate the scope
            /*for (Script s: scope) {
                for (int i = 0; i != s.CONTENTS.size(); i++) {
                    if (i >= CONTENTS.size()) { System.out.println(s.NAME+" has "+s.CONTENTS.size()+" lines.");break; }
                    String line = CONTENTS.get(i);
                    String cmd_name = this.getCommandName(line);
                    if (cmd_name.equals("runobjectscript")) {
                        ArrayList<String> parametres = this.getParametres(line);
                        int param_count = parametres.size();
                        Command c = COMMAND_LIST.get(cmd_name, param_count);
                        if (c != null) {
                            System.out.println("Commanf found: "+c.type(0)+", "+parametres.get(0));
                            if (parametres.get(0).equals(parent_object.NAME)) {
                                s.renameParametres(i, "script", old_name, new_name);
                            }
                        }
                    }
                }
            }
            //now only do the references within the parent object (ignore runobjectscript/runlevelscript)
            for (Script s: parent_object.SCRIPTS) {
                for (int i = 0; i != s.CONTENTS.size(); i++) {
                    if (i >= CONTENTS.size()) { System.out.println(s.NAME+" has "+s.CONTENTS.size()+" lines.");break; } else { System.out.println("Iterating "+i);}
                    String line = CONTENTS.get(i);
                    String cmd_name = this.getCommandName(line);
                    if (cmd_name.equals("runobjectscript") || cmd_name.equals("runlevelscript")) continue;
                    System.out.println("cmd_name is "+cmd_name);
                    ArrayList<String> parametres = this.getParametres(line);
                    int param_count = parametres.size();
                    System.out.println("parametres are: "+parametres.toString());
                    Command c = COMMAND_LIST.get(cmd_name, param_count);
                    if (c != null) {
                        System.out.println("Commanf found: "+c.type(0)+", "+parametres.get(0));
                        s.renameParametres(i, "script", old_name, new_name);
                    }
                    
                }
            }
        }*/
    }
    
    /**
     * Renames all valid parametres in all valid commands. When command is null, all commands are valid.
     * @param command The command to affect.
     * @param param_type The type of parametre to look for.
     * @param old_param Old name.
     * @param new_param New name.
     */
    public void renameParametres(String command, String param_type, String old_param, String new_param) {
        for (int cindex = 0; cindex != CONTENTS.size(); cindex++) {
            String s = CONTENTS.get(cindex);
            String cmd_name = this.getCommandName(s);
            ArrayList<String> parametres = this.getParametres(s);
            int param_count = parametres.size();
            if (command != null) { if (!cmd_name.equals(command)) continue; }
            Command c = COMMAND_LIST.get(cmd_name, param_count);
            if (c != null) {
                System.out.println("Command is valid, renaming "+cmd_name+" at index "+cindex);
                renameParametres(cindex, param_type, old_param, new_param);
            }
        }
    }
    
    public void renameParametres(int command_index, String param_type, String old_param, String new_param) {
        System.out.println("renameParametres "+command_index+" "+param_type+" "+old_param+" "+new_param);
        String s = CONTENTS.get(command_index);
        String new_s = "";
        ArrayList<String> params = this.getParametres(s);
        String cmd_name = this.getCommandName(s);
        new_s+=cmd_name+"[";
        Command c = COMMAND_LIST.get(cmd_name, params.size());
        if (c != null) {
            for (int i = 0; i != params.size(); i++) {
                System.out.println(c.type(i)+", "+params.get(i));
                if (c.type(i).equals(param_type) && params.get(i).equals(old_param)) {
                    params.set(i, new_param);
                }
                new_s+=params.get(i); if (i < c.paramCount() - 1) new_s+="|";
            }
            new_s+="]";
            System.out.println("new_s = "+new_s);
            CONTENTS.set(command_index, new_s);
        }
    }
    
    public void setContent(String s) {
        CONTENTS = Scene.parseString(s);
    }
    
}

class CommandList {
    ArrayList<Command> list;
    
    public CommandList() {
        list = new ArrayList<Command>();
        /**
         * YOU CANNOT HAVE MORE THAN ONE COMMAND WITH THE SAME NAME AND PARAMETRE COUNT
         * OR THE PROGRAM WILL NOT BE ABLE TO TELL THE TWO APART. IT'S EASIER TO JUST
         * WORK AROUND THIS FUNDEMENTAL FLAW THAN TO FIX IT.
         */
        
        //these are all the global commands
        list.add(new Command("setcampos", new String[]{"value", "value"}, true));
        list.add(new Command("setcamtarget", new String[]{"value", "value"}, true));
        list.add(new Command("setcamtarget", new String[]{"object"}, true));
        list.add(new Command("setcamspeed", new String[]{"value"}, true));
        list.add(new Command("setmusic", new String[]{"value"}, true));
        list.add(new Command("setambientsound", new String[]{"value"}, true));
        list.add(new Command("wait", new String[]{"value"}, true));
        list.add(new Command("enterlevel", new String[]{"level"}, true));
        list.add(new Command("enterlevel", new String[]{"level", "object"}, true));
        list.add(new Command("delete", new String[]{"object"}, true));
        list.add(new Command("runlevelscript", new String[]{"script", "value"}, true));
        list.add(new Command("runlevelscript", new String[]{"script"}, true));
        list.add(new Command("runobjectscript", new String[]{"object", "script", "value"}, true));
        list.add(new Command("runobjectscript", new String[]{"object", "script"}, true));
        list.add(new Command("loop", new String[]{}, true));
        list.add(new Command("print", new String[]{"value"}, true));
        
        //these are the commands that require a parent object to work with
        list.add(new Command("jump", new String[]{}, false));
        list.add(new Command("dialogue", new String[]{"dialogue"}, false));
        list.add(new Command("say", new String[]{"value"}, false));
        list.add(new Command("setanim", new String[]{"animation"}, false));
        list.add(new Command("move", new String[]{"value", "value"}, false));
        list.add(new Command("moveto", new String[]{"value", "value"}, false));
        list.add(new Command("relativemoveto", new String[]{"value", "value"}, false));
        list.add(new Command("runscript", new String[]{"script", "value"}, false));
        list.add(new Command("runscript", new String[]{"script"}, false));
    }
    
    public Command get(String name, int param_count) {
        for (Command c: list) { if (c.name().equals(name) 
                && c.paramCount() == param_count) return c; }
        return null;
    }
    
}

class Command {
    private String name;
    private String[] param_types;
    boolean global = false;
    
    /*LIST OF VALID TYPES
     * animation
     * dialogue
     * object
     * script
     * level
     * value (not a reference to anything, just a value)
     */
    
    public Command (String name, String[] types, boolean global) {
        this.name = name;
        this.param_types = types;
    }
    
    public int paramCount() {
        return param_types.length;
    }
    
    public String name() {
        return name;
    }
    
    public String type(int index) {
        if (index > -1 && index < param_types.length) return param_types[index];
        return "";
    }
    
}
