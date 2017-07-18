package misc;

import gui.GUI;
import project.Level;
import project.Project;
import project.objects.SceneObject;
import project.objects.components.Animation;
import project.objects.components.Flow;

public class Types {
    
    public static final int 
        ANY = 0, 
        VARIABLE = 1, 
        NUMBER = 2,
        TEXT = 3, 
        BOOLEAN = 4, 
        ANIM = 5,
        FLOW = 6, 
        OBJECT = 7, 
        LEVEL = 8, 
        ASSET = 9,
        NUMBER_LIST = 10,
        TEXT_LIST = 11,
        BOOLEAN_LIST = 12;
    
    private static final Type[] types = new Type[]{
        new TypeAny(),
        new TypeVar(),
        new TypeNumber(),
        new TypeText(),
        new TypeBoolean(),
        new TypeAnim(),
        new TypeFlow(),
        new TypeObject(),
        new TypeLevel(),
        new TypeAsset(),
        new TypeList(NUMBER),
        new TypeList(TEXT),
        new TypeList(BOOLEAN)
    };
    
    public static Type getType(int type) {
        if (type < 0 || type >= types.length) return null;
        return types[type];
    }
    
    public static int getType(String input) {
        for (int i = 1; i < types.length; i++) if (types[i].typeOf(input)) return i;
        return -1;
    }
    
    public static String getTypeName(int type) {
        if (type < 0 || type >= types.length) return null;
        return types[type].getName();
    }
 
    /**
     * Verifies that the complex type parameters exist.
     * @param input The user input (the complex type).
     * @param type The type.
     * @return A String describing any issues, null if no issues.
     */
    public static String verifyParams(String input, int type) {
        if (type < 0 || type >= types.length) return "Out of bounds: "+type+"!";
        if (!isComplex(type)) return types[type].getName()+" is not a complex type!";
        return ((ComplexType)(types[type])).verifyParams(input);
    }
    
    public static boolean isValidInput(String input, int type) {
        if (type < 0 || type >= types.length) return false;
        return types[type].typeOf(input);
    }
    
    public static boolean isComplex(int type) { 
        if (type < 0 || type >= types.length) return false;
        return types[type] instanceof ComplexType; 
    }
    
}

/**
 * GENERIC TYPES
 */

class Type {
    
    private String name;

    protected void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    /**
     * Returns true if this type is the super type of the value provided.
     * Overridden by the type declarations.
     * @param value
     * @return 
     */
    public boolean typeOf(String value) {
        if (value == null) return false;
        if (value.length() == 0) return false;
        return value.replaceAll("[{}\t\r\n]", "").equals(value);
    }

}

class ComplexType extends Type {
    
    private String params_regex, alias;
    //String params_regex = "([^,]+?|([^,]+[,][^,]+?)|([^,]+[,][^,]+[,][^,]+?))";
    
    public final void setParams(int i, boolean allow_none) {
        i = (int)MiscMath.clamp(i, 1, 3);
        params_regex = "("+"([^,]+?)"
                +(i >= 2 ? "|([^,]+[,][^,]+?)" : "")
                +(i == 3 ? "|([^,]+[,][^,]+[,][^,]+?)" : "")+")"+(allow_none ? "?" : "");
    }
    
    public final String verifyParams(String input) {
        if ("none".equals(input)) return null; //allow for "none" as an input
        boolean[] results = paramsExist(input);
        String[] names = new String[]{getName(), "Object", "Level"};
        for (int i = results.length - 1; i > -1; i--) {
            if (!results[i]) {
                return "Cannot find "+names[i].toLowerCase()+"!";
            }
        }
        return null;
    }
    
    public final String[] getParams(String input) { 
        String p[] = input.substring(alias.length()+1, input.length()-1).split("([ ]*)?[,]([ ]*)?");
        if (p.length == 1) if (p[0].length() == 0) return new String[0];
        return p;
    }
    
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        if (value.equals("none")) return true;
        return value.replaceAll("("+getAlias()+"\\()"+getParamsRegex()+"(\\))", "").equals("");
    }  
    
    public final String getAlias() { return alias; }
    public final void setAlias(String w) { alias = w; }
    public final String getParamsRegex() { return params_regex; }
    public boolean[] paramsExist(String input) { return null; }
    public boolean isValidName(String name) { return false; }
    
}

/**
 * TYPE CLASSES
 */

class TypeAny extends Type {
    public TypeAny() { 
        setName("Any"); 
    }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        int t = Types.getType(value);
        if (t < 0) return false;
        Type type = Types.getType(t);
        if (type instanceof ComplexType) {
            return ((ComplexType)type).verifyParams(value) == null;
        }
        return true;
    }    
}

class TypeVar extends Type {
    public TypeVar() { 
        setName("Variable"); 
    }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        if (value.equals("none")) return false;
        if (value.trim().length() == 0) return false;
        if (value.trim().equals("true") || value.trim().equals("false")
                || value.trim().toLowerCase().equals("player")) return false;
        return value.replaceAll("^[a-zA-Z_$][a-zA-Z_$0-9]*$", "").equals("");
    }    
}

