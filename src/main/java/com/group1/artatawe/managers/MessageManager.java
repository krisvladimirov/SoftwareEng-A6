package com.group1.artatawe.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.communication.Chat;
import com.group1.artatawe.communication.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class MessageManager {

    private static final String MESSAGE_FILE = "message.json";

    private final List<Chat> chats = new LinkedList<>();

    public MessageManager() {

    }

    /**
     * Save all the Messages back to the file
     */
    public void saveChatFile() {
        StringBuilder data = new StringBuilder();

        //this.messages.forEach(message -> data.append(message.toJsonObject().toString()));
        this.chats.forEach(chat -> data.append(chat.toJsonObject().toString()));

        File messageFile = new File(MESSAGE_FILE);

        try {
            Files.write(messageFile.toPath(), data.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks if a chat already exists, if not an exception would be thrown from the lambda expression
     * @param a1 Sender account
     * @param a2 Receiver account
     * @return The chat between the two accounts or and exception that there is not chat between them
     */
    private Chat getChat(Account a1, Account a2) {

        Chat c = chats.stream()
                        .filter(x -> x.getAccounts().contains(a1)
                                && x.getAccounts().contains(a2)).findFirst()
                        .get();

        return c;
    }

    /**
     * Tries to send a message to a user, by determining if a chat between two user 'endpoints' exists
     * If it doesn't exist the try catch block would capture an exception(i.e. there is not connection currently
     * between the two users) and create a new chat.
     * @param message The message that would be sen
     */
    public void sendMessage(Account a1, Account a2, Message message) {

        try {
            Chat c = getChat(a1, a2);
            c.addMessage(message);
        } catch (NoSuchElementException e) {
            Chat c = new Chat(a1, a2);
            c.addMessage(message);
            this.chats.add(c);
        }

    }

    /**
     * Deletes a chat
     */
    public void deleteChat() {

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
