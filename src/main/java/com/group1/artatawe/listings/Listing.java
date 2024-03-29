package com.group1.artatawe.listings;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.artwork.Artwork;

/**
 * Represent a single Listing / Auction for a Artwork.
 */
public class Listing {
	
	private final int id;
	private Artwork artwork;
	private final double reserve;
	private final int maxBids;
	private final BidHistory bidHistory;
	private final long dateCreated;
	private final String seller;   //Username of the seller
	private final List<Comment> comments =  new LinkedList<Comment>();
	
	private ListingState listingState;
	
	/**
	 * Construct a new Listing. 
	 * Use this constructor creating a new listing.
	 * 
	 * @param id          - The unique ID of this listing
	 * @param artwork     - The artwork this listing is for
	 * @param reserve     - The reserver for this artwork (where the bidding starts)
	 * @param maxBids     - The number of bids before this Listing is over
	 * @param seller      - The username of the person selling
	 * @param dateCreated - The date the listing was made
	 */
	public Listing(int id, Artwork artwork, double reserve, int maxBids, String seller, long dateCreated) {
		this.id = id;
		this.artwork = artwork;
		this.reserve = reserve;
		this.maxBids = maxBids;
		this.seller = seller;
		this.dateCreated = dateCreated;
		this.listingState = ListingState.ACTIVE;
		this.bidHistory = new BidHistory();
	}
	
	/**
	 * Construct a new Listing from a JsonObject
	 * 
	 * @param jo - The JsonObject to load from
	 * @throws Exception - If the loading fails
	 */
	public Listing(JsonObject jo) throws Exception {
		this.id = jo.get("id").getAsInt();
		this.artwork = Artwork.loadFromJson(jo);
		this.reserve = jo.get("reserve").getAsDouble();
		this.maxBids = jo.get("maxbids").getAsInt();
		this.bidHistory = new BidHistory(jo);
		this.dateCreated = jo.get("datecreated").getAsLong();
		this.seller = jo.get("seller").getAsString();
		this.listingState = ListingState.valueOf(jo.get("state").getAsString());
		this.loadComments(jo.getAsJsonArray("comments"));

	}

	/**
	 * Loads all the comments of a listing
	 * @param ja
	 */
	public void loadComments(JsonArray ja) {
	    JsonArray commentsArray = ja;
	    for (int i = 0; i < commentsArray.size(); i++) {
	        JsonObject object = commentsArray.get(i).getAsJsonObject();
	        Comment c = new Comment(object);
	        this.comments.add(c);
	    }
	}

	/**
	 * Adds a comment to this listing
	 * @param c
	 */
	public void addComment(Comment c) {
		this.comments.add(c);
	}

	/**
	 * Gets all new comments that were added to this listing since the last login of the seller
	 * @param lastLogin
	 * @return
	 */
	public List<Comment> getNewComments(long lastLogin) {
		return comments.stream()
						.filter(x -> x.getDateCreated() > lastLogin)
						.collect(Collectors.toList());
	}

	
	/**
	 * Create a bid for this listing
	 * 
	 * @param price  - The amount bid
	 * @param bidder - The Account of the bidder
	 */
	public void createBid(double price, Account bidder) {
		if(this.listingState != ListingState.ACTIVE) {
			throw new IllegalStateException("You cannot bid on a Listing that is not active!!");
		}
		
		bidder.getHistory().addBiddedOnListing(this);
		
		this.bidHistory.createNewBid(price, bidder);
		
		if(this.maxBids <= this.bidHistory.getNumOfBids()) {
			this.endListing();
		}
		
		Main.listingManager.saveListingsFile();
	}
	
	/**
	 * Get the ID of this listing
	 * @return This listing's ID
	 */
	public int getID() {
		return this.id;
	}
	
	/**
	 * Get the artwork this listing is for
	 * @return The artwork
	 */
	public Artwork getArtwork() {
		return this.artwork;
	}
	
	/**
	 * Get the reserve (minimum bid) for this artwork
	 * @return The reserve
	 */
	public double getReserve() {
		return this.reserve;
	}
	
	/**
	 * Get the BidHistory for this Listing
	 * @return The BidHistory
	 */
	public BidHistory getBidHistory() {
		return this.bidHistory;
	}
	
	/**
	 * Get the current bid (from the BidHistory)
	 * @return The current bid. Null if no current bid
	 */
	public Bid getCurrentBid() {
		return this.bidHistory.getCurrentBid();
	}
	
	/**
	 * Check if this Listing has a description
	 * @return True if their is a description, else False
	 */
	public boolean hasDescription() {
		return ! this.artwork.getDescription().trim().isEmpty();
	}
	
	/**
	 * Get the number of bids remaining
	 * @return The number of bids remaining
	 */
	public int getBidsLeft() {
		return this.maxBids - this.getNumOfBids();
	}
	
	/**
	 * Get the number of bids made on this Listing
	 * @return The number of bids on this Listing
	 */
	public int getNumOfBids() {
		return this.bidHistory.getNumOfBids();
	}
	
	/**
	 * Get the maximum number of bids, before this Listing is over
	 * @return The maximum number of bids
	 */
	public int getMaxBids() {
		return this.maxBids;
	}
	
	/**
	 * Get the date this Listing was made
	 * @return The date this Listing was made
	 */
	public long getDateCreated() {
		return this.dateCreated;
	}
	
	/**
	 * Get the username of the seller
	 * @return The username of seller
	 */
	public String getSeller() {
		return this.seller;
	}
	
	/**
	 * Get the state of the Listing
	 * @return The ListingState 
	 */
	public ListingState getListingState(){
		return listingState;
	}
	
	/**
	 * Cancel this listing. Will set the state to CANCELLED
	 */
	public void cancelListing() {
		this.listingState = ListingState.CANCELLED;
	}

	public List<Comment> getComments() {
		return this.comments;
	}
	/**
	 * Turn the Listing into a JsonObject
	 * @return The JsonObject
	 */
	public JsonObject toJsonObject() {
		JsonObject jo = new JsonObject();
		jo.addProperty("id", this.id);
		jo.addProperty("reserve", this.reserve);
		jo.addProperty("maxbids", this.maxBids);
		jo.addProperty("datecreated", this.dateCreated);
		jo.addProperty("seller", this.seller);
		jo.addProperty("state", this.listingState.toString());
		
		for(Entry<String, JsonElement> entry : this.artwork.toJsonObject(this).entrySet()) {
			jo.add(entry.getKey(), entry.getValue());
		}
		
		for(Entry<String, JsonElement> entry : this.bidHistory.toJsonObject().entrySet()) {
			jo.add(entry.getKey(), entry.getValue());
		}

		JsonArray commentsArray = new JsonArray();
		comments.stream().forEach(x -> {
			JsonObject j = x.toJsonObject();
			commentsArray.add(j); // Adding a Json object into the array, this object hold all data about a comment
		});
		jo.add("comments", commentsArray);
		
		return jo;
	}
	
	/**
	 * End this listing. To be called when the max number of bids is reached
	 * Will set the state to FINISHED
	 */
	private void endListing() {
		this.listingState = ListingState.FINISHED;
	}
}