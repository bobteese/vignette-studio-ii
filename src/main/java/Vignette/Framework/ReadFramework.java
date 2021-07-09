package Vignette.Framework;

import Application.Main;
import ConstantVariables.ConstantVariables;
import org.apache.commons.io.FileUtils;

import java.io.*;
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

    public static void read(String zipFilePath) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                String fileName = name.split("/")[name.split("/").length-1];
                if(!entry.isDirectory()){
                    if((name!=null || !"".equalsIgnoreCase(fileName)) && name.startsWith("pages/")
                            && ".html".equalsIgnoreCase(fileName.substring(fileName.lastIndexOf(".")))
                            && validPages.contains(fileName)){
                        Main.getVignette().addToHtmlFilesList(name.split("/")[name.split("/").length-1]);
                    }else if(((name!=null || !"".equalsIgnoreCase(name)) && name.startsWith("pages/images/"))){
                        Main.getVignette().addToImagesPathForHtmlFiles(name.split("/")[name.split("/").length-1]);
                    }
                }
            }
            zipFile.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void unZipTheFrameWorkFile(String zipFileName){
        try(ZipFile file = new ZipFile(zipFileName))
        {
            FileSystem fileSystem = FileSystems.getDefault();
            Enumeration<? extends ZipEntry> entries = file.entries();
            setUnzippedFrameWorkDirectory(Main.getFrameworkZipFile().replaceAll("/*.zip$", "") + "/");
            System.out.println("TARGET DIR: "+getUnzippedFrameWorkDirectory());
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
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


    public static void deleteDirectory(String filePath) throws IOException {
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

    }
}
