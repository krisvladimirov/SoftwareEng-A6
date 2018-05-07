package com.group1.artatawe.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.artwork.Gallery;
import com.group1.artatawe.artwork.Painting;
import com.group1.artatawe.artwork.Sculpture;
import com.group1.artatawe.communication.Chat;
import com.group1.artatawe.listings.Bid;
import com.group1.artatawe.listings.Comment;
import com.group1.artatawe.listings.Listing;
import com.group1.artatawe.listings.ListingState;
import com.group1.artatawe.utils.AlertUtil;
import com.group1.artatawe.utils.NumUtil;
import com.group1.artatawe.utils.WeeklyBarChart;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 * Controller for "ViewListing.fxml"
 */
public class ViewListingController {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	private static final double MAX_POPUP_IMG_SIZE = 600;

	//The listing that is being viewed
	private static Listing viewing = null;

	//If the account is viewing their own listing
	private static boolean viewingOwnListing = false;

	//Header Attributes
	@FXML StackPane topstack;
	@FXML ImageView profileimage;
	@FXML Button home;
	@FXML Button currentlistings;
	@FXML Button createlisting;
	@FXML Button logout;
	@FXML Button buttonMyGallery;

	//Listing Attributes
	@FXML ImageView image;
	@FXML Label title;
	@FXML VBox infobox;
	@FXML Label currentbid;
	@FXML Label bidsleft;
	@FXML TextField amount;
	@FXML Button placebid;
	@FXML VBox bidhistorybox;

	//Seller Attributes
	@FXML ImageView selleravatar;
	@FXML Label sellername;

	@FXML Button buttonAddCustomGallery;
	@FXML MenuButton menuGallery;

	//Commenting Attributes
    @FXML Label commentsNum;
    @FXML TextArea commentSection;
    @FXML Button commentButton;
    @FXML TilePane tileComments;
    @FXML RadioMenuItem menuTopComments;
    @FXML RadioMenuItem menuNewestComments;

    //Message button
    @FXML Button messageButton;

	//Gallery variables
	private static final String ADD_BTN_MSG = "Add to gallery";
	private static final String RMV_BTN_MSG = "Remove from gallery";

	private Gallery currentGallery = null;

	public void initialize() {
		this.initializeHeader();
		this.fixTileComments();
		menuTopComments.setSelected(true);

		this.title.setText(viewing.getArtwork().getTitle());

		this.placebid.setOnMouseClicked(e -> this.handleBid());

		//Display the Artwork's main image
		this.image.setImage(viewing.getArtwork().getImage());
		this.image.setOnMouseClicked(e -> this.viewLargeImage(viewing.getArtwork().getImage()));

		//If a user is viewing their own Listing, hide the bidding UI
		if(viewingOwnListing) {
			this.amount.setVisible(false);
			this.placebid.setVisible(false);
            this.messageButton.setVisible(false);
		}

		if(viewing.getListingState() != ListingState.ACTIVE) {
			this.amount.setVisible(false);
			this.placebid.setVisible(false);
			this.bidsleft.setText("Auction Has Ended.");
		}



		//Set seller information
		Account seller = Main.accountManager.getAccount(viewing.getSeller());

		this.selleravatar.setImage(seller.getAvatar());
		this.sellername.setText(seller.getFirstName() + " " + seller.getLastName());

		this.selleravatar.setOnMouseClicked(e -> ProfileController.viewProfile(seller));
		this.sellername.setOnMouseClicked(e -> ProfileController.viewProfile(seller));

		//Create a new gallery button
		MenuItem newGalleryOpt = new MenuItem("New");
		newGalleryOpt.setOnAction(event -> {
			menuGallery.setText("New");
			currentGallery = null;
		});
        menuGallery.getItems().add(newGalleryOpt);
		menuGallery.setText("New");

		//Add the user's galleries to the menu
		for (Gallery gallery : Main.accountManager.getLoggedIn().getUserGalleries()) {
			MenuItem menuItem = new MenuItem(gallery.getName());

			//Update when a menuItem is selected
			menuItem.setOnAction(event -> {
				/*
					Sets the text to the general menu button to the picked gallery
				 */
				menuGallery.setText(menuItem.getText());
				currentGallery = gallery;

				//Change the button text to reflect whether this will add to or remove from the gallery
				if (gallery.containsListing(viewing)) {
					buttonAddCustomGallery.setText(RMV_BTN_MSG);
				} else {
					buttonAddCustomGallery.setText(ADD_BTN_MSG);
				}
			});

			menuGallery.getItems().add(menuItem);
		}

		//Render the different parts of the Listing view
		this.displayCurrentBid();
		this.displayBidHistory();
		this.renderInfo();
		this.renderComments();
	}

