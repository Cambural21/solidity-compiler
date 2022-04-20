package org.cambural21.solidity.compiler;

import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Installer {

    public enum OS {
        LINUX("resources/linux"), WINDOWS("resources/windows"), macOS("resources/macOS");
        private final String folder;
        OS(String folder){
            this.folder = folder;
        }
        public String getFolder() {
            return folder;
        }
    };

    //******************************************************************************************************************

    private static final Object obj = new Object();
    private static Installer installer;

    private final static String ABIGEN_ = "abigen";
    private final static String SOLC_ = "solc";
    private String web3jVersion = "1.4.1";
    private final OS os;

    private Installer(OS os){
        this.os = os;
    }

    public Installer setWeb3jVersion(String web3jVersion) {
        if(web3jVersion != null && !web3jVersion.isEmpty()) this.web3jVersion = web3jVersion;
        return this;
    }

    public String getWeb3jVersion() {
        return web3jVersion;
    }

    //******************************************************************************************************************

    private File getWeb3jZip() {
        return new File("web3j-"+getWeb3jVersion()+".zip").getAbsoluteFile();
    }

    private String getWeb3jUrl() {
        String V = getWeb3jVersion();
        return "https://github.com/web3j/web3j-cli/releases/download/v"+V+"/web3j-"+V+".zip";
    }

    public File getWeb3j() {
        File file = new File("web3j-"+getWeb3jVersion()+"/bin/web3j").getAbsoluteFile();
        if(file.exists()) file.setExecutable(true);
        return file;
    }

    //******************************************************************************************************************

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[1024*2];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    private void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zipIn, filePath);
            } else {
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private void download(String url, File destiny) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(destiny);
        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            fileOutputStream.write(dataBuffer, 0, bytesRead);
        }
        fileOutputStream.close();
        in.close();
    }

    private boolean downloadWeb3j(){
        boolean success = false;
        try{
            download(getWeb3jUrl(), getWeb3jZip());
            unzip(getWeb3jZip().getAbsolutePath(), new File(".").getAbsolutePath());
            getWeb3jZip().delete();
            success = true;
        }catch (Exception e){
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private boolean installWeb3j(){
        if(!downloadWeb3j()) throw new NullPointerException("web3j is null");
        File web3j = getWeb3j();
        return web3j.exists();
    }

    //******************************************************************************************************************

    //fixme
    private InputStream getResourceAsStream(String name) throws IOException {

        Path resourcesPath = FileSystems.getDefault().getPath(System.getProperty("user.dir"), "/src/main/", "/"+os.getFolder()+"/" + name);
        return new ByteArrayInputStream(Files.readAllBytes(resourcesPath));

        //return Installer.class.getClassLoader().getResourceAsStream("resources/"+os.getFolder()+"/" + name);
        //return getClass().getClassLoader().getResourceAsStream("/"+os.getFolder()+"/" + name);
        //return getClass().getClassLoader().getResourceAsStream(os.getFolder()+"/" + name);
    }

    public static File getAbigen() throws IOException {
        File file = new File(getABIGEN());
        if(!file.canExecute()) throw new IOException("abigen can't execujte");
        return file.getAbsoluteFile();
    }

    public static File getSolc() throws IOException {
        File file = new File(getSOLC());
        if(!file.canExecute()) throw new IOException("solc can't execujte");
        return file.getAbsoluteFile();
    }

    public static Installer getInstance(){
        synchronized (obj){
            if(installer == null){
                if(VM.isMac()) installer = new Installer(OS.macOS);
                else if(VM.isUnix()) installer = new Installer(OS.LINUX);
                else if(VM.isWindows()) installer = new Installer(OS.WINDOWS);
            }
            return installer;
        }
    }

    private static String getABIGEN(){
        return VM.isWindows()?ABIGEN_+".exe":ABIGEN_;
    }

    private static String getSOLC(){
        return VM.isWindows()?SOLC_+".exe":SOLC_;
    }

    public boolean install(){
        boolean success = false;
        try{
            if(!installWeb3j()) throw new NullPointerException("web3j is null");
            for (String binName:new String[]{getABIGEN(), getSOLC()}) {
                File local = new File(binName);
                InputStream is = getResourceAsStream(binName);
                Files.copy(is, local.toPath(), StandardCopyOption.REPLACE_EXISTING);
                is.close();
                local.setExecutable(true, false);
            }
            success = true;
        }catch (Exception e){
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    //******************************************************************************************************************

    public static Process run(List<String> commandParts, File binaryFile) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(commandParts).directory(binaryFile.getParentFile());
        processBuilder.environment().put("LD_LIBRARY_PATH", binaryFile.getParentFile().getCanonicalPath());
        Process process = processBuilder.start();
        return process;
    }

    public static void printError(InputStream is, boolean asError) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = in.readLine()) != null) {
            if(asError) System.err.println(line);
            else System.out.println(line);
        }
        if(asError) System.err.println();
        else System.out.println();
    }

    public static void cleanDirectory(File root) {
        if(root != null){
            File[] list = root.listFiles();
            if (list == null) return;
            for ( File f : list ) {
                if (f.isDirectory()) cleanDirectory(f.getAbsoluteFile());
                else f.delete();
            }
        }
    }

}
