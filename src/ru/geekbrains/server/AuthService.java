package ru.geekbrains.server;

import org.jetbrains.annotations.Nullable;

import javax.xml.transform.Result;
import java.sql.*;

public class AuthService
{
    private static Connection connection;
    private static Statement statement;

    public static void connect()      // подключение БД
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mainDB");
            statement = connection.createStatement();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static @Nullable String getNickByLogAndPass(String login, String pass)
    {
        String sql = String.format("select nickname from main where login = '%s' and password = '%s'", login, pass);
        try
        {
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next())
            {
                return rs.getString(1);
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void disconnect()
    {
        try
        {
            connection.close();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    public static Statement getStatement()
    {
        return statement;
    }
}
