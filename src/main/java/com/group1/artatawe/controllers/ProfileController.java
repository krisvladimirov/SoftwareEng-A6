package com.group1.artatawe.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.group1.artatawe.Main;
import com.group1.artatawe.listings.Comment;
import com.group1.artatawe.utils.GridUtil;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.listings.Listing;

import com.group1.artatawe.utils.MonthlyBarChart;
import com.group1.artatawe.utils.WeeklyBarChart;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javax.swing.text.View;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Controller for "Profile.fxml"
 */
public class ProfileController {

    private static Account viewing = null;
    private boolean viewingOwnProfile = false;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    private static final long MONTH_IN_MILLISEC = 2629743000L;
    private static final long WEEK_IN_MILLISEC = 604800000L;

    //Stores index of listings in list view notificationsList
    private List<Listing> notificationsListings;

	//Header Attributes
	@FXML StackPane topstack;
	@FXML ImageView profileimage;
	@FXML Button home;
	@FXML Button currentlistings;
	@FXML Button createlisting;
	@FXML Button logout;
	@FXML Button buttonMyGallery;
    @FXML Button myMessages;

	//Profile Specific Attributes
	@FXML ImageView avatar;
	@FXML Button favbutton;
	@FXML Label firstname;
	@FXML Label lastname;
	@FXML Label username;
	@FXML Label lastseen;
	@FXML Button editaccount;
	@FXML Button editGalleries;
	@FXML Button showWeeklySalesGraphButton;
	@FXML Button showMonthlySalesGraphButton;

    @FXML GridPane selling;
    @FXML GridPane sold;
    @FXML GridPane wonauctions;

    // Messaging
    @FXML Button messageButton;

    @FXML
    ListView<String> notificationsList;

    public void initialize() {
        Account loggedIn = Main.accountManager.getLoggedIn();
        viewingOwnProfile = loggedIn.getUserName().equals(viewing.getUserName());

        notificationsListings = new ArrayList<>();

        this.initializeHeader();
        this.initializeFavButton();

        this.renderSellingListings();
        this.renderSoldListings();
        this.renderWonListings();
        this.putCursors();

        this.avatar.setImage(viewing.getAvatar());
        this.firstname.setText(viewing.getFirstName());
        this.lastname.setText(viewing.getLastName());
        this.username.setText(viewing.getUserName());
        this.lastseen.setText(DATE_FORMAT.format(new Date(viewing.getLastLogin())));

        if (!viewingOwnProfile) {
            this.messageButton.setVisible(true);
            this.editaccount.setVisible(false);
            this.notificationsList.setVisible(false);
        } else {
            this.notificationsList.setVisible(true);
            this.messageButton.setVisible(false);
            generateNotifications();
        }

        this.editaccount.setOnMouseClicked(e -> EditAccountController.editAccount());

        this.favbutton.setOnMouseClicked(e -> {
            if (!viewingOwnProfile) {
                if (loggedIn.isFavAccount(viewing)) {
                    loggedIn.removeFavAccounts(viewing);
                } else {
                    loggedIn.addFavAccount(viewing);
                }
                this.initializeFavButton();
            }
        });

        this.showWeeklySalesGraphButton.setOnMouseClicked(e -> renderWeeklyGraph());
        this.showMonthlySalesGraphButton.setOnMouseClicked(e -> renderMonthlyGraph());


    }

