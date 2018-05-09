package com.group1.artatawe.communication;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kristiyan Vladimirov
 * This class is used to instantiate new 'chat' which would allow two users to send messages between them .
 * A chat can not exist if there are not messages between two account. On the other hand whenever a message is sent
 * from Account A to Account B, a new chat is instantiated which would serve as a tool for communication between
 * A and B
 */
public class Chat {

    /*
        Provides storage which would contain all the messages inside a chat and the users participating in ti
     */
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
     * Constructs a new Chat from a JsonObject
     * @param jo The JsonObject
     */
    public Chat(JsonObject jo) {
        this.loadFromJson(jo);
    }

    /**
     * Loads all the chat data from a JsonObject
     */
    public void loadFromJson(JsonObject jo) {

        JsonArray accountArray = jo.getAsJsonArray("accounts");
        for (int i = 0; i < accountArray.size(); i++) {
            String account = accountArray.get(i).getAsString();
            this.accounts.add(Main.accountManager.getAccount(account));
        }

        JsonArray messagesArray = jo.getAsJsonArray("messages");
        for (int i = 0; i < messagesArray.size(); i++) {
            JsonObject object = messagesArray.get(i).getAsJsonObject();
            Message m = new Message(object);
            this.messages.add(m);
        }

    }

    /**
     *  Turns a chat into a JsonObject
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
     * Gets all new messages that were added to this chat since the last login of the seller
     * @param lastLogging
     * @return List of all new Messages from a chat
     */
    public List<Message> getNewMessages(long lastLogging) {
        Account a = Main.accountManager.getLoggedIn();
        return messages.stream()
                        .filter(x -> x.getRecipient() == a)
                        .filter(x -> x.getDate() > lastLogging)
                        .collect(Collectors.toList());
    }

    /**
     * Returns how many new messages have been sent to the currently logged user in a specific chat between him and
     * another user.
     * @param lastLogging
     * @return Number representation of how many new messages have been sent to the user since his last login
     */
    public long getNewMessagesFromThisChat(long lastLogging) {
        Account a = Main.accountManager.getLoggedIn();
        return messages.stream()
                .filter(x -> x.getRecipient() == a)
                .filter(x -> x.getDate() > lastLogging)
                .count();

    }

    /**
     * Gets the two accounts that are communication in this chat
     * @return Set which contains both accounts participating in this chat
     */
    public Set<Account> getAccounts() {
        return this.accounts;
    }

    /**
     * Gets the other account linked to the current user
     * @return The second account that is participating in this chat
     */
    public Account getOtherAccount() {
        return this.accounts.stream()
                            .filter(x -> x != Main.accountManager.getLoggedIn())
                            .findFirst()
                            .get();

    }

    /**
     * Get all the messages of chat in a list
     * @return All messages in one chat
     */
    public List<Message> getAllMessages() {
        return this.messages;
    }

    /**
     * Adds a new message to this chat
     * @param m The message to be added
     */
    public void addMessage(Message m) {
        this.messages.add(m);
    }

}
