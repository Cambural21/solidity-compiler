package org.cambural21.solidity.compiler;

import java.io.File;

public final class Web3jCompiler {

    private Web3jCompiler(){}

    private static CompilerInfo compile(final File _buildDir, final File _sourceFile, String _package, boolean optimize, boolean deleteCache){
        CompilerInfo compilerInfo = null;
        if(_sourceFile != null && _buildDir != null && _package != null){
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
                    Web3jGen web3jgen = new Web3jGen(_buildDir, _package, binFile, abiFile);
                    Web3jGen.Build web3jgenBuild = web3jgen.build();
                    abigenFile = web3jgenBuild.wasSuccess()?web3jgenBuild.getBinding():null;
                    abigenSuccess = web3jgenBuild.wasSuccess();
                }
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
        return compile(_buildDir, _sourceFile, _package, true, false);
    }

}
