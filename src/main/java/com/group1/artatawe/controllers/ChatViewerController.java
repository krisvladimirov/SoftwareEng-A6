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

    public void initialize() {
        this.initializeHeader();
        this.fixTile();
        this.renderChats();

    }

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

    private void renderChats() {

        Account currentUser = Main.accountManager.getLoggedIn();
        Main.messageManager.getChat(currentUser)
                .forEach(x -> {
                    Account a = x.getOtherAccount();
                    tilePaneChat.getChildren().add(this.container(a));
                });
    }

    /**
     * Creates the vbox container that would a chat
     * @return
     */
    private Node container(Account a) {
        VBox v = new VBox();
        v.setStyle("-fx-border-color: #111");
        v.setPadding(new Insets(10.0,10.0,10.0,10.0));
        v.setAlignment(Pos.CENTER);



        ImageView image = new ImageView(a.getAvatar());
        Label name = new Label("Messages from: " + a.getUserName());
        name.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));

        v.setOnMouseClicked(e -> {
            this.messageWindow(a);
        });

        v.getChildren().addAll(image, name);
        return v;
    }

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
}
