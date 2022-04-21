package org.cambural21.solidity;

import org.cambural21.solidity.compiler.CompilerItem;
import org.cambural21.solidity.compiler.Installer;
import org.cambural21.solidity.compiler.LoggingLevel;
import org.cambural21.solidity.compiler.VM;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;

/**
 * Solidity Compiler v0.1 with: Abigen 1.10.17, Web3j 1.4.1 Solc 0.8.13
 */
public final class TestCompiler extends InteractiveOptions {

    public static void main(String[] args) {
        compile(args);
    }

    private static void compile(String[] args) {
        try{
            String packageName = null;
            File destination = null;
            File source = null;
            TestCompiler compiler = new TestCompiler();
            if(compiler.isUI(args)){
                throw new NullPointerException("UI mode not available");
            }
            else if(compiler.isInteractive(args)){
                packageName = compiler.getPackageName();
                source = compiler.getSolidityFile();
                destination = compiler.getProjectDestination();
            }
            else if(args.length == 3){
                packageName = args[0];
                source = new File(args[1]).getAbsoluteFile();
                destination = new File(args[2]).getAbsoluteFile();
            }

            if(source != null && !source.canRead()) source.setReadable(true);
            if(destination != null && !destination.canRead()) destination.setReadable(true);

            CompilerItem item = new CompilerItem(source, destination, packageName).setLoggingLevel(LoggingLevel.trace);
            item.compile();

        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

}
