package org.cambural21.solidity.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class FileWalker {

    private FileWalker(){}

    public enum FileType {
        SOLIDITY("sol"), ABI("abi"), BIN("bin");
        private final String ext;
        FileType(String ext){
            this.ext = ext;
        }
        public String getExt() {
            return ext;
        }
    }

    public static void walkDirs(File root, SolidityListener listener) {
        if(root != null && listener != null){
            File[] list = root.listFiles();
            if (list == null) return;
            for ( File f : list ) {
                if (f.isDirectory()) {
                    listener.found(f);
                    walkFiles(f.getAbsoluteFile(), listener);
                }
            }
        }
    }

    public static File[] getAllDirs(File root){
        final List<File> list = new ArrayList<>();
        walkDirs(root, new SolidityListener(){
            @Override
            public void found(File file) {
                if(file.isDirectory() && !list.contains(file)) list.add(file);
            }
        });
        int size = list.size();
        return size>0?list.toArray(new File[size]):null;
    }

    public static void walkFiles(File root, SolidityListener listener) {
        if(root != null && listener != null){
            File[] list = root.listFiles();
            if (list == null) return;
            for ( File f : list ) {
                if (f.isDirectory()) walkFiles(f.getAbsoluteFile(), listener);
                else listener.found(f);
            }
        }
    }

    public static File[] getAll(File root, FileType... fileTypes){
        final List<File> list = new ArrayList<>();
        walkFiles(root, new SolidityListener(){
            @Override
            public void found(File file) {
                boolean valid = false;
                if(fileTypes != null && fileTypes.length > 0){
                    String ext = getExtension(file.getName()).toLowerCase();
                    for (FileType fileType:fileTypes) {
                        if(fileType.getExt().equals(ext)){
                            valid = true;
                            break;
                        }
                    }
                }
                else valid = true;
                if(valid && !list.contains(file)) list.add(file);
            }
        });
        int size = list.size();
        return size>0?list.toArray(new File[size]):null;
    }

    public static String getExtension(String filename) {
        String optional = null;
        if(filename != null && !filename.isEmpty()){
            optional = Optional.ofNullable(filename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(filename.lastIndexOf(".") + 1)).get();
        }
        return optional;
    }

    public static void deleteAll(File[] files){
        if(files != null && files.length>0){
            for (File file: files) file.delete();
        }
    }

    public interface SolidityListener {
        void found(File file);
    }

}
