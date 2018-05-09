package com.group1.artatawe.controllers;

import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Kristiyan Vladimirov 916747
 * ChatViewerController used to provide a graphical user interface to the user, from which he could communicate with
 * other sellers/buyers. It renders each two endpoints between two user or in other words a 'chat'. Each chat (if there
 * is more than one) is represented by its own box with the avatar of the recipient and his name. Additionally, there is
 * a notification functionality which enables the current user to see how many new messages have been sent to him by
 * opening the 'My messages' window.
 */
public class ChatViewerController {

    //Header Attributes
    @FXML StackPane topstack;
    @FXML Button currentlistings;
    @FXML Button home;
    @FXML Button logout;
    @FXML Button buttonMyGallery;
    @FXML ImageView profileimage;
    @FXML Button myMessages;

    //Chat attributes
    @FXML TilePane tilePaneChat;

    /**
     * The first method be run after this controller is called. It prepares the window before it is displayed to the
     * current user
     */
    public void initialize() {
        Main.checkedMessages = true;
        this.initializeHeader();
        this.fixTile();
        this.renderChats();

    }

    /**
     * Initializes the header elements of the window and adds the corresponding listeners to each button of the header
     */
    private void initializeHeader() {

        this.currentlistings.setOnMouseClicked(e -> Main.switchScene("CurrentListings"));
        this.profileimage.setImage(Main.accountManager.getLoggedIn().getAvatar());
        this.home.setOnMouseClicked(e -> Main.switchScene("Home"));
        this.currentlistings.setOnMouseClicked(e -> Main.switchScene("CreateListing"));
        this.logout.setOnMouseClicked(e -> Main.accountManager.logoutCurrentAccount());
        this.buttonMyGallery.setOnMouseClicked(e -> Main.switchScene("UserGallery"));

        this.topstack.setOnMouseClicked(e -> {
            if(this.profileimage.intersects(e.getX(), e.getY(), 0, 0)) {
                ProfileController.viewProfile(Main.accountManager.getLoggedIn());
            }
        });


    }

    /**
     * Adjusts the tile pane
     */
    private void fixTile() {
        tilePaneChat.setHgap(10.0);
        tilePaneChat.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
    }

    /**
     *  Renders the chats by looking up all of the chats the currently logged user has participated
     */
    private void renderChats() {
        long lastLoginDate = Main.accountManager.getLoggedIn().getPreLastLogin();
        Account currentUser = Main.accountManager.getLoggedIn();
        Main.messageManager.getChat(currentUser)
                .forEach(x -> {
                    long n = x.getNewMessagesFromThisChat(lastLoginDate);
                    Account a = x.getOtherAccount();
                    tilePaneChat.getChildren().add(this.container(a, n));
                });
    }

    /**
     * Creates the vbox container that would hold a chat
     * @return Node
     */
    private Node container(Account a, long newMessages) {
        VBox v = new VBox();
        v.setStyle("-fx-border-color: #111");
        v.setPadding(new Insets(10.0,10.0,10.0,10.0));
        v.setAlignment(Pos.CENTER);


        ImageView image = new ImageView(a.getAvatar());
        Label info;
        if (newMessages != 0) {
            info = new Label(newMessages + " new messages from: " + a.getUserName());
        } else {
            info = new Label("Messages from: " + a.getUserName());
        }
        info.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));

        v.setOnMouseClicked(e -> {
            this.messageWindow(a);
            this.correctMessageInfo(info, a.getUserName());
        });

        v.getChildren().addAll(image, info);
        return v;
    }

    /**
     * Opens the message window where the currently logged user could send a message to another user
     * @param a
     */
    private void messageWindow(Account a) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Message.fxml"));
            BorderPane someRoot = (BorderPane) loader.load();
            MessageController controller = loader.getController();

            controller.init(a);

            Scene scene = new Scene(someRoot);

            Stage stage= new Stage();
            stage.setScene(scene);
            stage.setTitle("Artatawe");

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the label to its default value when the user sees his new messages
     * @param info
     */
    private void correctMessageInfo(Label info, String name) {
        info.setText("Messages from: " + name);
    }
}