	public void updateGalleryMenu(String name, Gallery g) {
		MenuItem item = new MenuItem(name);
		item.setOnAction(event -> {
			/*
					Sets the text to the general menu button to the picked gallery
				 */
			menuGallery.setText(item.getText());
			currentGallery = g;

			//Change the button text to reflect whether this will add to or remove from the gallery
			if (g.containsListing(viewing)) {
				buttonAddCustomGallery.setText(RMV_BTN_MSG);
			} else {
				buttonAddCustomGallery.setText(ADD_BTN_MSG);
			}
		});
		menuGallery.getItems().add(item);
	}

	/**
	 * View a listing
	 * @param listing - The listing to view
	 */
	public static void viewListing(Listing listing) {
		viewing = listing;
		viewingOwnListing = Main.accountManager.getLoggedIn().getUserName().equals(listing.getSeller());

		Main.switchScene("ViewListing");
	}

	/**
	 * Initialize the header
	 */
	private void initializeHeader() {
		this.currentlistings.setOnMouseClicked(e -> Main.switchScene("CurrentListings"));
		this.profileimage.setImage(Main.accountManager.getLoggedIn().getAvatar());
		this.profileimage.setOnMouseClicked(e -> ProfileController.viewProfile(Main.accountManager.getLoggedIn()));
		this.createlisting.setOnMouseClicked(e -> Main.switchScene("CreateListing"));
		this.home.setOnMouseClicked(e -> Main.switchScene("Home"));
		this.logout.setOnMouseClicked(e -> Main.accountManager.logoutCurrentAccount());
		this.buttonMyGallery.setOnMouseClicked(e -> Main.switchScene("UserGallery"));

		//I could not get topstack to ignore the mouse event and let the child nodes handle it, so instead
		//we check where the click happened and what should actually of been clicked.
		this.topstack.setOnMouseClicked(e -> {
			if(this.profileimage.intersects(e.getX(), e.getY(), 0, 0)) {
				ProfileController.viewProfile(Main.accountManager.getLoggedIn());
			}
		});
	}

    /**
     * Fixes the tile pane containing all comments
     */
	private void fixTileComments() {
		tileComments.setVgap(10.0);
	    tileComments.setHgap(10.0);
        tileComments.setPadding(new Insets(10.0,10.0,10.0,10.0));
    }

	/**
	 * Handle the bid button
	 */
	private void handleBid() {
		//The UI allowing users to bid should be invisible to the Listings creator.
		if(viewingOwnListing) {
			throw new IllegalStateException("A user cannot bid on their own listing!");
		}

		if(viewing.getListingState() != ListingState.ACTIVE) {
			throw new IllegalStateException("Tried to bid on a listing that is not active!");
		}

		if(viewing.getCurrentBid() != null) {
			if(viewing.getCurrentBid().getBidder().equalsIgnoreCase(Main.accountManager.getLoggedIn().getUserName())) {
				AlertUtil.sendAlert(AlertType.INFORMATION, "Wow Buddy!", "You are already the highest bidder");
				return;
			}
		}

		double prevAmt = viewing.getCurrentBid() == null ? viewing.getReserve() : viewing.getCurrentBid().getPrice();
		double amount = 0;

		try {
			amount = NumUtil.toDouble(this.amount.getText(), num -> num.doubleValue() > prevAmt);
		} catch(Exception e) {
			AlertUtil.sendAlert(AlertType.ERROR, "Invalid bid", "The amount you have entered is either invalid, or not high enough. Try again.");
			return;
		}

		viewing.createBid(amount, Main.accountManager.getLoggedIn());

		this.displayBidHistory();
		this.displayCurrentBid();

		if(viewing.getListingState() == ListingState.FINISHED) {
			this.placebid.setVisible(false);
			this.amount.setVisible(false);
			this.bidsleft.setText("Auction Has Ended");
			AlertUtil.sendAlert(AlertType.INFORMATION, "Success!", "You have won the auction!");
		} else {
			AlertUtil.sendAlert(AlertType.INFORMATION, "Success!", "Your bid has been placed. Good luck!");
		}
	}

