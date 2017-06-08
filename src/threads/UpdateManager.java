package threads;

import gui.GUI;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import misc.Assets;
import misc.MiscMath;
import project.Level;
import project.objects.SceneObject;

public class UpdateManager extends Thread {
    
    private static String source, dest;
    private static boolean editor_update = false, runtime_update = false, blocked = false;
    private static Thread thread;
    
    public static int VERSION_ID = 10, RUNTIME_VERSION_ID = 0;
    public static int LATEST_VERSION_ID = 10, LATEST_RUNTIME_VERSION_ID = 0;
    public static final String VERSION_NAME = "1.3-beta";
    
    /**
     * Downloads a file in the main thread. Only use this for smaller files.
     * @param source The source URL.
     * @param dest The destination file path.
     */
    private static boolean download(String source, String dest) {
        System.out.println("Downloading...");
        
        URL url; //represents the location of the file we want to dl.
        URLConnection con;  // represents a connection to the url we want to dl.
        DataInputStream dis;  // input stream that will read data from the file.
        FileOutputStream fos; //used to write data from input stream to file.

        byte[] fileData;  //byte aray used to hold data from downloaded file

        //download the file
        int last_progress = -1;
        try {
            url = new URL(source);
            con = url.openConnection();
            dis = new DataInputStream(con.getInputStream());
            fileData = new byte[con.getContentLength()];

            int progress = 0;

            for (float x = 0; x < con.getContentLength(); x++) {

                fileData[(int) x] = dis.readByte();
                if (x > 0) {
                    progress = (int) (x / con.getContentLength() * 100);
                    if (progress > last_progress) { GUI.statusIndicator.setText("Download in progress: "+progress+"%"); }
                    last_progress = progress;
                }

            }

            dis.close(); // close the data input stream
            fos = new FileOutputStream(dest);
            fos.write(fileData);
            fos.close();

            System.out.println("Download complete!");
            GUI.statusIndicator.setText("");
            return true;

        } catch (MalformedURLException m) {
            System.out.println(m);
            m.printStackTrace();
        } catch (IOException io) {
            System.out.println(io);
            io.printStackTrace();
        }
        GUI.statusIndicator.setText("");
        return false;
    }
    
    public static void load() {
        File f = new File(Assets.USER_HOME+"/platformr/jars/versions.txt");
        if (!f.exists()) return;
        FileReader fr;
        System.out.println("Saving version info: " + f.getAbsoluteFile().getAbsolutePath());
        try {
            fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                String line = br.readLine();
                
                if (line == null) break;
                line = line.trim();
                if (line.indexOf("runtime_id = ") == 0) {
                    RUNTIME_VERSION_ID = Integer.parseInt(line.substring(10));
                }
                
            }
            br.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void save() {
        File f = new File(Assets.USER_HOME+"/platformr/jars/versions.txt");
        FileWriter fw;
        System.out.println("Saving version info: " + f.getAbsoluteFile().getAbsolutePath());
        try {
            if (!f.exists()) f.createNewFile();
            fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("runtime_id = "+RUNTIME_VERSION_ID);
            
            bw.close();
            System.out.println("Saved to "+f.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Downloads a file in a different thread. 
     * Should only use it for the game jar; server meta data will download quick enough without it.
     * @param src The source URL.
     * @param dst The file path you wish to save the downloaded file to.
     */
    private static void downloadThreaded(String src, String dst) {
        if (blocked) { System.err.println("Download failed: download already in progress."); }
        thread = new Thread(new Runnable() {
            public void run() {
                blocked = true;
                download(source, dest);
                blocked = false;
            }
        });
        source = src;
        dest = dst;
        thread.start();
    }
    
    public static void downloadRuntime() {
        downloadThreaded("https://computerology.bitbucket.io/tools/editor/runtime.jar",
                Assets.USER_HOME+"/platformr/jars/runtime.jar");
        RUNTIME_VERSION_ID = LATEST_RUNTIME_VERSION_ID;
        save();
    }
    
    public static boolean downloadInProgress() {
        return blocked;
    }
    
    public static boolean editorUpdate() { return editor_update; }
    public static boolean runtimeUpdate() { return runtime_update; }
    
    public static void checkForUpdates() {
        load();
        try {
            URL url = new URL("https://computerology.bitbucket.io/tools/editor/versions.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            
            LATEST_RUNTIME_VERSION_ID = Integer.parseInt(br.readLine().replace("runtime = ", ""));
            LATEST_VERSION_ID = Integer.parseInt(br.readLine().replace("editor = ", ""));
            
            System.out.println("Runtime: "+RUNTIME_VERSION_ID+" -> "+LATEST_RUNTIME_VERSION_ID);
            System.out.println("Editor: "+VERSION_ID+" -> "+LATEST_VERSION_ID);
            
            runtime_update = LATEST_RUNTIME_VERSION_ID >= RUNTIME_VERSION_ID;
            editor_update = LATEST_VERSION_ID >= VERSION_ID;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