class TypeNumber extends Type {
    public TypeNumber() { setName("Number"); }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.replaceAll("((\\+|-)?([0-9]+)(\\.[0-9]+)?)|((\\+|-)?\\.?[0-9]+)", "").equals("");
    }
}

class TypeText extends Type {
    public TypeText() { setName("Text"); }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.charAt(0) == '"' && value.charAt(value.length()-1) == '"' 
                && value.length() >= 2
                && !(value.contains("[") || value.contains("]"));
    }
}

class TypeBoolean extends Type {
    public TypeBoolean() { setName("Boolean"); }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.equals("true") || value.equals("false");
    }
}

class TypeList extends Type {
    
    private int subtype = -1;
    
    public TypeList(int subtype) { 
        this.subtype = subtype;
        setName("List (?)");
    }
    
    @Override
    public String getName() {
        return "List ("+Types.getTypeName(subtype)+")";
    }
    
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        value = value.trim();
        if (value.replaceAll("((\\[)([\\s\\S]*)(\\]))+", "").length() != 0) return false;
        int open = 0; String val = "";
        for (char c: value.toCharArray()) {
            open += c == '[' ? 1 : (c == ']' ? -1 : 0);
            if (open == 1 && c != '[' && c != ']') val += c;
            if (c == ']' && open == 0) {
                if (!Types.getType(subtype).typeOf(val)) return false;
                val = "";
            }
            if (open > 1 || open < 0) return false;
        }
        if (open != 0) return false;
        return true;
    }
    
}

/**
 * COMPLEX TYPE CLASSES
 */
class TypeAnim extends ComplexType {
    public TypeAnim() { 
        setName("Animation"); 
        setAlias("Anim");
        setParams(3, false);
    }
    @Override
    public boolean[] paramsExist(String input) {
        String params[] = getParams(input);
        Level l = params.length == 3 ? Project.getProject().getLevel(params[2]) : Project.getProject().getCurrentLevel();
        if (l == null) return new boolean[]{false, false, false};
        SceneObject o = params.length >= 2 ? l.getObject(params[1]) : GUI.getSceneCanvas().getActiveObject();
        if (o == null) return new boolean[]{false, false, true};
        Animation a = params.length >= 1 ? o.getAnimation(params[0]) : null;
        if (a == null) return new boolean[]{false, true, true};
        return new boolean[]{true, true, true};
    }
}

class TypeFlow extends ComplexType {
    public TypeFlow() { 
        setName("Flow");
        setAlias("Flow");
        setParams(3, true);
    }
    @Override
    public boolean[] paramsExist(String input) {
        String params[] = getParams(input);
        if (params.length == 0) return new boolean[]{true, true, true};
        Level l = params.length == 3 ? Project.getProject().getLevel(params[2]) : Project.getProject().getCurrentLevel();
        if (l == null) return new boolean[]{false, false, false};
        SceneObject o = params.length >= 2 ? l.getObject(params[1]) : GUI.getSceneCanvas().getActiveObject();
        if (o == null) return new boolean[]{false, false, true};
        Flow a = params.length >= 1 ? o.getFlow(params[0]) : null;
        if (a == null) return new boolean[]{false, true, true};
        return new boolean[]{true, true, true};
    }    
}

class TypeObject extends ComplexType {
    public TypeObject() { 
        setName("Object");
        setAlias("Object"); 
        setParams(2, true);
    }
    @Override
    public boolean[] paramsExist(String input) {
        String params[] = getParams(input);
        if (params.length == 1) if (params[0].equals("player")) return new boolean[]{true, true, true};
        if (params.length == 0) return new boolean[]{true, true, true};
        Level l = params.length == 2 ? Project.getProject().getLevel(params[1]) : Project.getProject().getCurrentLevel();
        if (l == null) return new boolean[]{false, false, false};
        SceneObject o = params.length >= 1 ? l.getObject(params[0]) : GUI.getSceneCanvas().getActiveObject();
        if (o == null) return new boolean[]{false, true, true};
        return new boolean[]{true, true, true};
    }
    
}

class TypeLevel extends ComplexType {
    public TypeLevel() {
        setName("Level"); 
        setAlias("Level"); 
        setParams(1, true);
    }
    @Override
    public boolean[] paramsExist(String input) {
        String params[] = getParams(input);
        if (params.length == 0) return new boolean[]{true, true, true};
        Level l = params.length == 1 ? Project.getProject().getLevel(params[0]) : Project.getProject().getCurrentLevel();
        if (l == null) return new boolean[]{false, false, false};
        return new boolean[]{true, true, true};
    }    
}

class TypeAsset extends ComplexType {
    public TypeAsset() { 
        setName("Asset");
        setAlias("Asset");
        setParams(1, false);
    }
    @Override
    public boolean[] paramsExist(String input) {
        String params[] = getParams(input);
        if (params.length == 1) return new boolean[]{true, true, true};
        return new boolean[]{false, false, false};
    } 
}
