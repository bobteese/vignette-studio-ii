package RecentFiles;

import ConstantVariables.ConstantVariables;
import SaveAsFiles.SaveAsVignette;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayDeque;

public class RecentFiles {

    private int numRecentFiles;
    private ArrayDeque<File> recentFiles;
    private Logger logger = LoggerFactory.getLogger(RecentFiles.class);



     public  RecentFiles() {
         numRecentFiles = 5;
        recentFiles = new ArrayDeque<File>();
     }


    public void createRecentFiles(){
         makeVignetteStudioDir();
        loadRecentFiles();
    }
    public void makeVignetteStudioDir(){

        File file = new File(ConstantVariables.VIGNETTESTUDIO_PATH);
        try {
            file.mkdirs();
            System.out.println("Successfully created vignettestudio-ii folder");
        } catch (SecurityException e) {

            logger.error("{Recent Files}", e);
            e.printStackTrace();
            System.out.println("{Recent Files}"+ e);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Warning");
            alert.setContentText("Error creating .vignettestudio-ii folder");

        }

    }
    public void saveRecentFiles(){
        FileWriter writer = null;
        try {
            writer = new FileWriter(ConstantVariables.RECENT_FILE_PATH, false);
            if (recentFiles == null) {
                writer.write("\n");
            } else {
                for (File f : recentFiles) {
                    writer.write(f.getAbsolutePath() + "\n");
                }
            }
        } catch (IOException e) {
            logger.error("{Recent Files}", e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                logger.error("{Recent Files}", e);
            }
        }


    }
    public ArrayDeque<File> loadRecentFiles() {
        String filePath = ConstantVariables.RECENT_FILE_PATH;
        File recentFile = new File(filePath);
        ArrayDeque<File> files = new ArrayDeque<File>();
        if (!recentFile.canRead()) {
            try {
                recentFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return files;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(recentFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    File f = new File(line.trim());
                    files.add(f);
                }
            }
        } catch (IOException e) {
            logger.error("{Recent Files}", e);
        }
        recentFiles = files;
        return files;
    }

    public void addRecentFile(File file){
        if (numRecentFiles <= 0)
            return;
        if (recentFiles.size() == numRecentFiles) {
            // Keep numRecentFiles recent files and remove the oldest one
            recentFiles.poll();
        }
        if (recentFiles.contains(file)) {
            recentFiles.remove(file);
        }
        recentFiles.add(file);

       saveRecentFiles();
    }







    public int getNumRecentFiles() {
        return numRecentFiles;
    }

    public void setNumRecentFiles(int numRecentFiles) {
        this.numRecentFiles = numRecentFiles;
    }

    public ArrayDeque<File> getRecentFiles() {
        return recentFiles;
    }

    public void setRecentFiles(ArrayDeque<File> recentFiles) {
        this.recentFiles = recentFiles;
    }
}
