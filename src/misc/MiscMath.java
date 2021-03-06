package misc;

import gui.GUI;
import gui.SceneCanvas;
import java.util.ArrayList;

public class MiscMath {
    
    /**
     * Calculates distance between two points.
     * @return The distance between (x1, y1) and (x2, y2).
     */
    public static double distanceBetween(double x1, double y1, double x2, double y2) {
        double distance_squared = Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
        double distance = Math.sqrt(distance_squared);
        return distance;
    }
    
    public static double clamp(double x, double min, double max) {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }
    
    /**
     * Finds the angle between (x, y) and (x2, y2) with 0 degrees being a vertical line.
     * @return A double representing the angle in degrees.
     */
    public static double angleBetween(double x1, double y1, double x2, double y2) {
        //slope formula = (Y2 - Y1) / (X2 - X1)
        double x, y, new_rotation;
        if (x1 < x2) {
            x = (x1 - x2) * -1;
            y = (y1 - y2) * -1;
            new_rotation = (((float)Math.atan(y/x) * 60));
        } else {
            x = (x2 - x1) * -1;
            y = (y1 - y2);
            new_rotation = (((float)Math.atan(y/x) * 60) + 180);
        }

        new_rotation += 90;
        return new_rotation % 360;
    }

    /**
     * Determines if point (x,y) intersects rectangle (rx, ry, rw, rh).
     * @param x The x value of the point.
     * @param y The y value of the point.
     * @param rx The x value of the rectangle.
     * @param ry The y value of the rectangle.
     * @param rw The width of the rectangle.
     * @param rh The height of the rectangle.
     * @return A boolean indicating whether the point intersects.
     */
    public static boolean pointIntersects(double x, double y, double rx, double ry, int rw, int rh) {
        if (x > rx && x < rx + rw) {
            if (y > ry && y < ry + rh) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determines if the rectangle with origin (x, y) and dimensions of (w, h) 
     * intersects the line between points (lx1, ly1) and (lx2, ly2).
     * @param x Rectangle x value.
     * @param y Rectangle y value.
     * @param w Rectangle width.
     * @param h Rectangle height.
     * @param lx1 x value of line endpoint #1
     * @param ly1 y value of line endpoint #1
     * @param lx2 x value of line endpoint #2
     * @param ly2 y value of line endpoint #2
     * @return
     */
    public static boolean rectangleIntersectsLine(double x, double y, int w, int h, double lx1, double ly1, double lx2, double ly2) {
        
        //determine values to be used in the equation for the line
        double m = (ly2-ly1)/(lx2-lx1);
        double p = lx1, q = ly1; //p = the offset from left side of screen, q = offset from bottom
        //if point l2 is closer to x = 0 than l1, set p and q to lx2's coordinates
        if (lx2 < lx1) {
            p = lx2;
            q = ly2;
        }
        //test if both end points of line are on left side, right, top, or bottom
        //if any is true, then the line does not intersect
        boolean on_left = (lx1 < x && lx2 < x), on_right = (lx1 > x+w && lx2 > x+w), 
                on_top = (ly1 < y && ly2 < y), on_bottom = (ly1 > y+h && ly2 > y+h); 
        if (!on_left && !on_right && !on_top && !on_bottom) {
            if (((y < (m*(x-p)+q)) && (y+h > (m*(x-p)+q)))
                    || ((y < (m*(x+w-p)+q)) && (y+h > (m*(x+w-p)+q)))) { //if left side or right side of rectangle intersects line
                return true;
            } 
            if ((x < (((y-q)/m)+p) && x+w > (((y-q)/m)+p))
                || (x < (((y+h-m)/q)+p) && x+w > (((y+h-q)/m)+p))) { //if top side or bottom side of rectangle intersects line
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if two rectangles intersect each other.
     * @param x The x of the first rectangle.
     * @param y The y of the first rectangle.
     * @param w The width of the first rectangle.
     * @param h The height of the first rectangle.
     * @param x2 The x of the second rectangle.
     * @param y2 The y of the second rectangle.
     * @param w2 The width of the second rectangle.
     * @param h2 The height of the second rectangle.
     * @return A boolean indicating if the two rectangles intersect.
     */
    public static boolean rectanglesIntersect(double x, double y, int w, int h, double x2, double y2, int w2, int h2) {
        
        //if any of the corners of rectangle 1 intersect rectangle 2
        if (MiscMath.pointIntersects(x, y, x2, y2, w2, h2)
                || MiscMath.pointIntersects(x + w, y, x2, y2, w2, h2)
                || MiscMath.pointIntersects(x + w, y + h, x2, y2, w2, h2)
                || MiscMath.pointIntersects(x, y + h, x2, y2, w2, h2)) {
            return true;
        }
        //and vice versa
        if (MiscMath.pointIntersects(x2, y2, x, y, w, h)
                || MiscMath.pointIntersects(x2 + w2, y2, x, y, w, h)
                || MiscMath.pointIntersects(x2 + w2, y2 + h2, x, y, w, h)
                || MiscMath.pointIntersects(x2, y2 + h2, x, y, w, h)) {
            return true;
        }
        //else return false
        return false;
    }
    
    /**
     * Check if a rectangle intersects a circle.
     * @param cx The origin x of the circle.
     * @param cy The origin y of the circle.
     * @param r The radius of the circle.
     */
    public static boolean rectangleIntersectsCircle(double x, double y, int w, int h, double cx, double cy, int r) {
        double r_x = x+(w/2), r_y = y+(h/2);
        double min_width = (w/2)+r, min_height = (h/2)+r;
        return MiscMath.distanceBetween(r_x, 0, cx, 0) < min_width 
                && MiscMath.distanceBetween(r_y, 0, cy, 0) < min_height;
    }
    
    public static String integersToString(ArrayList<Integer> int_arr) {
        String c = "";
        for (int s: int_arr) {
            c+=s+" ";
        }
        return c.trim();
    }
    
    /**
     * Rounds a to the nearest b.
     *
     * @return The value of a after rounding.
     */
    public static double round(double a, double b) {
        return Math.round(a / b) * b;
    }
    
    public static double[] getWorldCoords(int onscreen_x, int onscreen_y) {
        SceneCanvas canvas = GUI.getSceneCanvas();
        return new double[]{((onscreen_x - (canvas.getWidth() / 2)) / canvas.getZoom()) + canvas.getCameraX(),
                ((onscreen_y - (canvas.getHeight() / 2)) / canvas.getZoom()) + canvas.getCameraY()};
    }

    public static int[] getOnscreenCoords(double world_x, double world_y) {
        SceneCanvas canvas = GUI.getSceneCanvas();
        world_x = MiscMath.round(world_x, 1);
        world_y = MiscMath.round(world_y, 1);
        return new int[]{(int) round((world_x - canvas.getCameraX()) * canvas.getZoom(), 1) + (canvas.getWidth() / 2),
                (int) round((world_y - canvas.getCameraY()) * canvas.getZoom(), 1) + (canvas.getHeight() / 2)};
    }
    
    public static int[] toIntArray(String s, int size) {
        int[] arr = toIntArray(s);
        if (arr.length == 0) return new int[size];
        return arr;
    }
    
    /**
     * Turns a String of numbers into an int array.
     * @param s The string to parse.
     * @return An int array.
     */
    public static int[] toIntArray(String s) {
        if (s == null) return new int[]{};
        if (s.length() == 0) return new int[]{};
        String parsed[] = s.toLowerCase().split("[^0-9-+]");
        return toIntArray(parsed);
    }
    
    public static int[] toIntArray(String[] s) {
        if (s == null) return new int[]{};
        int[] result = new int[s.length];
        for (int i = 0; i < s.length; i++) result[i] = Integer.parseInt(s[i]);
        return result;
    }
    
    public static double[] toDoubleArray(String[] s) {
        if (s == null) return new double[]{};
        double[] result = new double[s.length];
        for (int i = 0; i < s.length; i++) result[i] = Double.parseDouble(s[i]);
        return result;
    }
    
    /**
     * Turns a String of numbers into a double array.
     * @param s The string to parse.
     * @return A double array.
     */
    public static double[] toDoubleArray(String s) {
        if (s == null) return new double[]{};
        String parsed[] = s.toLowerCase().split("[^0-9-.+]");
        return toDoubleArray(parsed);
    }
    
    public static boolean[] toBooleanArray(String s) {
        if (s == null) return new boolean[]{};
        String parsed[] = s.trim().toLowerCase().split(" ");
        return toBooleanArray(parsed);
    }
    
    public static boolean[] toBooleanArray(String[] s) {
        if (s == null) return new boolean[]{};
        boolean[] result = new boolean[s.length];
        for (int i = 0; i < s.length; i++) result[i] = Boolean.parseBoolean(s[i]);
        return result;
    }

}