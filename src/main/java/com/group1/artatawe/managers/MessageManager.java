package com.group1.artatawe.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.communication.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class MessageManager extends Thread {

    private static final String MESSAGE_FILE = "message.json";

    private final Set<Message> messages = new HashSet<>();

    public MessageManager() {}

    /**
     * Save all the Messages back to the file
     */
    public void saveMessageFile() {
        StringBuilder data = new StringBuilder();

        this.messages.forEach(message -> data.append(message.toJsonObject().toString()));

        File messageFile = new File(MESSAGE_FILE);

        try {
            Files.write(messageFile.toPath(), data.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param message
     */
    public void addMessage(Message message) {
        this.messages.add(message);
    }

    /**
     *
     * @param user
     * @return
     */
    public Set<Message> getMessages(Account user) {
        return this.messages.stream()
                            .filter(x -> x.getSender() == user)
                            .collect(Collectors.toSet());
    }

    /**
     *
     * @return return all messages
     */
    public Set<Message> getAllMessages() {
        return this.messages;
    }


    private void loadMessages() {
        File file = new File(MESSAGE_FILE);
        Scanner scan = null;
        try {
            scan = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scan != null)
            scan.close();
        }
    }

    private void loadMessage(String jsonString) {
        JsonParser jsonParser =  new JsonParser();
        try {
            JsonObject jo = jsonParser.parse(jsonString).getAsJsonObject();

            Message m = new Message(jo);

        } catch (Exception e) {
            System.out.println("Parse error on string: \n" + jsonString + "\nThe listing has not been loaded.");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        //super.run();
    }
}
