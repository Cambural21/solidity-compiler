package org.cambural21.solidity.compiler;

public enum Option {

    O("-o"), OUT("--out"), BIN("--bin"), ABI("--abi"), PKG("--pkg"),
    LANG("--lang"), OPTIMIZE("--optimize"), OVERWRITE("--overwrite"),
    BASE_PATH("--base-path"), INCLUDE_PATH("--include-path");

    private final String cmd;

    Option (String cmd){
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }

}