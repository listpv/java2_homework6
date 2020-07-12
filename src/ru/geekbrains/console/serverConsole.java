package ru.geekbrains.console;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class serverConsole
{
    ServerSocket serverSocket;
    Socket socket;
    DataInputStream in;
    DataOutput out;
    Scanner sc;
    final int PORT = 8189;

    public serverConsole()
    {
        try
        {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен, ожидаем подключения...");
            socket = serverSocket.accept();
            System.out.println("Клиент подключился");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            sc = new Scanner(System.in);
            boolean flag = true;
            while (true)
            {
                if(flag)
                {
//                    String scannerStr = sc.nextLine();
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
                        out.writeUTF("это сервер /end");
                    }
                    else
                        {
                        out.writeUTF("это сервер " + arString.toString());
                    }
                    flag = false;
                }
                String str = in.readUTF();
                if (str.equals("это клиент /end"))
                {
                    out.writeUTF("это сервер /end");
                    break;
                }
                System.out.println(str);
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
                    out.writeUTF("это сервер /end");
                }
                else {
                    out.writeUTF("это сервер " + arString);
                }
            }
        }
        catch (IOException e) {
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
            try
            {
                serverSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
