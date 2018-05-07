package com.group1.artatawe;

import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.communication.Message;

import java.util.*;

public class Testing {
    public static void main(String[] args) {
        Account a1 = null;
        Account a2 = null;
        Account a3 = null;
        Account a4 = null;
        Account a5 = null;
        Account a6 = null;
        Account a7 = null;
        Account a8 = null;
        Account a9 = null;
        Account a10 = null;


        List<Message> map = new LinkedList<>();
        Message m1 = new Message(a1, a2, "hello", 1);
        Message m2 = new Message(a3, a4, "test", 2);
        Message m3 = new Message(a5, a6, "test", 3);
        Message m4 = new Message(a7, a8, "test", 4);

        Message m = map.stream().filter(x -> x.getMessageValue().equals("non")).findFirst().get();

        System.out.println(m);

    }
}
