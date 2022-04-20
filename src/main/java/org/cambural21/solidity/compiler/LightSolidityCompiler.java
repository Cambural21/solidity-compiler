package org.cambural21.solidity.compiler;

import java.io.File;

public final class LightSolidityCompiler {

    public enum Compiler {
        GETH, WEB3J;
    }

    private LightSolidityCompiler(){}

    public static CompilerInfo compile(Compiler compiler, File _buildDir, File _sourceFile, String _package){
        CompilerInfo info = null;
        if(compiler != null){
            switch (compiler){
                case WEB3J:{
                    info = Web3jCompiler.compile(_buildDir, _sourceFile, _package);
                } break;
                case GETH:{
                    info = GethCompiler.compile(_buildDir, _sourceFile, _package);
                } break;
            }
        }
        return info;
    }

    public static CompilerInfo compile(File _buildDir, File _sourceFile, String _package){
        return compile(Compiler.WEB3J, _buildDir, _sourceFile, _package);
    }

    public static void cleanBuilds(File buildDir){
        File[] list = FileWalker.getAll(buildDir, FileWalker.FileType.ABI, FileWalker.FileType.BIN);
        if(list != null && list.length>0) FileWalker.deleteAll(list);
    }

}
