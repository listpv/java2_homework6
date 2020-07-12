package ru.geekbrains.server;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler
{
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private String nick;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // блок идентификации
                        while (true)
                        {
                            String str = in.readUTF();
                            if(str.startsWith("/auth"))
                            {
                                String[] tokens = str.split(" ");
                                String newNick = AuthService.getNickByLogAndPass(tokens[1], tokens[2]);
                                if(newNick == null)
                                {
                                    sendMsg("Неверный логин/пароль.");
                                }
                                else if (server.isNickAlready(newNick))
                                {
                                    sendMsg("Пользователь " + newNick + " уже  активен.");
                                }
                                else
                                    {
                                        sendMsg("/auth o'k");
                                        nick = newNick;
                                        server.subscribe(ClientHandler.this);
                                        break;
                                    }
                            }
                        }
                        //
                        while (true)
                        {
                            String str = in.readUTF();
                            if (str.equals("/end"))
                            {
                                out.writeUTF("/serverClosed");
                                break;
                            }
                            else if(str.startsWith("/w"))
                            {
                                isOneClient(str);
                            }
                            else
                                {
                                    server.broadcastMsg(nick + ": " + str);
                                }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        try
                        {
                            out.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        try
                        {
                            socket.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        server.unsubscribe(ClientHandler.this);
                    }

                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // метод, обрабатывающий личные сообщения.
    public void isOneClient(@NotNull String str)
    {
        String[] tokens = str.split(" ");
        if (!server.isNickAlready(tokens[1]))
        {
            sendMsg("Пользователь " + tokens[1] + " не активен.");
            return;
        }
        String string ="";
        for(int i = 2; i < tokens.length; i++)
        {
            string += tokens[i];
            if(i < (tokens.length - 1))
            {
                string += " ";
            }

        }
        server.broadcasting(nick, tokens[1], nick + ": " + string);
    }

    public String getNick()
    {
        return nick;
    }
}
