package com.group1.artatawe.listings;

import com.google.gson.JsonObject;

public class Comment {

    private String commentValue;
    private String owner;
    private long dateCreated;
    private int listingId;
    private int likes = 0;
    private int dislikes = 0;


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
        jo.addProperty("dislikes", this.dislikes);

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
        this.dislikes = jo.get("dislikes").getAsInt();
    }

    /**
     * Increments the like counter, indicating that someone liked this particular comment
     */
    public void like() {
        this.likes++;
    }

    /**
     * Increment the dislike counter, indicating that someone disliked this particular comment
     */
    public void dislike() {
        this.dislikes++;
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
     * @return Returns how many dislikes a comment has
     */
    public int getDislikes() {
        return this.dislikes;
    }

    /**
     *
     * @return The ratio of likes/dislikes of this comment
     */
    public double getLikeDislikeRatio() {
        double ratio = this.likes / this.dislikes;
        return ratio;
    }

}
