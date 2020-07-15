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
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler client)
    {
        clients.remove(client);
        broadcastClientList();
    }

    public void broadcastMsg(ClientHandler from, String msg)
    {
        for (ClientHandler o: clients)
        {
            if(!o.checkBlacklist(from.getNick()))
            {
                if(!o.getNick().equals(from.getNick()))
                {
                    o.sendMsg("@!@FROM from " + from.getNick() + ": " + msg);
                }
                else
                {
                    from.sendMsg("@!@TO to everyone : " + msg);
                }
            }
        }

    }

    public void sendPersonalMessage(ClientHandler from, String str)
    {

        String[] tokens = str.split(" ", 3);
        if (!isNickAlready(tokens[1]))
        {
            from.sendMsg("@!@TO Пользователь " + tokens[1] + " не активен.");
            return;
        }
        for(ClientHandler o: clients)
        {
            if(o.getNick().equals(tokens[1]))
            {
                if(o.checkBlacklist(from.getNick()))
                {
                    return;
                }
                o.sendMsg("@!@FROM from " + from.getNick() + ": " + tokens[2]);
                from.sendMsg("@!@TO to " + tokens[1] + ": " + tokens[2]);
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

    public void broadcastClientList()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("/clientlist ");
        for(ClientHandler o : clients)
        {
            sb.append(o.getNick() + " ");
        }
        String out = sb.toString();

        for(ClientHandler o : clients)
        {
            o.sendMsg(out);
        }
    }


}
