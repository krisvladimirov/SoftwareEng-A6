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
 * Class used to generate data for notifications
 *
 * @author Oskar Figura, Nikolina Antoniou and Kristiyan Vladimirov (v1.1)
 * @version 1.1
 * created on: 05/03/2018.
 */
public class Notification {

    private List<Message> newMessages =  new LinkedList<>();
    //private List<Message> newComments;

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
     * Get a list of all new messages to the currentUser
     * @return
     */
    public void scanForNewMessages() {

        long lastLoginDate = Main.accountManager.getLoggedIn().getPreLastLogin();
        // All the chats a user has participated in
        List<Chat> list = Main.messageManager.getChat(Main.accountManager.getLoggedIn());
        // All the new messages would be stored here
        //List<Message> newMessages = new LinkedList<>();

        list.forEach(chat -> chat.getNewMessages(lastLoginDate)
                    .forEach(message -> newMessages.add(message)));

    }

    /**
     *
     * @return
     */
    public List<Message> getNewMessages() {
        return this.newMessages;
    }

    /**
     *
     * @return
     */
    public int howManyNewMessages() {
        return newMessages.size();
    }

    /**
     * Get a list of all new comments on a sellers actions since last login
     * @return
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
