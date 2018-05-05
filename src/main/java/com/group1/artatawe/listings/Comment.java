package com.group1.artatawe.listings;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.HashSet;
import java.util.Set;

public class Comment {

    private String commentValue = "";
    private String owner = "";
    private long dateCreated;
    private int listingId;
    private int likes = 0;
    private Set<String> commented = new HashSet<String>();
    private String lastClicked = null;


    public Comment(String commentValue, String owner, int listingId, long dateCreated) {
        this.commentValue = commentValue;
        this.owner = owner;
        this.listingId = listingId;
        this.dateCreated = dateCreated;
    }

    public Comment(JsonObject jo) {
        this.loadFromJson(jo);
    }

    /**
     *
     * @return
     */
    public JsonObject toJsonObject() {
        JsonObject jo = new JsonObject();

        jo.addProperty("commentValue", this.commentValue);
        jo.addProperty("owner", this.owner);
        jo.addProperty("dateCreated", this.dateCreated);
        jo.addProperty("listingId", this.listingId);
        jo.addProperty("likes", this.likes);

        return jo;
    }

    /**
     *
     * @param jo
     */
    public void loadFromJson(JsonObject jo) {

            this.commentValue = jo.get("commentValue").getAsString();
            this.owner = jo.get("owner").getAsString();
            this.dateCreated = jo.get("dateCreated").getAsLong();
            this.listingId = jo.get("listingId").getAsInt();
            this.likes = jo.get("likes").getAsInt();

    }

    /**
     * Increments the like counter, indicating that someone liked this particular comment
     */
    public void like(String name) {
        if (lastClicked == null && !commented.contains(name)) {
            this.likes++;
            commented.add(name);
        } else if (lastClicked.equals("dislike") && commented.contains(name)) {
            this.likes++;
        } else if (lastClicked.equals("like") && commented.contains(name)) {
            // Do nothing
            System.out.println("No can't do");
        }

    }

    /**
     * Decrement the like counter, indicating that someone disliked this particular comment
     */
    public void dislike(String name) {
        if (lastClicked == null && !commented.contains(name)) {
            this.likes--;
            commented.add(name);
        } else if (lastClicked.equals("like") && commented.contains(name)) {
            this.likes--;
        } else if (lastClicked.equals("dislike") && commented.contains(name)) {
            // Do nothing
            System.out.println("No can't do");
        }
    }

    /**
     *
     * @return Returns how many likes a comment has
     */
    public int getLikes() {
        return this.likes;
    }

    /**
     *
     * @return the precise time of creation of this comment
     */
    public long getDateCreated() {
        return this.dateCreated;
    }

    public String getCommentValue() {
        return this.commentValue;
    }

    /**
     *
     * @return
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     *
     * @param value
     */
    public void setLastClicked(String value) {
        this.lastClicked = value;
    }

}
