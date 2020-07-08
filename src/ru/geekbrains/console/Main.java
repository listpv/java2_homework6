package ru.geekbrains.console;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                new serverConsole();
            }
        }).start();
        new ClientConsole();


    }
}
