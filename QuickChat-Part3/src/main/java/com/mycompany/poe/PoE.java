
package com.mycompany.poe;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Main application class for QuickChat.
 *
 * Handles registration, login, sending messages, storing messages,
 * searching messages, deleting messages and displaying reports.
 *
 * @author Student
 */
public class PoE {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to QuickChat");

        // Register the user.
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        System.out.print("Enter your South African cell phone number: ");
        String cellPhoneNumber = scanner.nextLine().trim();

        Login login = new Login(
                firstName,
                lastName,
                username,
                password,
                cellPhoneNumber
        );

        String registrationStatus = login.registerUser();

        System.out.println(registrationStatus);

        if (!registrationStatus.startsWith(
                "Username successfully captured")) {

            System.out.println(
                    "Registration failed. Please restart QuickChat."
            );

            scanner.close();
            return;
        }

        // Log in using the registered credentials.
        System.out.print("Enter your username to login: ");
        String loginUsername = scanner.nextLine().trim();

        System.out.print("Enter your password to login: ");
        String loginPassword = scanner.nextLine();

        String loginStatus = login.returnLoginStatus(
                loginUsername,
                loginPassword
        );

        System.out.println(loginStatus);

        if (!login.loginUser(loginUsername, loginPassword)) {

            System.out.println("Login failed. QuickChat will close.");

            scanner.close();
            return;
        }

        System.out.println("\nWelcome, " + firstName + "!");

        // Load previously stored messages from the JSON file.
        Message[] previouslyStoredMessages;

        try {

            previouslyStoredMessages = Message.loadStoredMessages();

            System.out.println(
                    previouslyStoredMessages.length
                    + " previously stored message(s) loaded."
            );

        } catch (IOException e) {

            System.out.println(
                    "Error loading stored messages: " + e.getMessage()
            );

            previouslyStoredMessages = new Message[0];
        }

        // QuickChatManager manages all five required arrays.
        QuickChatManager manager = new QuickChatManager(
                previouslyStoredMessages
        );

        int messageLimit = readMessageLimit(scanner);
        int messagesProcessed = 0;

        boolean running = true;

