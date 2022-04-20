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

    protected  File getSolidityFile() throws IOException {
        print("Please enter the solidity path file (Required Field): ");
        File file = new File(getUserInput()).getAbsoluteFile();
        if(!file.exists()) throw new IOException("Solidity file is null");
        return file;
    }

    protected final String getPackageName() {
        print("Please enter the package name for your project (Required Field): ");
        return getUserInput();
    }

    protected final File getProjectDestination() {
        print("Please enter the destination of your project (Required Field): ");
        File file = new File(getUserInput()).getAbsoluteFile();
        if(!file.exists()) file.mkdirs();
        return file;
    }

    String getUserInput() {

        return scanner.nextLine();
    }

    private void print(final String text) {
        System.out.println(text);
    }
}
