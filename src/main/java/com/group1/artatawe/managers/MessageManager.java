package com.group1.artatawe.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.communication.Chat;
import com.group1.artatawe.communication.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kristiyan Vladimirov 916747
 * MessageManager deals with reading/writing from the message Json file which contains all the chats with the messages
 * inside them or reading/writing for the computers memory.
 */
public class MessageManager {

    private static final String MESSAGE_FILE = "message.json";

    /*
        Provides storage for all the chat that are present in the system
     */
    private final List<Chat> chats = new LinkedList<>();

    public MessageManager() {
        this.loadChats();
    }

    /**
     * Save all the Messages back to the file
     */
    public void saveChatFile() {
        StringBuilder data = new StringBuilder();

        this.chats.forEach(chat -> data.append(chat.toJsonObject().toString() + "\n"));

        File messageFile = new File(MESSAGE_FILE);

        try {
            Files.write(messageFile.toPath(), data.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Adds a chat to the list of chats
     * @param c the chat
     */
    public void addChat(Chat c) {
        this.chats.add(c);
    }

    /**
     * Checks if a chat already exists, if not an exception would be thrown from the lambda expression
     * @param a1 Sender account
     * @param a2 Receiver account
     * @return The chat between the two accounts or and exception that there is not chat between them
     */
    public Chat getChat(Account a1, Account a2) {

        Chat c = chats.stream()
                        .filter(x -> x.getAccounts().contains(a1)
                                && x.getAccounts().contains(a2)).findFirst()
                        .get();

        return c;
    }

    /**
     * Retrieves only chats where the user has participated in
     * @param a The specific account for which chats are filtered
     * @return List of chats where the account has participated in
     */
    public List<Chat> getChat(Account a) {
        return chats.stream()
                    .filter(x -> x.getAccounts().contains(a))
                    .collect(Collectors.toList());
    }

    /**
     * Deletes a chat from the system and all the messages inside it
     * @param chat The chat to be deleted
     */
    public void deleteChat(Chat chat) {

    }

    /**
     * Open file and load all the chat/messages out of the file
     */
    public void loadChats() {

        File file = new File(MESSAGE_FILE);
        Scanner scan = null;

        try {

            scan = new Scanner(file);

            while (scan.hasNextLine()) {
                String nextLine = scan.nextLine().trim();
                if (!nextLine.isEmpty()) {
                    this.loadChat(nextLine);
                }
            }

        } catch (FileNotFoundException e) {
            try {
                new File(MESSAGE_FILE).createNewFile();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (scan != null)
            scan.close();
        }

    }

    /**
     * Loads a single Chat from a Json String into the system
     * @param jsonString The Json String that is turned into a chat
     */
    private void loadChat(String jsonString) {
        JsonParser jsonParser =  new JsonParser();
        try {
            JsonObject jo = jsonParser.parse(jsonString).getAsJsonObject();
            Chat chat = new Chat(jo);
            this.chats.add(chat);
        } catch (Exception e) {
            System.out.println("Parse error on string: \n" + jsonString + "\nThe listing has not been loaded.");
            System.out.println(e.getMessage());
        }
    }
}
