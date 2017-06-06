package threads;

import gui.GUI;
import project.objects.components.Animation;

public class AnimationPlayer extends Thread {
    
    private static long last_time = 0;
    private static boolean paused;
    private static Animation animation;
    private static Thread thread;
    
    public AnimationPlayer(Runnable r) {
        super(r);
    }
    
    public static void init(Animation a) {
        a = a == null ? new Animation() : a;
        animation = a;
        if (thread == null) {
            thread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (paused) continue;
                        //gui animation canvas next frame
                    }
                }
            });
        }
    }

    public static void setPaused(boolean p) {
        paused = p;
    }
    
}
