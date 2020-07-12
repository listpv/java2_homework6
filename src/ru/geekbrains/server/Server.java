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
            AuthService.connect();
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен. Ожидаем подключения... .");
            clients = new Vector<>();

            while (true)
            {
                socket = server.accept();
                System.out.println("Клиент подключился.");
                new ClientHandler(this, socket);
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
            AuthService.disconnect();
        }
    }

    public void subscribe(ClientHandler client)
    {
        clients.add(client);
    }

    public void unsubscribe(ClientHandler client)
    {
        clients.remove(client);
    }

    public void broadcastMsg(String msg)
    {
        for (ClientHandler o: clients)
        {
            o.sendMsg(msg);
        }

    }

    public void broadcasting(String nick1, String nick2, String msg)
    {
        for(ClientHandler o: clients)
        {
            if(o.getNick().equalsIgnoreCase(nick1) || o.getNick().equalsIgnoreCase(nick2))
            {
                o.sendMsg(msg);
            }
        }
    }

    // метод, проверяющий наличие пользователя
    public boolean isNickAlready(String nick)
    {
        for (ClientHandler o: clients)
        {
            if(o.getNick().equals(nick))
            {
                return true;
            }
        }
        return false;
    }


}
