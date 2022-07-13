package RecentFiles;

import ConstantVariables.ConstantVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayDeque;

public class RecentFiles {

    private int numRecentFiles;
    private ArrayDeque<File> recentFiles;
    private Logger logger = LoggerFactory.getLogger(RecentFiles.class);

    private boolean clearRecentFiles;



     public  RecentFiles() {
         numRecentFiles = 5;
        recentFiles = new ArrayDeque<File>();
     }


    /**
     *
     */
    public void createRecentFiles(){
         checkDeletedFiles();
        loadRecentFiles(getNumRecentFiles());
    }

    /**
     * This function just appends the NUMBER of recent files to a txt document.
     * @param numRecentFiles
     */
    public void saveNumberRecentFiles(int numRecentFiles){
        FileWriter writer = null;
        try {
            writer = new FileWriter(ConstantVariables.NUM_RECENT_FILE_PATH, false);
            writer.write(numRecentFiles + "\n");
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


    /**
     * This function saves the PATHS of the recently "saved as" vignette files by appending the paths to a txt document.
     */
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


    /**
     * This function reads the txt file created by saveRecentFiles() at ConstantVariables.RECENT_FILE_PATH.
     * It creates an ArrayDeque and then populates it with the recent Files
     * @return
     */
    public ArrayDeque<File> loadRecentFiles(int numRecentFiles) {

        String filePath = ConstantVariables.RECENT_FILE_PATH;
        File recentFile = new File(filePath);
        if(!recentFile.exists()) {
            try {
                recentFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

            int counter = 0;
            while ((line = br.readLine()) != null && counter<numRecentFiles ) {
                if (!line.trim().isEmpty()) {
                    File f = new File(line.trim());
                    files.add(f);
                }
                counter++;
            }
        } catch (IOException e) {
            logger.error("{Recent Files}", e);
        }
        recentFiles = files;
        return files;
    }

    /**
     *
     * @param file
     */
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

    public void clearRecentFiles(){
         this.setClearRecentFiles(true);
         this.getRecentFiles().clear();
         saveRecentFiles();
    }

    public void checkDeletedFiles()
    {
        String filePath = ConstantVariables.RECENT_FILE_PATH;
        FileWriter writer = null;

        try {
            File recentFile = new File(filePath);
            if(!recentFile.exists()){
                if(!recentFile.createNewFile())
                    return;
            }

            BufferedReader br = new BufferedReader(new FileReader(recentFile));
            String line;
            String para="";
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    File f = new File(line.trim());

                    //checking if file exists
                    if (f.exists()) {
                        para+=line+'\n';
                    }
                    else
                        System.out.println(line+" has been deleted.");
                }
            }

            writer = new FileWriter(filePath, false);
            writer.write(para);


        } catch (IOException e) {
            logger.error("{Recent Files}", e);
        }

        finally {
            try {
                if(writer!=null)
                    writer.close();
            } catch (IOException e) {
                logger.error("{Recent Files}", e);
            }
        }

    }




    public int getNumRecentFiles() {
        String num;
        File numRecentFile = new File(ConstantVariables.NUM_RECENT_FILE_PATH);
        try {
            if(!numRecentFile.exists()){
                numRecentFile.createNewFile();
                FileWriter initialWriter = new FileWriter(numRecentFile);
                initialWriter.write("1");
                initialWriter.close();
                return 1;
            }
            BufferedReader reader = new BufferedReader(new FileReader(numRecentFile));  //ConstantVariables.NUM_RECENT_FILE_PATH)
            num = reader.readLine();
            System.out.println("num of recent file read: "+num);
            return Integer.parseInt(num);
        } catch (IOException e) {
            logger.error("{Recent Files}", e);
        }
    return 0;

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
    public boolean isClearRecentFiles() {
        return clearRecentFiles;
    }

    public void setClearRecentFiles(boolean clearRecentFiles) {
        this.clearRecentFiles = clearRecentFiles;
    }
}