    /**
     * Generate notifications for logged in user
     */
    private void generateNotifications(){
        this.notificationsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {

                    ViewListingController.viewListing(notificationsListings.get(notificationsList.getSelectionModel().getSelectedIndices().get(0)));
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        for (Listing listing : Main.accountManager.getLoggedIn().getNewListings()) {
            this.notificationsList.getItems().add("New Auction: " + listing.getArtwork().getTitle());
            notificationsListings.add(listing);
        }

        for (Listing listing : Main.accountManager.getLoggedIn().getEndingListings()) {
            this.notificationsList.getItems().add("Auction Ending: " + listing.getArtwork().getTitle());
            notificationsListings.add(listing);
        }

        for (Listing listing : Main.accountManager.getLoggedIn().getNewBids()) {
            this.notificationsList.getItems().add("New Bid: " + listing.getArtwork().getTitle());
            notificationsListings.add(listing);
        }

        for (Listing listing : Main.accountManager.getLoggedIn().getLostListings()) {
            this.notificationsList.getItems().add("Lost Auction: " + listing.getArtwork().getTitle());
            notificationsListings.add(listing);
        }

        for (Comment comment : Main.notifications.getNewComments()) {
            Listing l = Main.listingManager.getListing(comment.getListingId());
            this.notificationsList.getItems().add("New comment on: " + l.getArtwork().getTitle());
            notificationsListings.add(l);
        }
    }

    /**
     * Open the profile GUI on a specific account
     *
     * @param account - The account to show the GUI of
     */
    public static void viewProfile(Account account) {
        viewing = account;
        Main.switchScene("Profile");
    }

	/**
	 * Initialize the header
	 */
	private void initializeHeader() {
		this.currentlistings.setOnMouseClicked(e -> Main.switchScene("CurrentListings"));
		this.profileimage.setImage(Main.accountManager.getLoggedIn().getAvatar());
		this.profileimage.setOnMouseClicked(e -> viewProfile(Main.accountManager.getLoggedIn()));
		this.createlisting.setOnMouseClicked(e -> Main.switchScene("CreateListing"));
		this.home.setOnMouseClicked(e -> Main.switchScene("Home"));
		this.logout.setOnMouseClicked(e -> Main.accountManager.logoutCurrentAccount());
		this.buttonMyGallery.setOnMouseClicked(e -> Main.switchScene("UserGallery"));
        this.myMessages.setOnMouseClicked(e -> Main.switchScene("ChatViewer"));

        //I could not get topstack to ignore the mouse event and let the child nodes handle it, so instead
        //we check where the click happened and what should actually of been clicked.
        this.topstack.setOnMouseClicked(e -> {
            if (this.profileimage.intersects(e.getX(), e.getY(), 0, 0)) {
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
        this.buttonMyGallery.setCursor(Cursor.HAND);
        this.myMessages.setCursor(Cursor.HAND);
        this.messageButton.setCursor(Cursor.HAND);
        this.favbutton.setCursor(Cursor.HAND);
        this.editaccount.setCursor(Cursor.HAND);
        this.showMonthlySalesGraphButton.setCursor(Cursor.HAND);
        this.showWeeklySalesGraphButton.setCursor(Cursor.HAND);
    }

    /**
     * Initialize the fav account button. Will set the formatting of the favourite button (gold or grey)
     * depending on if the viewing account is favourite.
     */
    private void initializeFavButton() {
        if (!viewingOwnProfile) {
            if (Main.accountManager.getLoggedIn().isFavAccount(viewing)) {
                this.favbutton.setStyle("-fx-background-color: #ffb938;");
            } else {
                this.favbutton.setStyle("-fx-background-color: lightgrey;");
            }
        } else {
            this.favbutton.setVisible(false);
        }
    }


    /**
     * Turns a Listing that is currently being sold, into a node
     *
     * @param listing - The listing to turn into a node
     * @return The node created
     */
    private Node getSellingListingNode(Listing listing) {
        HBox hbox = new HBox();

        Image image = listing.getArtwork().getImage();
        ImageView iv = new ImageView(image);
        iv.setPreserveRatio(true);
        iv.setFitHeight(120);
        iv.setFitWidth(120);

        ListView<String> info = new ListView<>();
        info.getItems().addAll(listing.getArtwork().getTitle(),
                listing.getArtwork().getArtist());

        if (listing.hasDescription()) {
            info.getItems().add(listing.getArtwork().getDescription());
        }

        info.setMaxHeight(125);

        if (listing.getCurrentBid() == null) {
            info.getItems().add("£" + listing.getReserve());
        } else {
            info.getItems().add("£" + listing.getCurrentBid().getPrice());
        }

        info.getItems().add(listing.getBidsLeft() + " bids remaining");

        hbox.setOnMouseClicked(e -> ViewListingController.viewListing(listing));
        hbox.getChildren().add(iv);
        hbox.getChildren().add(info);

        return hbox;
    }

    /**
     * Turns a Listing that has been sold, into a node
     *
     * @param listing - The listing to turn into a node
     * @return The node created
     */
    private Node getSoldListingNode(Listing listing) {
        HBox hbox = new HBox();

        Image image = listing.getArtwork().getImage();
        ImageView iv = new ImageView(image);
        iv.setPreserveRatio(true);
        iv.setFitHeight(120);
        iv.setFitWidth(120);

        ListView<String> info = new ListView<>();
        info.getItems().addAll(listing.getArtwork().getTitle(),
                listing.getArtwork().getArtist());

        if (listing.hasDescription()) {
            info.getItems().add(listing.getArtwork().getDescription());
        }

        info.setMaxHeight(125);

        if (listing.getCurrentBid() != null) {
            info.getItems().add("Sold for: £" + listing.getCurrentBid().getPrice());
            info.getItems().add("Winner: " + listing.getCurrentBid().getBidder());
        }

        hbox.setOnMouseClicked(e -> ViewListingController.viewListing(listing));
        hbox.getChildren().add(iv);
        hbox.getChildren().add(info);

        return hbox;
    }

    /**
     * Turns a Listing that has been won, into a node
     *
     * @param listing - The listing to turn into a node
     * @return The node created
     */
    private Node getWonListingNode(Listing listing) {
        HBox hbox = new HBox();

        Image image = listing.getArtwork().getImage();
        ImageView iv = new ImageView(image);
        iv.setPreserveRatio(true);
        iv.setFitHeight(120);
        iv.setFitWidth(120);

        ListView<String> info = new ListView<>();
        info.getItems().addAll(listing.getArtwork().getTitle(),
                listing.getArtwork().getArtist());

        if (listing.hasDescription()) {
            info.getItems().add(listing.getArtwork().getDescription());
        }

        info.setMaxHeight(125);

        if (listing.getCurrentBid() != null) {
            info.getItems().add("Purchased for: £" + listing.getCurrentBid().getPrice());
        }

        hbox.setOnMouseClicked(e -> ViewListingController.viewListing(listing));
        hbox.getChildren().add(iv);
        hbox.getChildren().add(info);

        return hbox;
    }

    /**
     * Get all the listings the viewing account is selling and add it to the box
     */
    private void renderSellingListings() {
        LinkedList<Node> nodes = new LinkedList<>();

        viewing.getHistory().getSellingListings().stream()
                .forEach(listing -> nodes.add(this.getSellingListingNode(listing)));

        GridUtil.insertList(this.selling, nodes);
    }

    /**
     * Get all the listings the viewing account has sold and add it to the box
     */
    private void renderSoldListings() {
        LinkedList<Node> nodes = new LinkedList<>();

        viewing.getHistory().getSoldListings().stream()
                .forEach(listing -> nodes.add(this.getSoldListingNode(listing)));

        GridUtil.insertList(this.sold, nodes);
    }

    /**
     * Get all the listings the viewing account has one and add it to the box
     */
    private void renderWonListings() {
        LinkedList<Node> nodes = new LinkedList<>();

        viewing.getHistory().getWonListings().stream()
                .forEach(listing -> nodes.add(this.getWonListingNode(listing)));

        GridUtil.insertList(this.wonauctions, nodes);
    }

    /**
     * Method to render and display (in a pop up) a bar chart (week).
     */
	private void renderWeeklyGraph() {
        getWeeklyGraphData();
		BarChart<String, Number> wkChart = WeeklyBarChart.start();
		VBox vbox = new VBox();
		vbox.getChildren().add(wkChart);
		Popup graphPopup = new Popup();
		graphPopup.getContent().add(vbox);

		graphPopup.setHideOnEscape(true);
		graphPopup.setAutoHide(true);

		vbox.setPrefWidth(1000);
		vbox.setPrefHeight(wkChart.getPrefHeight());
		vbox.setStyle("-fx-background-color: lime; -fx-padding: 10;");

		graphPopup.show(topstack.getScene().getWindow());
	}

    /**
     * Method to render and display (in a pop up) a bar chart (month).
     */
	private void renderMonthlyGraph() {
        getMonthlyGraphData();
		BarChart<String, Number> mChart = MonthlyBarChart.start();
		VBox vbox = new VBox();
		vbox.getChildren().add(mChart);
		Popup graphPopup = new Popup();
		graphPopup.getContent().add(vbox);

		graphPopup.setHideOnEscape(true);
		graphPopup.setAutoHide(true);

		vbox.setPrefWidth(1000);
		vbox.setPrefHeight(mChart.getPrefHeight());
		vbox.setStyle("-fx-background-color: hotpink; -fx-padding: 10;");

		graphPopup.show(topstack.getScene().getWindow());
	}

    /**
     * Method to populate the variables used in creating a bar chart (month)
     */
	public void getWeeklyGraphData() {
	    WeeklyBarChart.reset();

        long currentTime = Calendar.getInstance().getTime().getTime();
        long currentUnixWeek = currentTime / WEEK_IN_MILLISEC;

        for (Listing l:  Main.accountManager.getLoggedIn().getHistory().getSoldListings()) {

            long elemTime = l.getBidHistory().getCurrentBid().getDate();
            long elemWeek = elemTime / WEEK_IN_MILLISEC;

            int elemWeeksAgo = (int) (currentUnixWeek - elemWeek); //Rounds down

            if (elemWeeksAgo < WeeklyBarChart.getNumOfWeeks()) {

                if (l.getArtwork().getClass().getSimpleName().equals("Painting")) {

                    WeeklyBarChart.setPaintingSalesWk(elemWeeksAgo, (WeeklyBarChart.getPaintingSalesWk(elemWeeksAgo) + 1));

                } else if (l.getArtwork().getClass().getSimpleName().equals("Sculpture")) {

                    WeeklyBarChart.setSculptureSalesWk(elemWeeksAgo, (WeeklyBarChart.getSculptureSalesWk(elemWeeksAgo) + 1));

                } else {
                    System.out.println("ERROR CODE 006");
                }
            }
        }


    }

    /**
     * Method to populate the variables used in creating a bar chart (month)
     */
    public void getMonthlyGraphData() {
        MonthlyBarChart.reset();
        long currentTime = Calendar.getInstance().getTime().getTime();
        long currentUnixMonth = currentTime / MONTH_IN_MILLISEC;

        for (Listing l : Main.accountManager.getLoggedIn().getHistory().getSoldListings()) {

            long elemTime = l.getBidHistory().getCurrentBid().getDate();
            long elemMonth = elemTime / MONTH_IN_MILLISEC;

            int elemMonthsAgo = (int) (currentUnixMonth - elemMonth); //Rounds down

            if (elemMonthsAgo < MonthlyBarChart.getNumOfMonths()) {

                if (l.getArtwork().getClass().getSimpleName().equals("Painting")) {

                    MonthlyBarChart.setPaintingSalesM(elemMonthsAgo, (MonthlyBarChart.getPaintingSalesM(elemMonthsAgo) + 1));

                } else if (l.getArtwork().getClass().getSimpleName().equals("Sculpture")) {

                    MonthlyBarChart.setSculptureSalesM(elemMonthsAgo, (MonthlyBarChart.getSculptureSalesM(elemMonthsAgo) + 1));

                } else {
                    System.out.println("ERROR CODE 007");
                }
            }
        }

    }

    /**
     * Sends a message to a user
     */
    public void sendMessage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Message.fxml"));
            BorderPane someRoot = (BorderPane) loader.load();
            MessageController controller = loader.getController();

            controller.init(viewing);

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