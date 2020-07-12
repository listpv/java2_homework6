package ru.geekbrains.console;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException
    {
//////    для окончания набора в консоли */* , для завершения работы /end /
        new Thread(new Runnable() {
            @Override
            public void run() {
                new serverConsole();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new ClientConsole();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
