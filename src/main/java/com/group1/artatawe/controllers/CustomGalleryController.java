package com.group1.artatawe.controllers;

import com.group1.artatawe.Main;
import com.group1.artatawe.artwork.Gallery;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Adam Payne
 */
public class CustomGalleryController {
    //Header Attributes
    @FXML StackPane topstack;
    @FXML ImageView profileimage;
    @FXML Button home;
    @FXML Button currentlistings;
    @FXML Button createlisting;
    @FXML Button logout;
    @FXML Button buttonMyGalleries;
    @FXML Button myMessages;
    @FXML Button buttonEdit;

    @FXML VBox galleryBox;

    public void initialize() {

        this.initializeHeader();
        this.anyGalleries();
        this.putCursors();
        this.buttonEdit.setOnMouseClicked(event -> {
            this.galleryMenuPopup();
            this.anyGalleries();
        });

        }

    /**
     * Initializes all of the header elements such as linking buttons
     */
    private void initializeHeader() {
        this.currentlistings.setOnMouseClicked(e -> Main.switchScene("CurrentListings"));
        this.profileimage.setImage((Main.accountManager.getLoggedIn().getAvatar()));
        this.createlisting.setOnMouseClicked(e -> Main.switchScene("CreateListing"));
        this.home.setOnMouseClicked(e -> Main.switchScene("Home"));
        this.logout.setOnMouseClicked(e -> Main.accountManager.logoutCurrentAccount());
        this.myMessages.setOnMouseClicked(e -> Main.switchScene("ChatViewer"));

        this.topstack.setOnMouseClicked(e -> {
            if(this.profileimage.intersects(e.getX(), e.getY(), 0, 0)) {
                ProfileController.viewProfile(Main.accountManager.getLoggedIn());
            }
        });
        this.updateMessageNotifyer();
    }

    /**
     * Updates the Message button by including how many new messages the currently logged user has
     */
    private void updateMessageNotifyer() {
        int n = Main.notifications.howManyNewMessages();
        if (n != 0 && n > 0 && !Main.checkedMessages) {
            myMessages.setText("My Messages (" + n + ")");
        } else {
            myMessages.setText("My Messages");
        }
    }

    /**
     * Puts cursors to all of the buttons inside this window
     */
    private void putCursors() {
        this.currentlistings.setCursor(Cursor.HAND);
        this.createlisting.setCursor(Cursor.HAND);
        this.home.setCursor(Cursor.HAND);
        this.logout.setCursor(Cursor.HAND);
        this.myMessages.setCursor(Cursor.HAND);
        this.profileimage.setCursor(Cursor.HAND);
    }

    /**
     * Checks if the user has any galleries, if not a notification is sent
     */
    private void anyGalleries() {

        int size = Main.accountManager.getLoggedIn().getUserGalleries().size(); // all galleries

        if (size == 0) {
            galleryBox.getChildren().clear();
            noGalleries();

        } else {
            galleryBox.getChildren().clear();
            this.renderGalleriesNu();

        }
    }

    /**
     * Renders all artworks/listings from all galleries a user has
     */
    private void renderGalleriesNu() {
        Main.accountManager.getLoggedIn().getUserGalleries().stream().forEach(gallery -> {
            VBox galVert = new VBox();
            galVert.setPrefWidth(this.galleryBox.getPrefWidth());
            this.galleryBox.getChildren().add(galVert);

            Label galTitle = new Label(gallery.getName());
            galVert.getChildren().add(galTitle);

            ScrollPane galList = new ScrollPane();
            galVert.getChildren().add(galList);

            HBox galBox = new HBox();
            galList.setContent(galBox);

            gallery.getListings().stream().forEach(listing -> {
                String title = listing.getArtwork().getTitle();
                Image image = listing.getArtwork().getImage();
                ImageView iv = makeImgView(image);
                VBox v = galleryNode(iv, title);
                v.setOnMouseClicked(e -> ViewListingController.viewListing(listing));
                galBox.getChildren().add(v);

            });
        });
    }

    /**
     * Creates a VBox container
     * @param iv ImageView which would store a Listings image
     * @param title The title fo a gallery
     * @return A gallery node representing a single Listing of a gallery
     */
    private VBox galleryNode(ImageView iv, String title) {

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefSize(150.0, 150.0);

        Label label = new Label(title);
        vbox.getChildren().addAll(label, iv);

        return vbox;
    }

    /**
     * A method to notify the user that currently he does not have any custom galleries
     */
    private void noGalleries() {

        Label l = new Label("You do not have any galleries");
        l.setFont(new Font("Calibri", 24));

        this.galleryBox.getChildren().add(l);

    }

    /**
     * Makes an image view which would be added to a VBox container holding a listing from a particular gallery
     * @param image The image file (main image) corresponding to a Listing
     * @return A ImageView container holding a Listings image
     */
    private ImageView makeImgView(Image image) {

        ImageView im = new ImageView();

        im.setFitHeight(126.0);
        im.setFitWidth(126.0);
        im.setPreserveRatio(false);
        im.setImage(image);

        return im;

    }

    private void galleryMenuPopup() {
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/GalleryOptions.fxml"));

            BorderPane someRoot = (BorderPane) loader.load();

            GalleryOptionsController controller = loader.getController();

            Scene defaultAvatarScene = new Scene(someRoot);

            Stage defaultAvatarStage = new Stage();
            defaultAvatarStage.setScene(defaultAvatarScene);
            defaultAvatarStage.setTitle("Artatawe");

            defaultAvatarStage.initModality(Modality.APPLICATION_MODAL);

            defaultAvatarStage.showAndWait();

            Main.accountManager.saveAccountFile();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
