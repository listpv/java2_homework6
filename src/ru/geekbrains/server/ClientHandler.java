package ru.geekbrains.server;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler
{
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private String nick;

    public ClientHandler(Server server, Socket socket) {
        try
        {
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
                            if(str.startsWith("/"))
                            {
                                if (str.equals("/end"))
                                {
                                    out.writeUTF("/serverClosed");
                                    break;
                                }
                                if(str.startsWith("/w"))
                                {
                                    server.sendPersonalMessage(ClientHandler.this, str);
                                }
                                if (str.startsWith("/blacklist"))
                                {
                                    addToBlacklist(str);
                                }
                            }
                            else
                                {
                                    server.broadcastMsg(ClientHandler.this, str);
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


    // метод добавляет в чёрный списокю
    public void addToBlacklist(String str)
    {
        String[] tokens = str.split(" ");
        String sql = String.format("insert into blacklist (`snick`, `blnick`) VALUES ('%s', '%s')", nick, tokens[1]);
        try
        {
            AuthService.getStatement().executeQuery(sql);
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        sendMsg("Вы добавили пользователя " + tokens[1] + " в чёрный список.");

    }

    // метод проверяет, есть ли в чёрном списке.
    public boolean checkBlacklist(String nick)
    {
        String sql = String.format("select * from blacklist where snick = '%s' and blnick = '%s'", this.nick, nick);
        try
        {
            ResultSet rs = AuthService.getStatement().executeQuery(sql);
            if(rs.next())
            {
                return  true;
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return false;
    }

    public String getNick()
    {
        return nick;
    }
}