	/**
	 * Update the current bid
	 */
	private void displayCurrentBid() {
		Bid current = viewing.getCurrentBid();

		if(viewing.getListingState() != ListingState.ACTIVE) {
			this.bidsleft.setText("Auction Has Ended");
		} else {
			this.bidsleft.setText(viewing.getBidsLeft() + "");
		}


		if(current != null) {
			this.currentbid.setText("£" + current.getPrice() + " (" + current.getBidder() + ")");
			this.currentbid.setOnMouseClicked(e -> {
				ProfileController.viewProfile(Main.accountManager.getAccount(current.getBidder()));
			});
		} else {
			this.currentbid.setText("£" + viewing.getReserve());
		}
	}

	/**
	 * Render all the bids into the bid history box.
	 * 
	 * Is the creator of the Listing is viewing it, then the entire history will be displayed.
	 * If another user is viewing the Listing, then they will only see their bids.
	 */
	private void displayBidHistory() {
		this.bidhistorybox.getChildren().clear();

		for(Bid bid : viewing.getBidHistory().getAllBids()) {
			if(viewingOwnListing) {
				//Creator of listing can see complete history
				this.renderBidIntoHistory(bid);

			} else if(Main.accountManager.getLoggedIn().getUserName().equalsIgnoreCase(bid.getBidder())) {
				//Otherwise users can only see their bid history
				this.renderBidIntoHistory(bid);
			}
		}
	}

	/**
	 * Render a single bid into the history box
	 * @param bid - The bid to render
	 */
	private void renderBidIntoHistory(Bid bid) {
		Account bidder = Main.accountManager.getAccount(bid.getBidder());

		VBox vbox = new VBox(2);

		vbox.setAlignment(Pos.CENTER);
		vbox.setStyle("-fx-border-color: lightgrey;");

		ImageView avatar = new ImageView(bidder.getAvatar());
		avatar.setFitHeight(70);
		avatar.setFitWidth(70);

		Label name = new Label("Name: " + bidder.getFirstName());
		Label amount = new Label("Bid: £" + bid.getPrice());
		Label time = new Label("Date: " + DATE_FORMAT.format(new Date(bid.getDate())));

		vbox.getChildren().addAll(avatar, name, amount, time);

		vbox.setOnMouseClicked(e -> ProfileController.viewProfile(bidder));

		this.bidhistorybox.getChildren().add(0, vbox);
	}

	/**
	 * Get a label for displaying a header
	 * @param text - The text to display
	 * @return The label
	 */
	private Label getHeaderLabel(String text) {
		Label label = new Label();
		label.setFont(new Font(label.getFont().getName(), 18));
		label.setUnderline(true);
		label.setText(text);
		return label;
	}

	/**
	 * Get a label for displaying text / information
	 * @param text - The text to display
	 * @return The label
	 */
	private Label getTextLabel(String text) {
		Label label = new Label(text);
		label.setFont(new Font(label.getFont().getName(), 18));
		return label;
	}

	/**
	 * Add information on to the info box. 
	 * Will format the header and value, and but them together.
	 * 
	 * @param header - The header
	 * @param value  - The value
	 */
	private void displayInfo(String header, String value) {
		HBox hbox = new HBox();
		hbox.setSpacing(10);

		Label headerLbl = this.getHeaderLabel(header + ":");
		Label textLbl = this.getTextLabel(value);

		hbox.getChildren().addAll(headerLbl, textLbl);
		this.infobox.getChildren().add(hbox);
	}

	/**
	 * Add the Artwork's description to the info box
	 */
	private void displayDescription() {
		this.infobox.getChildren().add(this.getHeaderLabel("Description"));

		//We use a Hbox just so we can apply a border to make the description stand out
		HBox descBox = new HBox();

		descBox.setMinWidth(this.infobox.getWidth());
		descBox.setStyle("-fx-border-color: lightgrey;");

		Label desc = this.getTextLabel(viewing.getArtwork().getDescription());
		desc.setWrapText(true);
		descBox.getChildren().add(desc);

		this.infobox.getChildren().add(descBox);
	}

