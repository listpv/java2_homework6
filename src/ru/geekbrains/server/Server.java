package ru.geekbrains.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server
{
    private Vector<ClientHandler> clients;

    public Server()
    {
        ServerSocket server = null;
        Socket socket = null;

        try
        {
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен. Ожидаем подключения... .");
            clients = new Vector<>();

            while (true)
            {
                socket = server.accept();
                System.out.println("Клиент подключился.");
                clients.add(new ClientHandler(this, socket));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } finally
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
                server.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(String msg)
    {
        for (ClientHandler o: clients)
        {
            o.sendMsg(msg);
        }

    }

    public void removeClient(Socket sc)    // удаление элемента
    {
        for(ClientHandler o : clients)
        {
            if(o.getSocket() == sc)
            {
                clients.remove(o);
                System.out.println("Удалён.");
            }
        }
    }


}
