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

    /*
    File source = new File("/root/Downloads/Cambural21/Solidity/ERC721/ERC721.sol");
    File destination = new File("/root/Downloads/Cambural21/Solidity/BUILDS/ERC721");
    String packageName = "org.cambural21.solidity.manager.contracts";
    * */

    /*
    readlink -f `which web3j`
    /root/.web3j/web3j
    /root/.web3j/web3j-cli-shadow-1.4.1/bin/web3j
    rm -rf /root/.web3j/web3j-cli-shadow-1.4.1/bin/web3j
    * */

}
