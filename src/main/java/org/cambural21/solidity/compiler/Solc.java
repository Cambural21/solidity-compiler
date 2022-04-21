package org.cambural21.solidity.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Solc {

    private boolean BIN;
    private boolean ABI;
    private boolean OPT;

    private File sourceFile;
    private File buildDir;

    public Solc(File buildDir, File sourceFile){
        this.buildDir = buildDir;
        this.sourceFile = sourceFile;
    }

    public boolean isABI() {
        return ABI;
    }

    public boolean isBIN() {
        return BIN;
    }

    public boolean isOPT() {
        return OPT;
    }

    public Solc setABI(boolean ABI) {
        this.ABI = ABI;
        return this;
    }

    public Solc setBIN(boolean BIN) {
        this.BIN = BIN;
        return this;
    }

    public Solc setOPT(boolean OPT) {
        this.OPT = OPT;
        return this;
    }

    public boolean build(){
        boolean success;
        try {
            final File solc = Installer.getSolc();
            if(!buildDir.exists()) buildDir.mkdirs();
            List<String> commandParts = new ArrayList<>();
            commandParts.add(solc.getAbsolutePath());
            commandParts.add(sourceFile.getAbsolutePath());
            if(ABI && BIN) commandParts.add(Option.OVERWRITE.getCmd());
            if(OPT) commandParts.add(Option.OPTIMIZE.getCmd());
            if(BIN) commandParts.add(Option.BIN.getCmd());
            if(ABI) commandParts.add(Option.ABI.getCmd());

            File[] internalDirectories = FileWalker.getAllDirs(sourceFile.getParentFile());
            if(internalDirectories != null && internalDirectories.length>0){
                String path = "";
                commandParts.add(Option.BASE_PATH.getCmd());
                commandParts.add(sourceFile.getParentFile().getAbsolutePath());
                for (File include: internalDirectories) {
                    if(path.isEmpty()) path += include.getAbsolutePath();
                    else path += ","+include.getAbsolutePath();
                }
                commandParts.add(Option.INCLUDE_PATH.getCmd());
                commandParts.add(path);
            }
            commandParts.add(Option.O.getCmd());
            commandParts.add(buildDir.getAbsolutePath());

            Process process = Installer.run(commandParts, solc);
            Installer.printError(process.getInputStream(), false);
            Installer.printError(process.getErrorStream(), true);

            String ext = FileWalker.getExtension(sourceFile.getName());
            File abi = new File(buildDir, sourceFile.getName().replace(ext, "abi"));
            File bin = new File(buildDir, sourceFile.getName().replace(ext, "bin"));

            success = process.waitFor() == 0 && abi.exists() && bin.exists();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public Solc cleanAll(){
        if(buildDir.exists()) Installer.cleanDirectory(buildDir);
        return this;
    }

}


