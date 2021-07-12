package Vignette.Framework;

import ConstantVariables.ConstantVariables;
import com.sun.javafx.stage.StageHelper;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

public class Framework implements Serializable {

    public Framework(String frameworkPath, String frameworkName, long serialNumber) {
        this.frameworkPath = frameworkPath;
        this.frameworkName = frameworkName;
        this.serialNumber = serialNumber;
    }
    File frameworkFile;
    String frameworkPath;
    String frameworkName;
    long serialNumber;

    public String getFrameworkPath() {
        return frameworkPath;
    }

    public void setFrameworkPath(String frameworkPath) {
        this.frameworkPath = frameworkPath;
    }

    public String getFrameworkName() {
        return frameworkName;
    }

    public void setFrameworkName(String frameworkName) {
        this.frameworkName = frameworkName;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void createFrameworkVersionFile() throws IOException {
        this.frameworkFile = new File(ConstantVariables.FRAMEWORK_VERSION_FILE_PATH);
        if(!this.frameworkFile.exists())
            this.frameworkFile.createNewFile();
        else
            System.out.println("VERSION FILE EXISTS");
    }
    public static boolean frameworkIsPresent(FileInputStream stream, String frameworkName){
        try {
            String fileContents  = IOUtils.toString(stream, StandardCharsets.UTF_8.name());
            if("".equalsIgnoreCase(fileContents) || fileContents!=null){
                String frameworks[] = fileContents.split("\n");
                if(frameworks.length>0){
                    for(String f:frameworks){
                        f = (f.split(",")[1]).split("=")[1].replaceAll("\'", "").trim().toLowerCase(Locale.ROOT);
                        if(frameworkName.equalsIgnoreCase(f))
                            return true;
                    }
                }
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean addToFrameworkVersionFile() {
        FileWriter fileWriterObject = null;
        try{
            createFrameworkVersionFile();
            FileOutputStream outputStream = new FileOutputStream(this.frameworkFile, true);
            FileInputStream inputStream = new FileInputStream(ConstantVariables.FRAMEWORK_VERSION_FILE_PATH);
            if(!frameworkIsPresent(inputStream, this.frameworkName)){
                StringWriter getContent = new StringWriter();
                IOUtils.copy(inputStream, getContent, StandardCharsets.UTF_8);
                outputStream.write(this.toString().getBytes());
                return true;
            }else{
                System.out.println("FRAMEWORK IS PRESENT");
                ArrayList<Framework> listOfFramworks = ReadFramework.readFrameworkVersionFile();
                int index = -1;
                for(int i = 0;i<listOfFramworks.size();i++){
                    if(listOfFramworks.get(i).getFrameworkName().equalsIgnoreCase(this.getFrameworkName())){
                        index = i;
                        break;
                    }
                }
                if(listOfFramworks.get(index).getFrameworkPath().equalsIgnoreCase(this.getFrameworkPath()))
                    System.out.println("PATHS ARE SAME");
                else{
                    System.out.println("PATH IS CHANGED FOR THE SAME FRAMEWORK!! ");
                    listOfFramworks.get(index).setFrameworkPath(this.getFrameworkPath());
                    FileOutputStream rewriteOutputStream = new FileOutputStream(this.frameworkFile, false);
                    for(Framework f:listOfFramworks)
                        rewriteOutputStream.write(f.toString().getBytes());
                }

                return false;
            }

        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public String toString() {
        return "{" +
                "frameworkPath='" + frameworkPath + '\'' +
                ", frameworkName='" + frameworkName + '\'' +
                ", serialNumber=" + serialNumber +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Framework)) {
            return false;
        }
        Framework c = (Framework) o;

        if(c.getFrameworkName().equalsIgnoreCase(this.getFrameworkName()) && this.getSerialNumber() == c.getSerialNumber())
            return true;
        return false;
    }
}


//Framework: 5840307509838859698