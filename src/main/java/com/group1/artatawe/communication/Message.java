package com.group1.artatawe.communication;

import com.google.gson.JsonObject;
import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;

public class Message {

    private Account sender;
    private Account recipient;
    private String messageValue;
    private long date;

    /**
     *
     * @param sender
     * @param recipient
     * @param messageValue
     * @param date
     */
    public Message(Account sender, Account recipient, String messageValue, long date) {
        this.sender = sender;
        this.recipient = recipient;
        this.messageValue = messageValue;
        this.date = date;
    }

    /**
     *
     * @param jo
     */
    public Message(JsonObject jo) {
        this.loadFromJson(jo);
    }


    /**
     *
     * @param jo
     */
    private void loadFromJson(JsonObject jo) {

        this.sender = Main.accountManager.getAccount(jo.get("sender").getAsString());
        this.recipient = Main.accountManager.getAccount(jo.get("recipient").getAsString());
        this.messageValue = jo.get("messageValue").getAsString();
        this.date = jo.get("date").getAsLong();

    }

    /**
     *
     * @return
     */
    public JsonObject toJsonObject() {
        JsonObject jo = new JsonObject();

        jo.addProperty("sender", sender.getUserName());
        jo.addProperty("recipient", recipient.getUserName());
        jo.addProperty("messageValue", messageValue);
        jo.addProperty("date", date);

        return jo;

    }

    public String getMessageValue() {
        return this.messageValue;
    }

    public Account getRecipient() {
        return this.recipient;
    }

    public Account getSender() {
        return this.sender;
    }

    public long getDate() {
        return this.date;
    }
}
