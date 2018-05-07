package com.group1.artatawe.controllers;

import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.communication.Chat;
import com.group1.artatawe.communication.Message;
import com.group1.artatawe.utils.AlertUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

public class MessageController {

    @FXML TextArea messageField;
    @FXML VBox messageDisplay;

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private Account recipient;
    private Chat chat;

    //TODO -> preload any previous messages from this chat

    /**
     *
     * @param account
     */
    public void init(Account account) {
        this.recipient = account;
        try {
            this.chat = Main.messageManager.getChat(Main.accountManager.getLoggedIn() ,recipient);
            this.renderMessages();
        } catch (NoSuchElementException e) {
            System.out.println("I create a new chat");
            chat = new Chat(Main.accountManager.getLoggedIn(), recipient);
            Main.messageManager.addChat(chat);
        }
    }

    /**
     *
     */
    private void renderMessages() {
        chat.getAllMessages().forEach(message -> {
            Node n = this.renderSingleMessage(message);
            messageDisplay.getChildren().addAll(n);
        });
    }

    private Node renderSingleMessage(Message m) {
        HBox h = new HBox();

        System.out.println("Width of Main vbox " + messageDisplay.getPrefWidth());
        h.setPrefWidth(messageDisplay.getPrefWidth());
        System.out.println("Width of hbox " + h.getPrefWidth());

        VBox v = new VBox();
        v.setSpacing(5);
        v.setPadding(new Insets(5,10,10,10));

        Label name = new Label(m.getSender().getUserName());
        Label date = new Label("Date: " + DATE_FORMAT.format(new Date(m.getDate())));
        Text message = new Text(m.getMessageValue());
        message.setWrappingWidth(h.getPrefWidth() * 0.70);

        if (m.getSender().equals(Main.accountManager.getLoggedIn())) {
            h.setAlignment(Pos.TOP_RIGHT);
            v.setAlignment(Pos.TOP_RIGHT);
            v.setStyle("-fx-border-color: #166fec");
            v.setPrefWidth(h.getPrefWidth() * 0.75);
        } else {
            h.setAlignment(Pos.TOP_LEFT);
            v.setAlignment(Pos.TOP_LEFT);
            v.setStyle("-fx-border-color: #da2827");
            v.setPrefWidth(h.getPrefWidth() * 0.75);
        }
        System.out.println("Width of child vbox " + v.getPrefWidth());
        v.getChildren().addAll(date, message);
        h.getChildren().addAll(v);

        return h;
    }

    /**
     *
     */
    public void message() {
        if (messageField.getText().isEmpty() && messageField == null) {
            AlertUtil.sendAlert(Alert.AlertType.ERROR, "Artatawe", "Please enter a message before you try to sent");
        } else {
            Message m = new Message(Main.accountManager.getLoggedIn(),
                                    recipient,
                                    messageField.getText().trim(),
                                    System.currentTimeMillis());
            messageField.clear();
            chat.addMessage(m);
            messageDisplay.getChildren().addAll(this.renderSingleMessage(m));

        }
    }
}
