package org.cambural21.solidity.compiler;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class GethCompiler {

    private GethCompiler(){}

    public enum Language {
        GO, JAVA;
    }

    public static File parsePackageName(File tree, String _package){
        for (String path:_package.split("\\.")) {
            tree = new File(tree, path);
            tree.mkdirs();
        }
        return tree;
    }

    private static CompilerInfo compile(Language language, final File _buildDir, final File _sourceFile, String _package, boolean optimize, boolean deleteCache){
        CompilerInfo compilerInfo = null;
        if(language != null && _sourceFile != null && _buildDir != null && _package != null){
            File abiFile = null, binFile = null, abigenFile = null;
            boolean install = Installer.getInstance().install();
            boolean solcSuccess = false, abigenSuccess = false;
            if(install){
                Solc solc = new Solc(_buildDir, _sourceFile).setOPT(optimize).setABI(true).setBIN(true);
                if(deleteCache) solc.cleanAll();
                solcSuccess = solc.build();
                if(solcSuccess){
                    String ext = FileWalker.getExtension(_sourceFile.getName());
                    abiFile = new File(_buildDir, _sourceFile.getName().replace(ext, "abi"));
                    binFile = new File(_buildDir, _sourceFile.getName().replace(ext, "bin"));
                    Abigen abigen = new Abigen(_buildDir,_package, binFile, abiFile);
                    switch (language){
                        case JAVA:{
                            abigen.setLanguageJava(true);
                        } break;
                        case GO:{
                            abigen.setLanguageGo(true);
                        } break;
                    }
                    Abigen.Build abigenBuild = abigen.build();
                    abigenFile = abigenBuild.wasSuccess()?abigenBuild.getBinding():null;
                    abigenSuccess = abigenBuild.wasSuccess();
                }
            }

            if(solcSuccess && abigenSuccess) {
                try{
                    String ext = FileWalker.getExtension(abigenFile.getName());
                    String _name_ = abigenFile.getName().replace("."+ext, "");
                    byte[] data = Files.readAllBytes(abigenFile.toPath());
                    String pattern_constructor_1 = "private " + (("\"")+_package+("\""));
                    String pattern_constructor_2 = "public " + (("\"")+_package+("\""));
                    String pattern_constructor_3 = "return new " + (("\"")+_package+("\""));
                    String pattern_constructor_4 = "public static " + (("\"")+_package+("\""));
                    String pattern_constructor_5 = "public class " + (("\"")+_package+("\""));
                    String pattern_constructor_6 = ("\"")+_package+("\"")+";";
                    data = new String(data)
                            .replace(pattern_constructor_1, "private " + _name_)
                            .replace(pattern_constructor_2, "public " + _name_)
                            .replace(pattern_constructor_3, "return new " + _name_)
                            .replace(pattern_constructor_4, "public static " + _name_)
                            .replace(pattern_constructor_5, "public class " + _name_)
                            .replace(pattern_constructor_6, _package+";")
                            .replace("BoundContract Contract", "BoundContract contract")
                            .replace("Transaction Deployer", "Transaction deployer")
                            .replace("Address Address", "Address address")
                            .replace("this.Contract", "this.contract")
                            .replace("this.Deployer", "this.deployer")
                            .replace("this.Address", "this.address")
                            .getBytes(StandardCharsets.UTF_8);

                    abigenFile.delete();

                    File tree = _buildDir;
                    {
                        tree = parsePackageName(tree, _package);
                        tree = new File(tree, abigenFile.getName().replace("abi", "java"));
                    }
                    Files.write(tree.toPath(), data);
                    abigenFile = tree;

                }catch (Exception e){}
            }

            final boolean _solcSuccess = solcSuccess, _abigenSuccess = abigenSuccess;
            final File _abiFile = abiFile, _binFile = binFile, _abigenFile = abigenFile;

            compilerInfo = new CompilerInfo(){
                @Override
                public boolean isAbigenSuccess() {
                    return _abigenSuccess;
                }
                @Override
                public boolean isSolcSuccess() {
                    return _solcSuccess;
                }
                @Override
                public File getBindingFile() {
                    return _abigenFile;
                }
                @Override
                public File getSourceFile() {
                    return _sourceFile;
                }
                @Override
                public String getPackage() {
                    return _package;
                }
                @Override
                public File getBuildDir() {
                    return _buildDir;
                }
                @Override
                public File getABIFile() {
                    return _abiFile;
                }
                @Override
                public File getBINFile() {
                    return _binFile;
                }

                @Override
                public String toString() {
                    if(_abigenSuccess && _solcSuccess){
                        return "CompilerInfo {\n" +
                                "\tSOLIDITY: " + getSourceFile() + "\n" +
                                "\tBINDING: " + getBindingFile() + "\n" +
                                "\tABI: " + getABIFile() + "\n" +
                                "\tBIN: " + getBINFile() + "\n" +
                                "}\n";
                    }
                    else {
                        return "CompilerInfo {\n" +
                                "\tABIGEN: " + isAbigenSuccess() + "\n" +
                                "\tSOLC: " + isSolcSuccess() + "\n" +
                                "}\n";
                    }
                }
            };
        }
        return compilerInfo;
    }

    public static CompilerInfo compile(final File _buildDir, final File _sourceFile, String _package){
        return compile(Language.JAVA, _buildDir, _sourceFile, _package, true, false);
    }

}
