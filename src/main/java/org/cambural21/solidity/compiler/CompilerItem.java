package org.cambural21.solidity.compiler;

import org.slf4j.LoggerFactory;

import org.slf4j.impl.SimpleLogger;
import org.slf4j.impl.SimpleLoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CompilerItem {

    private final String packageName;
    private final File source;
    private final File build;

    public CompilerItem(File source, File build, String packageName){

        if(source == null) throw new NullPointerException("source is NULL");
        else if(build == null) throw new NullPointerException("source is NULL");
        else if(packageName == null || packageName.isEmpty()) throw new NullPointerException("source is NULL");
        this.packageName = packageName;
        this.source = source;
        this.build = build;
        if(!build.exists()) build.mkdirs();
        if(!source.exists()) throw new NullPointerException("Source not exist");
    }

    public String getPackageName() {
        return packageName;
    }

    public CompilerItem setLoggingLevel(LoggingLevel lvl) {
        if(lvl != null){
            try{
                String logLevel = null;
                switch (lvl){
                    case trace: {
                        logLevel = "trace";
                    } break;
                    case debug: {
                        logLevel = "debug";
                    } break;
                    case info: {
                        logLevel = "info";
                    } break;
                    case warning: {
                        logLevel = "warning";
                    } break;
                    case error: {
                        logLevel = "error";
                    } break;
                }

                System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_THREAD_NAME_KEY, "false");
                System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, logLevel.toLowerCase());
                Field field = SimpleLogger.class.getDeclaredField("INITIALIZED");
                field.setAccessible(true);
                field.set(null, false);
                Method method = SimpleLoggerFactory.class.getDeclaredMethod("reset");
                method.setAccessible(true);
                method.invoke(LoggerFactory.getILoggerFactory());

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return this;
    }

    public File getSource() {
        return source;
    }

    public File getBuild() {
        return build;
    }

    public void compile(){
        LightSolidityCompiler.cleanBuilds(getBuild());
        CompilerInfo info = LightSolidityCompiler.compile(LightSolidityCompiler.Compiler.WEB3J, getBuild(), getSource(), getPackageName());
        LightSolidityCompiler.cleanBuilds(getBuild());
        File binding = info.getBindingFile();
        System.out.println("BF: " + binding);
        System.out.println("LCP: " + info);
    }

}