	/**
	 * Populate the info box
	 */
	private void renderInfo() {
		//Load default information
		this.displayInfo("Artist", viewing.getArtwork().getArtist());
		this.displayInfo("Year", viewing.getArtwork().getYear() + "");
		this.displayDescription();

		//Load specific info to a type of Artwork
		if(viewing.getArtwork() instanceof Painting) {
			Painting painting = (Painting) viewing.getArtwork();

			this.displayInfo("Width", painting.getWidth() + "");
			this.displayInfo("Height", painting.getWidth() + "");
		} else if(viewing.getArtwork() instanceof Sculpture) {
			Sculpture sculpture = (Sculpture) viewing.getArtwork();

			this.displayInfo("Width", sculpture.getWidth() + "");
			this.displayInfo("Height", sculpture.getWidth() + "");
			this.displayInfo("Depth", sculpture.getDepth() + "");
			this.displayInfo("Weight", sculpture.getWeight() + "");
			this.displayInfo("Material", sculpture.getMaterial() + "");

			if(! sculpture.getExtraImages().isEmpty()) {
				HBox extraImages = new HBox(10);

				ScrollPane sp = new ScrollPane(extraImages);
				sp.setVbarPolicy(ScrollBarPolicy.NEVER);
				sp.setHbarPolicy(ScrollBarPolicy.ALWAYS);

				for(Image image : sculpture.getExtraImages()) {
					ImageView iv = new ImageView(image);
					iv.setFitHeight(70);
					iv.setFitWidth(70);

					extraImages.getChildren().add(iv);

					iv.setOnMouseClicked(e -> this.viewLargeImage(image));
				}

				this.infobox.getChildren().add(sp);
			}
		}
	}

	/**
	 * View an image as large as possible
	 * 
	 * @param image - The image to view
	 */
	private void viewLargeImage(Image image) {
		Popup popup = new Popup();

		ImageView iv = new ImageView(image);
		iv.setPreserveRatio(true);

		//Crop the image if it's too large.
		if(image.getWidth() > MAX_POPUP_IMG_SIZE) {
			iv.setFitWidth(MAX_POPUP_IMG_SIZE);
		}

		if(image.getHeight() > MAX_POPUP_IMG_SIZE) {
			iv.setFitHeight(MAX_POPUP_IMG_SIZE);
		}

		popup.getContent().add(iv);

		popup.setHeight(image.getHeight());
		popup.setWidth(image.getWidth());

		popup.setHideOnEscape(true);
		popup.setAutoHide(true);

		popup.show(this.image.getScene().getWindow());
	}

	@FXML
	public void addToCustomGallery() {
        if (currentGallery != null) {
        	//A gallery has been selected
        	if (currentGallery.containsListing(viewing)) {
        		//Remove the listing from the gallery
				currentGallery.removeListing(viewing);
				buttonAddCustomGallery.setText(ADD_BTN_MSG);

			} else {
        		//Add the listing to the gallery
        		currentGallery.addListing(viewing);
				buttonAddCustomGallery.setText(RMV_BTN_MSG);

			}

		} else {
			createNewGalPopup();
		}
		/*
			Updates the file after a new item is added to a gallery
		 */
		Main.accountManager.saveAccountFile();
	}

	/**
	 * Displays a modal popup allowing the user to create a new gallery
	 */
	private void createNewGalPopup() {
		Stage popup = new Stage();
		popup.initModality(Modality.APPLICATION_MODAL);

		VBox popBack = new VBox(10);
		popBack.setAlignment(Pos.CENTER);

		Label label = new Label("Name your new gallery");
		TextField input = new TextField();
		Button button = new Button("Create");

		popBack.getChildren().add(label);
		popBack.getChildren().add(input);
		popBack.getChildren().add(button);

		//Create the gallery when button is clicked
		button.setOnMouseClicked(e -> {
			Account user = Main.accountManager.getLoggedIn();

			if (input.getText() == null || input.getText().trim().isEmpty()) {
				AlertUtil.sendAlert(AlertType.ERROR, "No gallery name", "Please provide a gallery name");
				popup.close();
				createNewGalPopup();
			} else if (!user.checkGallery(input.getText())) {
				Gallery newGallery = new Gallery(user, input.getText());
				newGallery.addListing(viewing);
				user.addGallery(newGallery);
				updateGalleryMenu(newGallery.getName(), newGallery);
			} else {
				AlertUtil.sendAlert(AlertType.ERROR, "Existing gallery", "This gallery already exists," +
						" please try with another name");
				popup.close();
				createNewGalPopup();
			}

			/*
				Update the file after a new gallery is added to a user's account
		 	*/
			Main.accountManager.saveAccountFile();

			popup.close();
		});

		Scene popupScene = new Scene(popBack, 300, 100);
		popup.setScene(popupScene);
		popup.show();
	}

