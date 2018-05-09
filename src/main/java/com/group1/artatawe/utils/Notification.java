package com.group1.artatawe.utils;

import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.communication.Chat;
import com.group1.artatawe.communication.Message;
import com.group1.artatawe.listings.Comment;
import com.group1.artatawe.listings.Listing;
import com.group1.artatawe.listings.ListingState;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Notifications is used only to read in the provided data for attributes such as listings, bids, comments
 * and new messages. Then it uses a series of method which are tasked with looking up if an attribute is indeed 'new'
 * for the user since his/hers last login. If so it is saved and shown later in the Profiles window under the
 * notification panel
 *
 * @author Oskar Figura, Nikolina Antoniou and Kristiyan Vladimirov (v1.1)
 * @version 1.1
 * created on: 05/03/2018.
 */
public class Notification {

    /*
        Provides storage for all the new messages a user has had during the time since his last login
     */
    //private List<Message> newMessages =  new LinkedList<>();
    private int newMessagesCounter = 0;

    /**
     * Create a new Notification object
     */
    public Notification() {

    }

    /**
     * Get a List of new listings that are now on auction since last login
     * @return List of new listings since last login
     */
    public List<Listing> getNewListings() {
        long lastLoginDate = Main.accountManager.getLoggedIn().getLastLogin();
        return Main.listingManager.getAllActiveListings()
                .stream().filter(x -> x.getDateCreated() > lastLoginDate)
                .collect(Collectors.toList());
    }

    /**
     * Get a list of new bids on sellers auctions since last login
     * @return List of listings with new bids on sellers auctions
     */
    public List<Listing> getNewBids() {
        String currentUser = Main.accountManager.getLoggedIn().getUserName();
        long lastLoginDate = Main.accountManager.getLoggedIn().getLastLogin();

        return Main.listingManager.getAllActiveListings().stream()
                .filter(x -> x.getSeller().equals(currentUser))
                .filter(x -> x.getNumOfBids() > 0)
                .filter(x -> x.getCurrentBid().getDate() > lastLoginDate)
                .collect(Collectors.toList());
    }

    /**
     * List of auctions a user bid on that are coming to a close (have less than 3 bids left)
     * @return List of listings close to their bid limit
     */
    public List<Listing> getEndingListings() {
        Account currentUser = Main.accountManager.getLoggedIn();
        return Main.listingManager.getAllActiveListings()
                .stream().filter(x -> x.getBidsLeft() < 3)
                .filter(a -> a.getBidHistory().getBid(currentUser) != null)
                .collect(Collectors.toList());
    }

    /**
     * Get a list of lost auctions since last login
     * @return List of lost auctions since last login
     */
    public List<Listing> getLostListings() {
        String currentUser = Main.accountManager.getLoggedIn().getUserName();
        long lastLoginDate = Main.accountManager.getLoggedIn().getLastLogin();

        return Main.accountManager.getLoggedIn().getHistory().getBiddedOnListings().stream()
                .filter(x -> x.getListingState() == ListingState.FINISHED)
                .filter(x -> !x.getCurrentBid().getBidder().equals(currentUser))
                .filter(x -> x.getCurrentBid().getDate() > lastLoginDate)
                .collect(Collectors.toList());
    }

    /**
     * Scans for any new messages since the last login of the user and then saves them to the memory so the user could
     * see his messages right after he has logged
     */
    public void scanForNewMessages() {

        long lastLoginDate = Main.accountManager.getLoggedIn().getPreLastLogin();
        List<Chat> list = Main.messageManager.getChat(Main.accountManager.getLoggedIn());
        list.forEach(chat -> chat.getNewMessages(lastLoginDate)
                    .forEach(message -> newMessagesCounter++));

    }

    /**
     * Gets a list of all the new messages since the last login of the current user
     * @return List of Messages since last login
     */
    /*
    public List<Message> getNewMessages() {
        return this.newMessages;
    }
    */
    /**
     * Gets how many new messages a user has received since his last login. This is done in order to notify the current
     * user
     * @return Integer number representing how many new messages the current user has received
     */
    public int howManyNewMessages() {
        return this.newMessagesCounter;
    }

    /**
     * Get a list of all new comments on a sellers actions since last login
     * @return List of all the new comments on a seller's listing
     */
    public List<Comment> getNewComments() {
        String currentUser = Main.accountManager.getLoggedIn().getUserName();
        long lastLoginDate = Main.accountManager.getLoggedIn().getPreLastLogin();

        // All of the currentUser's listings
        List<Listing> listings = Main.listingManager.getAllListings().stream()
                                            .filter(listing -> listing.getSeller().equals(currentUser))
                                            .collect(Collectors.toList());

        List<Comment> newComments = new LinkedList<>();

        // All new comments to any of the currentUser's listings
        listings.forEach(listing -> listing.getNewComments(lastLoginDate)
                        .forEach(comment -> newComments.add(comment)));

        System.out.println("How many listings does an account have " + listings.size());
        System.out.println("How many newComments does it have " + newComments.size() );

        return newComments;
    }
}
