package com.group1.artatawe.listings;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.*;

/**
 * @author Kristiyan Vladimirov
 * Comment is part of a Listing and it is used by users to either you guessed it leave *comments* or feedback.
 */
public class Comment {

    private String commentValue = "";
    private String owner = "";
    private long dateCreated;
    private int listingId;
    private int likes = 0;
    /*
        Provides storage for 'binding'
        binding - storing the users names who have performed an action on a comment as a key and the actions itself
        which could either be like or dislike as value in this HashMap
     */
    private Map<String, String> userClick = new HashMap<>();


    /**
     * Instantiates a new comment that belongs to a listing
     * @param commentValue The actual comment itself
     * @param owner The owner of the comment, or in other words the user who wrote the comment
     * @param listingId The listing id of the listing where the comment was submitted
     * @param dateCreated The exact date of when this comment was submitted
     */
    public Comment(String commentValue, String owner, int listingId, long dateCreated) {
        this.commentValue = commentValue;
        this.owner = owner;
        this.listingId = listingId;
        this.dateCreated = dateCreated;
    }

    /**
     * Constructor used to initialize a comment after being loaded from a JsonParser
     * @param jo JsonObject holding all the attributes of a comment
     */
    public Comment(JsonObject jo) {
        this.loadFromJson(jo);
    }

    /**
     * Loads all associated data with a comment into a JsonObject
     * @return JsonObject holding all the attributes of a comment
     */
   public JsonObject toJsonObject() {
       JsonObject jo = new JsonObject();

       jo.addProperty("commentValue", this.commentValue);
       jo.addProperty("owner", this.owner);
       jo.addProperty("dateCreated", this.dateCreated);
       jo.addProperty("listingId", this.listingId);
       jo.addProperty("likes", this.likes);

       JsonArray jsonArray = new JsonArray();

       Iterator<Map.Entry<String, String>> iterator = userClick.entrySet().iterator();
       while (iterator.hasNext()) {
           Map.Entry<String, String> data = iterator.next();
           String name = data.getKey();
           String click = "";
           if (data.getValue() == null) {
               click = "null";
           } else {
               click = data.getValue();
           }
           JsonObject userAction = new JsonObject();
           userAction.addProperty("name", name);
           userAction.addProperty("action", click);
           jsonArray.add(userAction);
       }

       jo.add("binding", jsonArray);

       return jo;
   }

    /**
     * Loads all the associated with a comment from a JsonObject
     * @param jo JsonObject holding all information related to a comment such as owner, data, likes, listing id and
     *           binding - representing the last action a user has performed on this comment
     */
   private void loadFromJson(JsonObject jo) {

       this.commentValue = jo.get("commentValue").getAsString();
       this.owner = jo.get("owner").getAsString();
       this.dateCreated = jo.get("dateCreated").getAsLong();
       this.listingId = jo.get("listingId").getAsInt();
       this.likes = jo.get("likes").getAsInt();

       JsonArray bindingArray = jo.getAsJsonArray("binding");
       for (int i = 0; i < bindingArray.size(); i++) {
           JsonObject object = bindingArray.get(i).getAsJsonObject();
           addBinding(object);
       }

   }

    /**
     * Saves the last action a user has performed on a comment
     * The action could either be like or dislike or none
     * The actions are read from the JsonObject that contains them. After all the actions are saved in a HashMap
     * where each user's name is the key and the action of a user is the value
     *
     * @param jo JsonObject containing the last action a user has made on a comment either like, dislike or no action
     */
    private void addBinding(JsonObject jo) {

        String name = jo.get("name").getAsString();
        String action = jo.get("action").getAsString();
        if (action.equals("null")) {
            userClick.put(name, null);
        } else {
            userClick.put(name, action);
        }

    }

    /**
     * Increments the like counter, indicating that someone liked this particular comment
     */
    public void like(String name) {

        if (!userClick.containsKey(name)) {
            this.likes++;
            this.userClick.put(name, "like");
        } else if (userClick.containsKey(name) && userClick.get(name).equals("dislike")) {
            this.likes+=2;
            this.userClick.put(name, "like");
        } else if (userClick.containsKey(name) && userClick.get(name).equals("like")) {
            this.likes--;
            this.userClick.remove(name);
        }

    }

    /**
     * Decrement the like counter, indicating that someone disliked this particular comment
     */
    public void dislike(String name) {

        if (!userClick.containsKey(name)) {
            this.likes--;
            this.userClick.put(name, "dislike");
        } else if (userClick.containsKey(name) && userClick.get(name).equals("like")) {
            this.likes -= 2;
            this.userClick.put(name, "dislike");
        } else if (userClick.containsKey(name) && userClick.get(name).equals("dislike")) {
            this.likes++;
            this.userClick.remove(name);
        }

    }

    /**
     * Get the how many likes a comment has
     * @return Returns how many likes a comment has
     */
    public int getLikes() {
        return this.likes;
    }

    /**
     * Gets the date of creation of this comment
     * @return the precise time of creation of this comment
     */
    public long getDateCreated() {
        return this.dateCreated;
    }

    /**
     * Gets the comment itself
     * @return String representation of the comment
     */
    public String getCommentValue() {
        return this.commentValue;
    }

    /**
     * Gets the owner of a comment
     * @return String representation of the owner's name
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Gets the listing id to which this comment belongs
     * @return Integer value of a listing's id
     */
    public int getListingId() {
        return this.listingId;
    }
}
