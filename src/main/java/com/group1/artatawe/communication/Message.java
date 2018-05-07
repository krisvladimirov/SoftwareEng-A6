package com.group1.artatawe.communication;

import com.google.gson.JsonObject;
import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;

public class Message {

    private String messageValue;
    private long date;
    private Account[] participants = new Account[2];
    // The sender would always be at participants[0]
    // The recipient would always be at participants[1]

    /**
     *
     * @param sender
     * @param recipient
     * @param messageValue
     * @param date
     */
    public Message(Account sender, Account recipient, String messageValue, long date) {
        this.participants[0] = sender;
        this.participants[1] = recipient;
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

        this.participants[0] = Main.accountManager.getAccount(jo.get("sender").getAsString());
        this.participants[1] = Main.accountManager.getAccount(jo.get("recipient").getAsString());
        this.messageValue = jo.get("messageValue").getAsString();
        this.date = jo.get("date").getAsLong();

    }

    /**
     *
     * @return
     */
    public JsonObject toJsonObject() {
        JsonObject jo = new JsonObject();

        jo.addProperty("sender", participants[0].getUserName());
        jo.addProperty("recipient", participants[1].getUserName());
        jo.addProperty("messageValue", messageValue);
        jo.addProperty("date", date);

        return jo;

    }

    public String getMessageValue() {
        return this.messageValue;
    }

    public Account getSender() {
        return this.participants[0];
    }

    public Account getRecipient() {
        return this.participants[1];
    }

    public long getDate() {
        return this.date;
    }
}
