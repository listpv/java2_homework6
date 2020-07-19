package ru.geekbrains.client;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller
{

    @FXML
    TextField textField;



    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADRESS = "localhost";
    final int PORT = 8189;

    @FXML
    HBox bottomPanel;

    @FXML
    HBox upperPanel;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    ListView<String> clientList;

    @FXML
    VBox vBoxChat;

    private boolean isAuthorized;

    public void setAuthorized(boolean isAuthorized)
    {
        this.isAuthorized = isAuthorized;
        if(!isAuthorized)
        {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            clientList.setManaged(false);
            clientList.setVisible(false);
//            vBoxChat.getChildren().clear();

        }
        else
            {
                upperPanel.setVisible(false);
                upperPanel.setManaged(false);
                bottomPanel.setVisible(true);
                bottomPanel.setManaged(true);
                clientList.setManaged(true);
                clientList.setVisible(true);
//                vBoxChat.getChildren().clear();
            }
    }


    public void connect()
    {
        try
        {
            socket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        while (true)
                        {
                            String str = in.readUTF();
                            if(str.startsWith("/auth o'k"))
                            {
                                setAuthorized(true);
                                break;
                            }
                            else
                                {
                                    Platform.runLater(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Label label = new Label(str + "\n");
                                            VBox vBox = new VBox();
                                            vBox.setAlignment(Pos.TOP_CENTER);
                                            vBox.getChildren().add(label);
                                            vBoxChat.getChildren().add(vBox);
                                        }
                                    });
                                }
                        }
                        while (true)
                        {
                            String str = in.readUTF();
                            if(str.startsWith("/"))
                            {
                                if (str.equalsIgnoreCase("/serverClosed"))
                                {
                                    break;
                                }
                                if(str.startsWith("/clientlist "))
                                {
                                    String[] tokens = str.split(" ");
                                    Platform.runLater(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            clientList.getItems().clear();
                                            for(int i = 1; i < tokens.length; i++)
                                            {
                                                clientList.getItems().add(tokens[i]);
                                            }
                                        }
                                    });
                                }
                            }
                            else
                                {
                                    // здесь !!!
                                    String[] tokens = str.split(" ", 2);
                                    Platform.runLater(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Label label = new Label(tokens[1] + "\n");
                                            VBox vBox = new VBox();
                                            if(tokens[0].equals("@!@FROM"))
                                            {
                                                vBox.setAlignment(Pos.TOP_LEFT);
                                            }
                                            else
                                                {
                                                    vBox.setAlignment(Pos.TOP_RIGHT);
                                                }
                                            vBox.getChildren().add(label);
                                            vBoxChat.getChildren().add(vBox);
                                        }
                                    });

                                }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setAuthorized(false);
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void sendMessage()
    {
        try
        {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void tryToAuth(javafx.event.ActionEvent actionEvent)
    {
        if(socket == null || socket.isClosed())
        {
            connect();
        }

        try
        {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
