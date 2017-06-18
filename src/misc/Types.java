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
            ASSET = 9;
    
    private static Type[] types = {
        new TypeAny(),
        new TypeVar(),
        new TypeNumber(),
        new TypeText(),
        new TypeBoolean(),
        new TypeAnim(),
        new TypeFlow(),
        new TypeObject(),
        new TypeLevel(),
        new TypeAsset()
    };
    
    public static Type getType(int index) {
        return types[index];
    }
    
    public static int getType(String input) {
        for (int i = 1; i < types.length; i++) if (types[i].typeOf(input)) return i;
        return -1;
    }
    
    public static String getTypeName(int type) {
        return types[type].getName();
    }
 
    /**
     * Verifies that the complex type parameters exist.
     * @param input The user input (the complex type).
     * @param type The type.
     * @return A String describing any issues, null if no issues.
     */
    public static String verifyParams(String input, int type) {
        if (!isComplex(type)) return types[type].getName()+" is not a complex type!";
        return ((ComplexType)(types[type])).verifyParams(input);
    }
    
    public static boolean isValidInput(String input, int type) {
        return types[type].typeOf(input);
    }
    
    public static boolean isComplex(int type) { return types[type] instanceof ComplexType; }
    
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
        boolean[] results = paramsExist(input);
        String[] params = getParams(input);
        String[] names = new String[]{getName(), "Object", "Level"};
        for (int i = results.length - 1; i > -1; i--) {
            if (!results[i]) {
                return "Can not find "+names[i].toLowerCase()+"!";
            }
        }
        return null;
    }
    
    public final String[] getParams(String input) { 
        String p[] = input.substring(alias.length()+1, input.length()-1).split("([ ]*)?[,]([ ]*)?");
        if (p.length == 1) if (p[0].length() == 0) return new String[0];
        return p;
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
        if (value == null) return false;
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
        return value.replaceAll("[^0-9]", "").equals(value);
    }
}

class TypeText extends Type {
    public TypeText() { setName("Text"); }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.charAt(0) == '"' && value.charAt(value.length()-1) == '"';
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

/**
 * COMPLEX TYPE CLASSES
 */

/**
 * if (type == TYPE_ANIM) return i.replaceAll("^(Anim\\()"+params_regex+"(\\))$", "").equals("");
        if (type == TYPE_FLOW) return i.replaceAll("^(Flow\\()"+params_regex+"(\\))$", "").equals("");
        if (type == TYPE_OBJECT) return i.replaceAll("^(Object\\()"+params_regex+"(\\))$", "").equals("");
        if (type == TYPE_LEVEL) return i.replaceAll("^(Level\\()"+params_regex+"(\\))$", "").equals("");
        if (type == TYPE_ASSET) return i.replaceAll("^(Asset\\()"+params_regex+"(\\))$", "").equals("");
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
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.replaceAll("("+getAlias()+"\\()"+getParamsRegex()+"(\\))", "").equals("");
    }    
}

class TypeFlow extends ComplexType {
    public TypeFlow() { 
        setName("Flow");
        setAlias("Flow");
        setParams(3, false);
    }
    @Override
    public boolean[] paramsExist(String input) {
        String params[] = getParams(input);
        Level l = params.length == 3 ? Project.getProject().getLevel(params[2]) : Project.getProject().getCurrentLevel();
        if (l == null) return new boolean[]{false, false, false};
        SceneObject o = params.length >= 2 ? l.getObject(params[1]) : GUI.getSceneCanvas().getActiveObject();
        if (o == null) return new boolean[]{false, false, true};
        Flow a = params.length >= 1 ? o.getFlow(params[0]) : null;
        if (a == null) return new boolean[]{false, true, true};
        return new boolean[]{true, true, true};
    }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.replaceAll("("+getAlias()+"\\()"+getParamsRegex()+"(\\))", "").equals("");
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
        if (params.length == 0) return new boolean[]{true, true, true};
        Level l = params.length == 2 ? Project.getProject().getLevel(params[1]) : Project.getProject().getCurrentLevel();
        if (l == null) return new boolean[]{false, false, false};
        SceneObject o = params.length >= 1 ? l.getObject(params[0]) : GUI.getSceneCanvas().getActiveObject();
        if (o == null) return new boolean[]{false, true, true};
        return new boolean[]{true, true, true};
    }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.replaceAll("("+getAlias()+"\\()"+getParamsRegex()+"(\\))", "").equals("");
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
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.replaceAll("("+getAlias()+"\\()"+getParamsRegex()+"(\\))", "").equals("");
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
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.replaceAll("^("+getAlias()+"\\()"+getParamsRegex()+"(\\))$", "").equals("");
    }    
}
