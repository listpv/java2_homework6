package ru.geekbrains.console;

import javafx.fxml.Initializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ClientConsole
{
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    final String IP_ADDRESS = "localhost";
    final int PORT = 8189;
    Scanner sc;

    public ClientConsole()
    {
        try
        {
            socket = new Socket(IP_ADDRESS, PORT);
            System.out.println("Client");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            sc = new Scanner(System.in);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    while (true)
                    {
                        String scannerStr = sc.nextLine();
                        out.writeUTF("это клиент " + scannerStr);
                        String str = in.readUTF();
                        if (str.equals("это сервер /end"))
                        {
                            break;
                        }
                        System.out.println( str);

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
