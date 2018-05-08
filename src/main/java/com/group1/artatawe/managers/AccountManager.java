package com.group1.artatawe.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.accounts.Address;

import javafx.scene.image.Image;

/**
 * Responsible for loading, saving and storing all the accounts in the system.
 */
public class AccountManager {

	private static final String ACCOUNT_FILE = "accounts.json";

	//Can be null if no account is logged in
	private Account loggedInAccount;

	private final LinkedList<Account> allAccounts = new LinkedList<>();
	private JsonArray galleryObjects = new JsonArray();

	/**
	 * Construct a AccountManager
	 * Will load all the accounts from the file
	 */
	public AccountManager() {
		this.loadAccounts();
		this.loadGalleries();
	}

	/**
	 * Get the logged in account
	 * @return The currently logged in Account. Will be null if no account is logged in
	 */
	public Account getLoggedIn() {
		return this.loggedInAccount;	
	}
	
	/**
	 * Log an account in
	 * @param account- The account to log in
	 */
	public void login(Account account) {
		this.loggedInAccount = account;
		this.loggedInAccount.updateNotifications();
		account.login();

		this.saveAccountFile();
	}

	/**
	 * Logout the current account
	 */
	public void logoutCurrentAccount() {
		this.loggedInAccount = null;
		this.saveAccountFile();
		Main.checkedMessages = false;
		Main.switchScene("AccountSelect");
	}

	/**
	 * Get the account with a matching username
	 * 
	 * @param userName - The username to find the account for
	 * @return The account. Will be null if no account has this username
	 */
	public Account getAccount(String userName) {
		return this.allAccounts.stream()
				.filter(account -> account.getUserName().equalsIgnoreCase(userName))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Get all Accounts in the system
	 * @return List of all Accounts
	 */
	public LinkedList<Account> getAccounts() {
		return this.allAccounts;
	}

	/**
	 * Check if a username has not been used
	 * 
	 * @param userName - The username to check
	 * @return True if the username hasn't been used, else False
	 */
	public boolean isUniqueUsername(String userName) {
		return this.getAccount(userName) == null;
	}

	/**
	 * Construct a new account and save it in the system
	 */
	/**
	 *
	 * @param userName - The username of an account
	 * @param firstName - The first name of a user
	 * @param lastName - The last name of a user
	 * @param mobileNum - The mobile number of a user
	 * @param address - The address of a user
	 * @param avatar -  The avatar of a user
	 * @return Account object with all attributes of a user
	 */
	public Account createAccount(String userName, String firstName, String lastName, String mobileNum, Address address, Image avatar) {
		Account newAccount = new Account(userName, firstName, lastName, mobileNum, address, avatar, System.currentTimeMillis());
		this.allAccounts.add(newAccount);
		
		this.saveAccountFile();
		return newAccount;
	}
	
	/**
	 * Save all the accounts back to the file
	 */
	public void saveAccountFile() {
		StringBuilder data = new StringBuilder();
		
		this.allAccounts.forEach(account -> data.append(account.toJsonObject().toString() + "\n"));
		
		File accountsFile = new File(ACCOUNT_FILE);
		
		try {
			Files.write(accountsFile.toPath(), data.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Open and load all the accounts out of the file.
	 */
	private void loadAccounts() {
		File temp = new File(ACCOUNT_FILE);
		Scanner scanner = null;
		try {
			scanner = new Scanner(temp);

			while(scanner.hasNextLine()) {
				String nextLine = scanner.nextLine().trim();

				if(! nextLine.isEmpty()) {
					this.loadAccount(nextLine);
				}
			}

		} catch (FileNotFoundException e) {
			try {
				new File(ACCOUNT_FILE).createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			if(scanner != null) { 
				scanner.close(); 
			}
		}
	}

	/**
	 * Loads an account from a Json String into the system
	 * 
	 * @param jsonString - The Json String that is turned into an Account
	 */
	private void loadAccount(String jsonString) {
		JsonParser jp = new JsonParser();
		try {
			JsonObject jo = (JsonObject) jp.parse(jsonString);

			Account account = new Account(jo);
			this.allAccounts.add(account);
			/*
				Creates a Json array which would each line of text which represents a single account with all its
				attributes including the galleries
			 */
			this.galleryObjects.add(jo);
		} catch(Exception e) {
			System.out.println("Parse error on string: \n" + jsonString + "\nThe account has not been loaded.");
			System.out.println(e.getMessage());
		}
	}

	/**
	 *
	 */
	private void loadGalleries() {
		// Adds the same exact line so we can go over it again and get the galleries
		// This is because we need to have existing account before we add a gallery
		/*
			For testing
			System.out.println("Size in AccountManager -> " + getAccounts().size());
		 */
		/*
			Goes through each object and initiates the adding of the galleries
		 */
		for (int i = 0; i < galleryObjects.size(); i++) {
			JsonObject object = galleryObjects.get(i).getAsJsonObject();
			Account acc = this.allAccounts.get(i);
			acc.loadGalleries(object, acc);
		}
	}
}
