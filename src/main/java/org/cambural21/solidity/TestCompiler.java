package org.cambural21.solidity;

import org.cambural21.solidity.compiler.CompilerItem;
import org.cambural21.solidity.compiler.LoggingLevel;
import java.io.File;

public final class TestCompiler extends InteractiveOptions {

    public static void main(String[] args) {
        try{
            TestCompiler compiler = new TestCompiler();
            String packageName = compiler.getPackageName();
            File source = compiler.getSolidityFile();
            File destination = compiler.getProjectDestination();
            CompilerItem item = new CompilerItem(source, destination, packageName).setLoggingLevel(LoggingLevel.trace);
            item.compile();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

}
