package ru.geekbrains.console;

import javafx.fxml.Initializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
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

    public ClientConsole() throws IOException {
        try
        {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            sc = new Scanner(System.in);
            boolean flag = true;
            while (true)
            {
                String str = in.readUTF();
                if (str.equals("это сервер /end"))
                {
                    out.writeUTF("это клиент /end");
                    break;
                }
                System.out.println( str);
//                String scannerStr = sc.nextLine();
                String var;
                boolean stp = false;
                ArrayList<String> arString = new ArrayList<>();
                while (true)
                {
                    var = sc.nextLine();
                    if(var.equalsIgnoreCase("/end"))
                    {
                        stp = true;
                        break;
                    }
                    if(var.equalsIgnoreCase("*/*"))
                    {
                        break;
                    }
                    arString.add(var);
                }
                if(stp)
                {
                    out.writeUTF("это клиент /end");
                }
                else {
                    out.writeUTF("это клиент " + arString);
                }

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
/*        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    while (true)
                    {
//                        String scannerStr = sc.nextLine();
//                        out.writeUTF("это клиент " + scannerStr);
                        String str = in.readUTF();
                        if (str.equals("это сервер /end"))
                        {
                            break;
                        }
                        System.out.println( str);
                        String scannerStr = sc.nextLine();
                        out.writeUTF("это клиент " + scannerStr);

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();*/

    }

}
