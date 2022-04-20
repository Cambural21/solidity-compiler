package org.cambural21.solidity.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Web3jGen {

    private File buildDir = null;
    private String PKG = null;
    private File BIN = null;
    private File ABI = null;

    public Web3jGen(File buildDir, String pkg, File bin, File abi){
        this.PKG = pkg;
        this.BIN = bin;
        this.ABI = abi;
        this.buildDir = buildDir;
    }

    private Build buildWithDownload() {
        File binding = null;
        boolean success = false;
        try{
            final File web3j = Installer.getInstance().getWeb3j();
            if(!buildDir.exists()) buildDir.mkdirs();
            List<String> commandParts = new ArrayList<>();
            commandParts.add(web3j.getAbsolutePath());
            commandParts.add("generate");
            commandParts.add("solidity");
            if(ABI != null) {
                commandParts.add("-a");
                commandParts.add(ABI.getAbsolutePath());
            }
            if(BIN != null) {
                commandParts.add("-b");
                commandParts.add(BIN.getAbsolutePath());
            }
            if(!PKG.isEmpty()) {
                commandParts.add("-p");
                commandParts.add(PKG);
            }
            commandParts.add("-o");
            commandParts.add(buildDir.getAbsolutePath());

            Process process = Installer.run(commandParts, web3j);
            Installer.printError(process.getInputStream(), false);
            Installer.printError(process.getErrorStream(), true);

            File tree = buildDir;
            {
                for (String path:PKG.replace(".", "//").split("//")) tree = new File(tree, path);
                tree = new File(tree, BIN.getName().replace("bin", "java"));
            }

            success = process.waitFor() == 0 && tree.exists();
            if(success) binding = tree;

        }catch (Exception e){
            success = false;
            binding = null;
            e.printStackTrace();
        }

        final boolean success_ = success;
        final File binding_ = binding;
        return new Build() {
            @Override
            public boolean wasSuccess() {
                return success_;
            }
            @Override
            public File getBinding() {
                return binding_;
            }
        };
    }

    public Build build() {
        Build build = null;
        try{
            build = buildWithDownload();

            /*if (VM.isMac() || VM.isWindows()) build = buildWithLibrary();
            else build = buildWithDownload();*/

        }catch (Exception e){
            e.printStackTrace();
            build = null;
        }
        return build;
    }

    public interface Build {
        boolean wasSuccess();
        File getBinding();
    }
}
