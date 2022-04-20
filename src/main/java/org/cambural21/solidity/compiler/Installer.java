package org.cambural21.solidity.compiler;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

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

    private final static String ABIGEN_ = "abigen";
    private final static String SOLC_ = "solc";

    private final OS os;

    private Installer(OS os){
        this.os = os;
    }

    //fixme
    private InputStream getResourceAsStream(String name) throws IOException {

        Path resourcesPath = FileSystems.getDefault().getPath(System.getProperty("user.dir"), "/src/main/", "/"+os.getFolder()+"/" + name);
        return new ByteArrayInputStream(Files.readAllBytes(resourcesPath));

        //return Installer.class.getClassLoader().getResourceAsStream("resources/"+os.getFolder()+"/" + name);
        //return getClass().getClassLoader().getResourceAsStream("/"+os.getFolder()+"/" + name);
        //return getClass().getClassLoader().getResourceAsStream(os.getFolder()+"/" + name);
    }

    public static File web3jPath = null;

    public static boolean setWeb3jPath(File web3jPath) {
        Installer.web3jPath = web3jPath;
        return web3jPath != null && web3jPath.exists();
    }

    public static File getWeb3j() throws IOException {
        return web3jPath.getAbsoluteFile();
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
        Installer installer = null;
        if(VM.isMac()) installer = new Installer(OS.macOS);
        else if(VM.isUnix()) installer = new Installer(OS.LINUX);
        else if(VM.isWindows()) installer = new Installer(OS.WINDOWS);
        return installer;
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
