package Vignette.Framework;

import Application.Main;
import ConstantVariables.ConstantVariables;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class ReadFramework {
    public static String getUnzippedFrameWorkDirectory() {
        return unzippedFrameWorkDirectory;
    }

    public static void setUnzippedFrameWorkDirectory(String unzippedFrameWorkDirectory) {
        ReadFramework.unzippedFrameWorkDirectory = unzippedFrameWorkDirectory;
    }
    static String unzippedFrameWorkDirectory;
    public static Set<String> validPages = new HashSet<>(Arrays.asList(ConstantVariables.PAGES_LIST_TO_BE_PRESENT));

    public static ArrayList<Framework> readFrameworkVersionFile(){
        ArrayList<Framework> frameworksList = new ArrayList<>();
        String theString="";
        try{
            FileInputStream fin = new FileInputStream(new File(ConstantVariables.FRAMEWORK_VERSION_FILE_PATH));
            StringWriter writer = new StringWriter();
            IOUtils.copy(fin, writer, StandardCharsets.UTF_8);
            theString = writer.toString();
            String frameworks[] =  theString.split("\n");
            for(String f:frameworks){
                String path = f.split(",")[0].split("=")[1].replaceAll("\'",""); // path
                String name= f.split(",")[1].split("=")[1].replaceAll("\'",""); //name
                String serialNo = f.split(",")[2].split("=")[1].replaceAll("\\}$",""); //serialNo
                Framework temp =  new Framework(path,name,Long.parseLong(serialNo));
                frameworksList.add(temp);
            }
            fin.close();
        }catch(Exception e){
            System.out.println(e);
        }
        return frameworksList;
    }

    public static void listFileWithinFolder(String directoryName, List<String> files) {
        File directory = new File(directoryName);
        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if(fList != null)
            for (File file : fList) {
                if (file.isFile()) {
//                    files.add(directoryName.split("/")[directoryName.split("/").length-1]+"/"+file.getName());
                    System.out.println(file.getName());
                } else if (file.isDirectory()) {
                    System.out.println(file.getAbsolutePath());
                }
            }
    }

//    public static String readFile(InputStream file){
//        //String nextPageAnswers = createNextPageAnswersDialog(false);
//        StringBuilder stringBuffer = new StringBuilder();
//        BufferedReader bufferedReader = null;
//        try {
//            bufferedReader = new BufferedReader(new InputStreamReader(file));
//            String text;
//            while ((text = bufferedReader.readLine()) != null) {
//                //if(
//
//                text.contains("NextPageName")) text = "NextPageName=\""+page.getConnectedTo() +"\";";
//                stringBuffer.append(text);
//                stringBuffer.append("\n");
//            }
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//            System.out.println("{HTML Editor Content}"+ ex);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            System.out.println("{HTML Editor Content}"+ ex);
//        }
//        finally {
//            try {
//                bufferedReader.close();
//            } catch (IOException exp) {
//                exp.printStackTrace();
//                System.out.println("{HTML Editor Content}"+ exp);
//            }
//        }
//
//        return stringBuffer.toString();
//    }
    public static void readDefaultFramework(){
        ArrayList<String> tmep = new ArrayList<>();
        listFileWithinFolder(ConstantVariables.DEFAULT_FRAMEWORK_FOLDER+"/questionStyle/", tmep);
        for (int i = 0; i < ConstantVariables.PAGE_TYPE_ARRAY.length; i++) {
            String str = ConstantVariables.PAGE_TYPE_ARRAY[i];
            Main.getVignette().addToHtmlFilesList(str);
            ConstantVariables.PAGE_TYPE_LINK_MAP.put(str, ConstantVariables.PAGE_TYPE_SOURCE_ARRAY[i]);
        }
        System.out.println(Main.getVignette().getHtmlFiles());
        InputStream is = Main.class.getResourceAsStream( ConstantVariables.PAGE_TYPE_LINK_MAP.get("q"));
    }
    public static void read(String zipFilePath) {
        try {
            File zipFile = new File(zipFilePath);
            File[] allFiles = zipFile.listFiles();

//            for (File current: allFiles) {
//                String name = current.getName();
//                String fileName = name.split("/")[name.split("/").length-1];
//                System.out.println(current.getAbsolutePath());
//                if(current.isDirectory()){
//                    if((name!=null || !"".equalsIgnoreCase(fileName)) && name.startsWith("pages/")
//                            && ".html".equalsIgnoreCase(fileName.substring(fileName.lastIndexOf(".")))
//                            && validPages.contains(fileName)){
//                        Main.getVignette().addToHtmlFilesList(name.split("/")[name.split("/").length-1]);
//                    }else if(((name!=null || !"".equalsIgnoreCase(name)) && name.startsWith("pages/images/"))){
//                        Main.getVignette().addToImagesPathForHtmlFiles(name.split("/")[name.split("/").length-1].replaceAll(".png$","").trim(), name.trim());
//                    }
//                }
//            }



            for(File current: allFiles){
                if(current.isDirectory() && "pages".equalsIgnoreCase(current.getName())){
                    File[] pagesFiles = current.listFiles();
                    for(File pageFile:pagesFiles){
                        if((pageFile!=null || !"".equalsIgnoreCase(pageFile.getName())) && pageFile.getName().lastIndexOf(".")>-1 &&
                                ".html".equalsIgnoreCase(pageFile.getName().substring(pageFile.getName().lastIndexOf(".")))
                            && validPages.contains(pageFile.getName())){
                            Main.getVignette().addToHtmlFilesList(pageFile.getName().split("/")[pageFile.getName().split("/").length-1]);
                        }
                        if(pageFile.isDirectory() && "images".equalsIgnoreCase(pageFile.getName())){
                            File[] imageFiles = pageFile.listFiles();
                            for(File imageFile:imageFiles ){
                                Main.getVignette().addToImagesPathForHtmlFiles(
                                        imageFile.getName().split("/")[imageFile.getName().split("/").length-1].replaceAll(".png$","").trim(),
                                        "pages/"+"images/"+imageFile.getName().trim());
                            }
                        }
                    }
                }
            }

//            for (File current: allFiles) {
//                String name = current.getName();
//                String fileName = name.split("/")[name.split("/").length-1];
//                System.out.println(current.isDirectory());
//                System.out.println(current.getName());

//                if(!current.isDirectory()){
//                    if((name!=null || !"".equalsIgnoreCase(fileName)) && name.startsWith("pages/")
//                            && ".html".equalsIgnoreCase(fileName.substring(fileName.lastIndexOf(".")))
//                            && validPages.contains(fileName)){
//                        Main.getVignette().addToHtmlFilesList(name.split("/")[name.split("/").length-1]);
//                    }else if(((name!=null || !"".equalsIgnoreCase(name)) && name.startsWith("pages/images/"))){
//                        Main.getVignette().addToImagesPathForHtmlFiles(name.split("/")[name.split("/").length-1].replaceAll(".png$","").trim(), name.trim());
//                    }
//                }
//            }


        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
    public static File getResourceAsFile(String resourcePath) {
        try {
            InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                return null;
            }
            File tempFile = File.createTempFile(resourcePath, ".zip");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                //copy stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static boolean unZipTheFrameWorkFile(File zipFileName) {
//        if(Main.defaultFramework){
//            System.out.println("NO NEED TO UNZIP!");
//            return true;
//        }
        ZipFile file = null;
        try {
            file = new ZipFile(zipFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ZIP FILE NAME TO UNZIP: "+zipFileName);
        try
        {
            FileSystem fileSystem = FileSystems.getDefault();
            Enumeration<? extends ZipEntry> entries = file.entries();

            String name =  Main.getFrameworkZipFile().replaceAll("/*.zip$", "") + "/";
            setUnzippedFrameWorkDirectory(zipFileName.getAbsolutePath().replaceAll("/*.zip$", "") + "/");
            File f = new File(getUnzippedFrameWorkDirectory());
            if(f.exists()){
                System.out.println("DIR EXISTS AND NEEDS TO BE DELETED");
                FileUtils.deleteDirectory(f);
            }
            Files.createDirectory(fileSystem.getPath(getUnzippedFrameWorkDirectory()));
            //Iterate over entries
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                //If directory then create a new directory in uncompressed folder
                if (entry.isDirectory())
                {
//                    System.out.println("Creating Directory:" + getUnzippedFrameWorkDirectory() + entry.getName());
                    Files.createDirectories(fileSystem.getPath(getUnzippedFrameWorkDirectory() + entry.getName()));
                }
                //Else create the file
                else
                {
                    InputStream is = file.getInputStream(entry);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    String uncompressedFileName = getUnzippedFrameWorkDirectory() + entry.getName();
                    Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
                    Files.createFile(uncompressedFilePath);
                    FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
                    while (bis.available() > 0)
                    {
                        fileOutput.write(bis.read());
                    }
                    fileOutput.close();
//                    System.out.println("Written :" + entry.getName());
                }
            }
            return true;
        }
        catch (Exception e) {
            System.out.println("CANNOT SET DEFAULT FRAMEWORK: "+e.getMessage());
            return  false;
        }
    }


    public static void deleteDirectory(String filePath) {
     try{
         Path path = Paths.get(filePath);
         Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                     // delete directories or folders
                 @Override
                 public FileVisitResult postVisitDirectory(Path dir,
                                                           IOException exc)
                         throws IOException {
                     Files.delete(dir);
//                        System.out.printf("Directory is deleted : %s%n", dir);
                     return FileVisitResult.CONTINUE;
                 }
                 // delete files
                 @Override
                 public FileVisitResult visitFile(Path file,
                                                  BasicFileAttributes attrs)
                         throws IOException {
                     Files.delete(file);
//                        System.out.printf("File is deleted : %s%n", file);
                     return FileVisitResult.CONTINUE;
                 }
             }
         );
     }catch (IOException e){
         System.out.println(e.getMessage());
     }


    }

    public static void listFilesForFolder(File file, HashMap<String, String> questionStyleFileList) {
        for (File fileEntry : file.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry ,questionStyleFileList);
            } else {
                questionStyleFileList.put(fileEntry.getName().substring(0,fileEntry.getName().lastIndexOf(".")), fileEntry.getAbsolutePath());
                System.out.println(fileEntry.getName());
            }
        }
    }
}