        // Main QuickChat menu.
        while (running) {

            System.out.println("\nQuickChat Menu");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");

            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":

                    if (messagesProcessed >= messageLimit) {

                        System.out.println(
                                "You have reached your message limit."
                        );

                        break;
                    }

                    // Process messages until the limit is reached.
                    while (messagesProcessed < messageLimit) {

                        int messageNumber = messagesProcessed + 1;

                        System.out.print(
                                "Enter recipient number "
                                + "(e.g. +27718693002): "
                        );

                        String recipient = scanner.nextLine().trim();

                        System.out.print("Enter your message: ");

                        String messageText = scanner.nextLine();

                        Message message = new Message(
                                messageNumber,
                                recipient,
                                messageText
                        );

                        // Validate the message ID.
                        if (!message.checkMessageID()) {

                            System.out.println("Invalid message ID.");
                            continue;
                        }

                        // Validate the recipient.
                        if (!message.checkRecipientCell().equals(
                                "Cell phone number successfully captured")) {

                            System.out.println(
                                    message.checkRecipientCell()
                            );

                            continue;
                        }

                        // Validate the message length.
                        if (!message.checkMessageLength()) {

                            System.out.println(
                                    "Message exceeds 250 characters."
                            );

                            continue;
                        }

                        System.out.println("\nMessage preview:");
                        System.out.println(message.printMessage());

                        System.out.println("\nChoose an action:");
                        System.out.println("1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println(
                                "3) Store Message to send later"
                        );

                        System.out.print("Your choice: ");

                        String action = scanner.nextLine().trim();

                        boolean processed = false;

                        switch (action) {

                            case "1":

                                String sendResult =
                                        message.SentMessage();

                                if ("Sent".equals(
                                        message.getMessageStatus())) {

                                    manager.addSentMessage(message);

                                    System.out.println(
                                            "Message successfully sent."
                                    );

                                    System.out.println(
                                            message.printMessage()
                                    );

                                    processed = true;

                                } else {

                                    System.out.println(sendResult);
                                }

                                break;

                            case "2":

                                message.disregardMessage();

                                manager.addDisregardedMessage(message);

                                System.out.println(
                                        "Message disregarded."
                                );

                                processed = true;

                                break;

                            case "3":

                                String storeResult =
                                        message.storeMessages();

                                if ("Stored".equals(
                                        message.getMessageStatus())) {

                                    manager.addStoredMessage(message);

                                    System.out.println(
                                            "Message successfully stored."
                                    );

                                    System.out.println(storeResult);

                                    processed = true;

                                } else {

                                    System.out.println(storeResult);
                                }

                                break;

                            default:

                                System.out.println(
                                        "Invalid action. Choose 1, 2 or 3."
                                );

                                break;
                        }

                        if (!processed) {
                            continue;
                        }

                        messagesProcessed++;

                        if (messagesProcessed >= messageLimit) {
                            break;
                        }

                        System.out.print(
                                "Would you like to process another "
                                + "message? (yes/no): "
                        );

                        String again = scanner.nextLine().trim();

                        if (!again.equalsIgnoreCase("yes")) {
                            break;
                        }
                    }

                    break;

                case "2":

                    displaySentMessages(manager.getSentMessages());

                    break;

                case "3":

                    running = false;

                    System.out.println(
                            "Thank you for using QuickChat."
                    );

                    break;

                case "4":

                    boolean storedMenuRunning = true;

                    // Stored messages submenu.
                    while (storedMenuRunning) {

                        System.out.println("\nStored Messages Menu");

                        System.out.println(
                                "1) Display sender and recipient"
                        );

                        System.out.println(
                                "2) Display longest stored message"
                        );

                        System.out.println(
                                "3) Search message using message ID"
                        );

                        System.out.println(
                                "4) Search messages by recipient"
                        );

                        System.out.println(
                                "5) Delete message using message hash"
                        );

                        System.out.println(
                                "6) Display full message report"
                        );

                        System.out.println(
                                "7) Return to main menu"
                        );

                        System.out.print("Choose an option: ");

                        String storedChoice =
                                scanner.nextLine().trim();

                        switch (storedChoice) {

                            case "1":

                                displaySenderAndRecipient(
                                        manager.getStoredMessages(),
                                        firstName + " " + lastName
                                );

                                break;

                            case "2":

                                displayLongestMessage(manager);

                                break;

                            case "3":

                                System.out.print(
                                        "Enter the message ID: "
                                );

                                String searchID =
                                        scanner.nextLine().trim();

                                searchByMessageID(manager, searchID);

                                break;

                            case "4":

                                System.out.print(
                                        "Enter the recipient number: "
                                );

                                String searchRecipient =
                                        scanner.nextLine().trim();

                                searchByRecipient(
                                        manager,
                                        searchRecipient
                                );

                                break;

                            case "5":

                                System.out.print(
                                        "Enter the message hash: "
                                );

                                String searchHash =
                                        scanner.nextLine().trim();

                                Path jsonFile = Path.of(
                                        System.getProperty("user.dir"),
                                        "messages.json"
                                );

                                try {

                                    boolean deleted =
                                            manager.deleteMessageByHash(
                                                    searchHash,
                                                    jsonFile
                                            );

                                    if (deleted) {

                                        System.out.println(
                                                "Message successfully deleted."
                                        );

                                    } else {

                                        System.out.println(
                                                "Message hash not found."
                                        );
                                    }

                                } catch (IOException e) {

                                    System.out.println(
                                            "Error updating JSON file: "
                                            + e.getMessage()
                                    );
                                }

                                break;

                            case "6":

                                System.out.println(
                                        manager.generateMessageReport()
                                );

                                break;

                            case "7":

                                storedMenuRunning = false;

                                break;

                            default:

                                System.out.println(
                                        "Invalid option. Choose 1 to 7."
                                );

                                break;
                        }
                    }

                    break;

                default:

                    System.out.println(
                            "Invalid option. Choose 1, 2, 3 or 4."
                    );

                    break;
            }
        }

        // Display final message totals using QuickChatManager.
        System.out.println("\nQUICKCHAT SUMMARY");

        System.out.println(
                "Total messages sent: "
                + manager.getSentMessages().length
        );

        System.out.println(
                "Total messages disregarded: "
                + manager.getDisregardedMessages().length
        );

        System.out.println(
                "Total stored messages: "
                + manager.getStoredMessages().length
        );

        System.out.println(
                "Total message hashes: "
                + manager.getMessageHashes().length
        );

        System.out.println(
                "Total message IDs: "
                + manager.getMessageIDs().length
        );

        scanner.close();
    }

    /**
     * Displays all recently sent messages.
     */
    private static void displaySentMessages(Message[] messages) {

        if (messages.length == 0) {

            System.out.println("No messages have been sent yet.");
            return;
        }

        System.out.println("\nRecently Sent Messages");

        for (Message message : messages) {

            System.out.println("-------------------------");
            System.out.println(message.printMessage());
        }
    }

    /**
     * Displays the sender and recipient of stored messages.
     */
    private static void displaySenderAndRecipient(
            Message[] messages,
            String sender) {

        if (messages.length == 0) {

            System.out.println("No stored messages found.");
            return;
        }

        System.out.println(
                "\nStored Message Senders and Recipients"
        );

        for (Message message : messages) {

            System.out.println("-------------------------");

            System.out.println("Sender: " + sender);

            System.out.println(
                    "Recipient: " + message.getRecipient()
            );
        }
    }

    /**
     * Displays the longest stored message.
     */
    private static void displayLongestMessage(
            QuickChatManager manager) {

        Message longestMessage =
                manager.findLongestStoredMessage();

        if (longestMessage == null) {

            System.out.println("No stored messages found.");
            return;
        }

        System.out.println("\nLongest Stored Message");

        System.out.println(longestMessage.printMessage());

        System.out.println(
                "Message length: "
                + longestMessage.getMessage().length()
                + " characters"
        );
    }

    /**
     * Searches stored messages using a message ID.
     */
    private static void searchByMessageID(
            QuickChatManager manager,
            String messageID) {

        Message message = manager.searchByMessageID(messageID);

        if (message == null) {

            System.out.println("Message ID not found.");
            return;
        }

        System.out.println("\nMessage Found");

        System.out.println(
                "Recipient: " + message.getRecipient()
        );

        System.out.println(
                "Message: " + message.getMessage()
        );
    }

    /**
     * Searches all stored messages for a recipient.
     */
    private static void searchByRecipient(
            QuickChatManager manager,
            String recipient) {

        Message[] matchingMessages =
                manager.searchByRecipient(recipient);

        if (matchingMessages.length == 0) {

            System.out.println(
                    "No messages found for this recipient."
            );

            return;
        }

        System.out.println("\nMessages Found");

        for (Message message : matchingMessages) {

            System.out.println("-------------------------");
            System.out.println(message.printMessage());
        }
    }

    /**
     * Reads a valid non-negative message limit.
     */
    private static int readMessageLimit(Scanner scanner) {

        while (true) {

            System.out.print(
                    "How many messages would you like to process? "
            );

            try {

                int limit = Integer.parseInt(
                        scanner.nextLine().trim()
                );

                if (limit >= 0) {
                    return limit;
                }

                System.out.println(
                        "Please enter zero or a positive number."
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid whole number."
                );
            }
        }
    }
}