	private void renderComments() {
        List<Comment> sortedComments = viewing.getComments();
        if (!sortedComments.isEmpty()) {
            //sortedComments = viewing.getComments();
            if (menuTopComments.isSelected()) {
                sortedComments.sort(Comparator.comparingInt(Comment::getLikes));
            } else if (menuNewestComments.isSelected()) {
                sortedComments.sort(Comparator.comparingLong(Comment::getDateCreated));
            }
        }


		//Clears the tile pane of previous comments
        tileComments.getChildren().clear();

		// Adds the Vbox holding the comment to the tile pane
		sortedComments.forEach(x -> {
			Node n = container(x);
			tileComments.getChildren().add(n);
		});

    }

    private Node container(Comment c) {
	    VBox v = new VBox();
	    v.setAlignment(Pos.TOP_LEFT);
	    v.setMinSize(260,50);
		// Create the container for the name and date
		Node nameDate = nameDataHbox(c.getOwner(), c.getDateCreated());
		Text comment = new Text(c.getCommentValue());
		Node likeDislike = likeDislikeHbox(c);

		v.getChildren().addAll(nameDate, comment, likeDislike);

	    return v;
    }

    private Node nameDataHbox(String name, long dateOfCreation) {
		HBox h = new HBox();
		h.setAlignment(Pos.CENTER_LEFT);
		//TODO -> fix the margins
		Label owner = new Label(name);
		Label creationDate = new Label(DATE_FORMAT.format(new Date(dateOfCreation)));

		h.getChildren().addAll(owner, creationDate);

		return h;
	}

	private Node likeDislikeHbox(Comment c) {
		HBox h = new HBox();
		Label l = new Label("" + c.getLikes());
		l.setStyle("-fx-font-weight: bold; -fx-font-size: 20");
		Image likeImage = new Image("/images/commentIcons/like-icon.png");
		Image dislikeImage = new Image("/images/commentIcons/dislike-icon.png");

		ImageView ivLikeIcon = new ImageView(likeImage);
		ivLikeIcon.setSmooth(true);
		ivLikeIcon.setPreserveRatio(true);
		ivLikeIcon.setStyle("-fx-padding: 5");
		ivLikeIcon.setFitHeight(16);
		ivLikeIcon.setFitWidth(16);

		ImageView ivDislikeIcon = new ImageView(dislikeImage);
		ivDislikeIcon.setSmooth(true);
		ivDislikeIcon.setPreserveRatio(true);
		ivDislikeIcon.setFitHeight(16);
        ivDislikeIcon.setFitWidth(16);

		Button like = new Button();
		like.setMaxSize(16, 16);
		like.setGraphic(ivLikeIcon);
		like.setOnMouseClicked(e -> {
		    c.like(Main.accountManager.getLoggedIn().getUserName());
            l.setText("" + c.getLikes());
        });

		Button dislike = new Button();
		dislike.setMaxSize(32,32);
		dislike.setGraphic(ivDislikeIcon);
		dislike.setOnMouseClicked(e -> {
            c.dislike(Main.accountManager.getLoggedIn().getUserName());
            l.setText("" + c.getLikes());
        });

        h.getChildren().addAll(l, like, dislike);
		return h;
	}

	public void sortByTopComment() {
		this.renderComments();
    }

    public void sortByNewestComment() {
		this.renderComments();
    }

    public void comment() {
	    String commentValue = commentSection.getText().trim();
	    if (!viewingOwnListing) {
	        // Not the listing of the currently logged user -> can comment
	        if (!commentValue.isEmpty()) {
                String name = Main.accountManager.getLoggedIn().getUserName();
                Comment c = new Comment(commentValue, name, viewing.getID(), System.currentTimeMillis());
                viewing.addComment(c);
                this.renderComments();
            }
        } else {
	    	AlertUtil.sendAlert(AlertType.ERROR, "Can not add own comment", "You can not leave own comment");
		}
		commentSection.clear();
    }

	/**
	 * Sends a message to the seller of the artwork
	 */
	public void sendMessage() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Message.fxml"));
			BorderPane someRoot = (BorderPane) loader.load();
			MessageController controller = loader.getController();

			controller.init(Main.accountManager.getAccount(viewing.getSeller()));

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
