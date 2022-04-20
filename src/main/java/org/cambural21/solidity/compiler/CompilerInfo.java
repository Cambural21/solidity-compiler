package org.cambural21.solidity.compiler;

import java.io.File;

public interface CompilerInfo {
    boolean isAbigenSuccess();
    boolean isSolcSuccess();
    File getBindingFile();
    File getSourceFile();
    String getPackage();
    File getBuildDir();
    File getABIFile();
    File getBINFile();
}
