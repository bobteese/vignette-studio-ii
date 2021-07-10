package Vignette.Framework;

import ConstantVariables.ConstantVariables;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
    public boolean frameworkIsPresent(FileInputStream stream, String frameworkName){
        try {
            String fileContents  = IOUtils.toString(stream, StandardCharsets.UTF_8.name());
            String frameworks[] = fileContents.split("\n");
            if(frameworks.length>0){
                for(String f:frameworks){
                    f = (f.split(",")[1]).split("=")[1].replaceAll("\'", "").trim().toLowerCase(Locale.ROOT);
                    if(frameworkName.equalsIgnoreCase(f))
                        return true;
                }
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public void addToFrameworkVersionFile() {
        FileWriter fileWriterObject = null;
        try{
            createFrameworkVersionFile();
            FileOutputStream outputStream = new FileOutputStream(this.frameworkFile, true);
            FileInputStream inputStream = new FileInputStream(ConstantVariables.FRAMEWORK_VERSION_FILE_PATH);
            if(!frameworkIsPresent(inputStream, this.frameworkName)){
                StringWriter getContent = new StringWriter();
                IOUtils.copy(inputStream, getContent);
                outputStream.write(this.toString().getBytes());
            }else{
                System.out.println("FRAMEWORK IS PRESENT");
            }

        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "{" +
                "frameworkPath='" + frameworkPath + '\'' +
                ", frameworkName='" + frameworkName + '\'' +
                ", serialNumber=" + serialNumber +
                '}' + "\n";
    }
}
