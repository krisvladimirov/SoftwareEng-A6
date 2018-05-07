package com.group1.artatawe.communication;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;

import java.util.*;

public class Chat {

    Set<Account> accounts = new HashSet<>();
    private List<Message> messages = new LinkedList<>();

    /**
     * Initializes a new chat, where two accounts could send messages to each other via it.
     * @param a1 Sender account
     * @param a2 Receiver account
     */
    public Chat(Account a1, Account a2) {
        this.accounts.add(a1);
        this.accounts.add(a2);
    }

    /**
     *
     * @param jo
     */
    public Chat(JsonObject jo) {
        this.loadFromJson(jo);
    }

    /**
     * Loads all the chat data from a JsonObject
     */
    public void loadFromJson(JsonObject jo) {

        //Account a1 = Main.accountManager.getAccount(jo.get("account1").getAsString());
        //Account a2 = Main.accountManager.getAccount(jo.get("account2").getAsString());
        JsonArray accountArray = jo.getAsJsonArray("accounts");
        for (int i = 0; i < accountArray.size(); i++) {
            JsonObject object = accountArray.get(i).getAsJsonObject();
        }
        this.accounts.add(a1);
        this.accounts.add(a2);

        JsonArray messagesArray = jo.getAsJsonArray("messages");
        for (int i = 0; i < messagesArray.size(); i++) {
            JsonObject object = messagesArray.get(i).getAsJsonObject();
            Message m = new Message(object);
            this.messages.add(m);
        }

    }

    /**
     *
     * @return JsonObject that would represent the chat with the two account endpoints and all of their messages
     */
    public JsonObject toJsonObject() {
        JsonObject jo = new JsonObject();
        JsonArray accountArray = new JsonArray();
        JsonArray messageArray = new JsonArray();

        this.accounts.forEach(x -> accountArray.add(x.getUserName()));
        this.messages.forEach(x -> messageArray.add(x.toJsonObject()));

        jo.add("accounts", accountArray);
        jo.add("messages", messageArray);

        return jo;

    }

    /**
     * Gets the two accounts that are communication in this chat
     * @return
     */
    public Set<Account> getAccounts() {
        return this.accounts;
    }

    /**
     *
     * @return All messages in one chat
     */
    public List<Message> getAllMessages() {
        return this.messages;
    }

    /**
     * Adds a new message to this chat
     * @param m
     */
    public void addMessage(Message m) {
        this.messages.add(m);
    }

}
