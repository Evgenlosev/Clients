package sample3;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Controller3 {
    public static Socket socket;
    public static DataInputStream in;
    public static DataOutputStream out;
    @FXML
    TextArea mainChatArea;
    @FXML
    TextField loginField;
    @FXML
    TextField passField;
    @FXML
    TextField messageField;
    private String myNick;

    public void start() {
        try {
            socket = new Socket("localhost", 8189);
            socket.setSoTimeout(120_000);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/authok ")) {
                            socket.setSoTimeout(0);
                            myNick = str.split("\\s")[1];
                        }
                        mainChatArea.appendText(str + "\n");
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }

            });
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onAuthClick() {
        if (socket == null || socket.isClosed()) {
            start();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
            loginField.setText("");
            passField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        if (!messageField.getText().trim().isEmpty()) {
            try {
                out.writeUTF(messageField.getText().trim());
                messageField.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
