package org.cambural21.solidity.compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Abigen {

    private File buildDir = null;
    private String PKG = null;
    private File BIN = null;
    private File ABI = null;

    private boolean LANG_JAVA = false;
    private boolean LANG_GO = false;

    public Abigen(File buildDir, String pkg, File bin, File abi){
        this.PKG = pkg;
        this.BIN = bin;
        this.ABI = abi;
        this.buildDir = buildDir;
    }

    public boolean isLanguageJava() {
        return LANG_JAVA;
    }

    public boolean isLanguageGo() {
        return LANG_GO;
    }

    public Abigen setLanguageJava(boolean value) {
        this.LANG_JAVA = value;
        this.LANG_GO = !value;
        return this;
    }

    public Abigen setLanguageGo(boolean value) {
        this.LANG_JAVA = !value;
        this.LANG_GO = value;
        return this;
    }

    public Build build(){
        File binding = null;
        boolean success;
        try {
            final File abigen = Installer.getAbigen();
            if(!buildDir.exists()) buildDir.mkdirs();
            List<String> commandParts = new ArrayList<>();
            commandParts.add(abigen.getAbsolutePath());
            if(ABI != null) {
                commandParts.add(Option.ABI.getCmd());
                commandParts.add(ABI.getAbsolutePath());
            }
            if(BIN != null) {
                commandParts.add(Option.BIN.getCmd());
                commandParts.add(BIN.getAbsolutePath());
            }
            if(!PKG.isEmpty()) {
                commandParts.add(Option.PKG.getCmd());
                commandParts.add("\""+PKG+"\"");
            }

            commandParts.add(Option.LANG.getCmd());
            if(isLanguageJava()) commandParts.add("java");
            else if(isLanguageGo()) commandParts.add("go");

            {
                String bindingName = "UnknownBinding";
                if(ABI != null) bindingName = ABI.getName().replace("abi", "");
                else if(BIN != null) bindingName = BIN.getName().replace("bin", "");
                else throw new IOException("empty parameters");
                if(isLanguageJava()) bindingName += "java";
                else if(isLanguageGo()) bindingName += "go";
                binding = new File(buildDir, bindingName);
            }

            commandParts.add(Option.OUT.getCmd());
            commandParts.add(binding.getAbsolutePath());
            Process process = Installer.run(commandParts, abigen);
            Installer.printError(process.getInputStream(), false);
            Installer.printError(process.getErrorStream(), true);
            success = process.waitFor() == 0 && binding.exists();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            binding = null;
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

    public Abigen clean(){
        if(buildDir.exists()) Installer.cleanDirectory(buildDir);
        return this;
    }

    public interface Build {
        boolean wasSuccess();
        File getBinding();
    }

}
