package org.cambural21.solidity;

import java.io.*;
import java.util.Scanner;

public class InteractiveOptions {

    private final Scanner scanner;
    private final Writer writer;

    InteractiveOptions() {
        this(System.in, System.out);
    }

    InteractiveOptions(final InputStream inputStream, final OutputStream outputStream) {
        this.scanner = new Scanner(inputStream);
        this.writer = new PrintWriter(outputStream);
    }

    private void print(final String text) {
        System.out.println(text);
    }

    private String getUserInput() {
        return scanner.nextLine();
    }

    protected  File getSolidityFile() throws IOException {
        print("Please enter the solidity path file (Required Field): ");
        File file = new File(getUserInput()).getAbsoluteFile();
        print(file.getAbsolutePath());
        if(!file.exists()) throw new IOException("Solidity file is null");
        return file;
    }

    protected final File getProjectDestination() {
        print("Please enter the destination of your project (Required Field): ");
        File file = new File(getUserInput()).getAbsoluteFile();
        print(file.getAbsolutePath());
        if(!file.exists()) file.mkdirs();
        return file;
    }

    public boolean isInteractive(String[] args){
        boolean interactive = args == null || args.length <= 0;
        return interactive;
    }

    protected final String getPackageName() {
        print("Please enter the package name for your project (Required Field): ");
        String input = getUserInput();
        print(input);
        return input;
    }

    public boolean isUI(String[] args){
        boolean ui = args != null &&  args.length == 1 && args[0] != null && args[0].equals("--ui");
        return ui;
    }

}
