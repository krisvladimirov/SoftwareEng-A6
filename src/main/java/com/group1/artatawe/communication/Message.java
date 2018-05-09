package com.group1.artatawe.communication;

import com.google.gson.JsonObject;
import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;

/**
 * @author Kristiyan Vladimirov
 * Message is used to create a new Message which would later be added to a 'chat' that is between two people.
 */
public class Message {

    private String messageValue;
    private long date;
    private Account[] participants = new Account[2];
    // The sender would always be at participants[0]
    // The recipient would always be at participants[1]

    /**
     * Constructs a new Message from the information provided by the current user
     * @param sender The person who is sending the message
     * @param recipient The person whi is receiving the message
     * @param messageValue The String value of the message
     * @param date The date this particular message was created
     */
    public Message(Account sender, Account recipient, String messageValue, long date) {
        this.participants[0] = sender;
        this.participants[1] = recipient;
        this.messageValue = messageValue;
        this.date = date;
    }

    /**
     * Constructs a new Message from a JsonObject
     * @param jo The JsonObject
     */
    public Message(JsonObject jo) {
        this.loadFromJson(jo);
    }


    /**
     * Loads all the attributes of a Message from a JsonObject
     * @param jo The JsonObject
     */
    private void loadFromJson(JsonObject jo) {

        this.participants[0] = Main.accountManager.getAccount(jo.get("sender").getAsString());
        this.participants[1] = Main.accountManager.getAccount(jo.get("recipient").getAsString());
        this.messageValue = jo.get("messageValue").getAsString();
        this.date = jo.get("date").getAsLong();

    }

    /**
     * Turn a Message into a JsonObject
     * @return The JsonObject
     */
    public JsonObject toJsonObject() {
        JsonObject jo = new JsonObject();

        jo.addProperty("sender", participants[0].getUserName());
        jo.addProperty("recipient", participants[1].getUserName());
        jo.addProperty("messageValue", messageValue);
        jo.addProperty("date", date);

        return jo;

    }

    /**
     * Gets the message value
     * @return Message value
     */
    public String getMessageValue() {
        return this.messageValue;
    }

    /**
     * Gets the sender of this message
     * @return Sender of message
     */
    public Account getSender() {
        return this.participants[0];
    }

    /**
     * Gets the recipient of the message
     * @return Recipient of message
     */
    public Account getRecipient() {
        return this.participants[1];
    }

    /**
     * Gets the creation date of this message
     * @return Creating date
     */
    public long getDate() {
        return this.date;
    }
}
