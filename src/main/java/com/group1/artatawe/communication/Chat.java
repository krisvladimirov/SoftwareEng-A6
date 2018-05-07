package com.group1.artatawe.communication;

import com.group1.artatawe.accounts.Account;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Chat {

    private static Map<Chat, List<Message>> chats = new HashMap<>();
    private Account accountOne;
    private Account accountTwo;
    private List<Message> messages = new LinkedList<>();

    public Chat (Account a1, Account a2) {
        this.accountOne = a1;
        this.accountTwo = a2;
        Chat.chats.put(this, messages);
    }

    /**
     * Adds a messages to this chat
     * @param m
     */
    public void sendMessage(Message m) {
        this.messages.add(m);

    }

}
