package threads;

import gui.GUI;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JOptionPane;
import misc.Assets;

public class DownloadThread extends Thread {
    
    private static String source, dest;
    private static boolean downloading = false, editor_update = false, runtime_update = false;
    private static DownloadThread thread;
    
    public static int LEVEL_EDITOR_ID = 10;
    
    private DownloadThread(){}
    
    /**
     * Downloads a file in the main thread. Only use this for smaller files.
     * @param source The source URL.
     * @param dest The destination file path.
     */
    public static boolean download(String source, String dest) {
        System.out.println("Downloading...");
        boolean button = GUI.runProjectAtLevelButton != null;
        if (button) GUI.runProjectAtLevelButton.setEnabled(false);
        if (button) GUI.runProjectButton.setEnabled(false);
        
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
                    if (progress > last_progress) { if (button) GUI.statusIndicator.setText("Downloading game files: "+progress+"%"); }
                    last_progress = progress;
                }

            }

            dis.close(); // close the data input stream
            fos = new FileOutputStream(dest);
            fos.write(fileData);
            fos.close();

            System.out.println("Download complete!");
            if (button) GUI.runProjectAtLevelButton.setEnabled(true);
            if (button) GUI.runProjectButton.setEnabled(true);
            GUI.statusIndicator.setText("");
            return true;

        } catch (MalformedURLException m) {
            System.out.println(m);
            m.printStackTrace();
        } catch (IOException io) {
            System.out.println(io);
            io.printStackTrace();
        }
        if (button) GUI.runProjectAtLevelButton.setEnabled(true);
        if (button) GUI.runProjectButton.setEnabled(true);
        GUI.statusIndicator.setText("");
        return false;
    }
    
    /**
     * Downloads a file in a different thread. 
     * Should only use it for the game jar; server meta data will download quick enough without it.
     * @param source The source URL.
     * @param dest The file path you wish to save the downloaded file to.
     */
    public static void downloadThreaded(String src, String dst) {
        thread = new DownloadThread();
        source = src;
        dest = dst;
        thread.start();
    }
    
    public static boolean editorUpdate() { return editor_update; }
    public static boolean runtimeUpdate() { return runtime_update; }
    
    public static void checkForUpdates() {
        try {
            URL url = new URL("https://computerology.bitbucket.io/tools/editor/version.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            runtime_update = Integer.parseInt(br.readLine().replace("runtime = ", "")) >= 0;
            editor_update = Integer.parseInt(br.readLine().replace("editor = ", "")) >= LEVEL_EDITOR_ID;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void handleUpdates() {
        if (editorUpdate()) JOptionPane.showMessageDialog(null, "A newer version of the level editor has been uploaded!"
                + "\nGo to http://computerology.bitbucket.io/ to grab it!");
        if (runtimeUpdate()) {
            JOptionPane.showMessageDialog(null, "A newer version of the runtime was found."
                    + "\nThe editor will now download it in the background.");
            downloadThreaded("https://computerology.bitbucket.io/tools/editor/runtime.jar",
                Assets.USER_HOME+"/level_editor/jars/runtime.jar");
        }
    }
    
    public void run() {
        download(source, dest);
    }
    
}
