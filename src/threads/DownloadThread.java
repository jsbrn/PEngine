package threads;

import gui.GUI;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import scene.Scene;

public class DownloadThread extends Thread {
    
    static String source, dest;
    static boolean downloading = false;
    static DownloadThread thread;
    
    public static int LEVEL_EDITOR_ID = 8;
    
    private DownloadThread(){}
    
    /**
     * Downloads a file in the main thread. Only use this for smaller files.
     * @param source The source URL.
     * @param dest The destination filepath.
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
                    if (progress > last_progress) {
                        System.out.println(progress+"%");
                        if (button) GUI.statusIndicator.setText("Downloading game files: "+progress+"%");
                    }
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
    
    public static boolean[] checkForUpdates() {
        GUI.downloadUpdateButton.setVisible(false);
        boolean game = false, editor = false;
        if (!download("https://computerology.bitbucket.io/tools/editor/info.properties", Scene.USER_HOME+"/level_editor/jars/info.properties")) {
            JOptionPane.showMessageDialog(null, "Could not connect to the update server!\n"
                    + "Use this program at your own risk; what you see may\n"
                    + "not reflect the most recent version.");
            return new boolean[]{false, false};
        }
        Properties prop = new Properties(), prop2 = new Properties();
        try {
            FileInputStream f = new FileInputStream(Scene.USER_HOME+"/level_editor/jars/info.properties");
            prop.load(f);
            int gid = Integer.parseInt(prop.getProperty("gameID"));
            int eid = Integer.parseInt(prop.getProperty("editorID"));
            f.close();
            new File(Scene.USER_HOME+"/level_editor/jars/info.properties").deleteOnExit();
            System.out.println("Server level_editor: "+eid+", Local level_editor: "+LEVEL_EDITOR_ID);
            if (eid > LEVEL_EDITOR_ID) { editor = true; }
            if (editor) { JOptionPane.showMessageDialog(null, "A new version of the level editor is available!\n"
                    + "It is strongly recommended that you download it\nbefore continuing.");}
            //if the local game info DNE then do not show there is an update
            if (new File(Scene.USER_HOME+"/level_editor/jars/test.properties").exists() == false) {
                GUI.downloadUpdateButton.setVisible(editor);
                return new boolean[]{false, editor};
            }
            f = new FileInputStream(Scene.USER_HOME+"/level_editor/jars/test.properties");
            prop2.load(f);
            int lgid = Integer.parseInt(prop2.getProperty("updateID"));
            System.out.println("Server game: "+gid+", Local game: "+lgid);
            if (gid > lgid) { game = true; }
            f.close();
        } catch (IOException ex) {
            Logger.getLogger(DownloadThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        GUI.downloadUpdateButton.setVisible(editor);
        return new boolean[]{game, editor};
    }
    
    public void run() {
        download(source, dest);
    }
    
}
