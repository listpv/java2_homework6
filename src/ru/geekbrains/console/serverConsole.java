package ru.geekbrains.console;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class serverConsole
{
    ServerSocket serverSocket;
    Socket socket;
    DataInputStream in;
    DataOutput out;
    Scanner sc;

    public serverConsole()
    {
        try
        {
            serverSocket = new ServerSocket(8189);
            System.out.println("Сервер запущен, ожидаем подключения...");
            socket = serverSocket.accept();
            System.out.println("Клиент подключился");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            sc = new Scanner(System.in);
            while (true) {
                String scannerStr = sc.nextLine();
                out.writeUTF("это сервер " + scannerStr);
                String str = in.readUTF();
                if (str.equals("это клиент /end")) {
                    break;
                }
                System.out.println(str);
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
