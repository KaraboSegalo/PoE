
package com.mycompany.poe;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PoE {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to QuickChat");

        // Registration
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        System.out.print("Enter your South African cell phone number: ");
        String cellPhoneNumber = scanner.nextLine();

        Login login = new Login(
                firstName, lastName, username, password, cellPhoneNumber
        );

        System.out.println(login.registerUser());

        // Login
        System.out.print("Enter your username to login: ");
        String loginUsername = scanner.nextLine();

        System.out.print("Enter your password to login: ");
        String loginPassword = scanner.nextLine();

        System.out.println(
                login.returnLoginStatus(loginUsername, loginPassword)
        );

        // Only continue if login succeeds.
        if (!loginUsername.equals(username)
                || !loginPassword.equals(password)) {
            System.out.println("Login failed. QuickChat will close.");
            scanner.close();
            return;
        }

        System.out.println("\nWelcome, " + firstName + "!");

        // Ask how many messages may be sent.
        int messageLimit = readMessageLimit(scanner);
        int messagesProcessed = 0;
        List<Message> sentMessages = new ArrayList<>();

        boolean running = true;

        while (running) {
            System.out.println("\nQuickChat Menu");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
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

                    while (messagesProcessed < messageLimit) {
                        System.out.print(
                                "Enter recipient number (e.g. +27718693002): "
                        );
                        String recipient = scanner.nextLine().trim();

                        System.out.print("Enter your message: ");
                        String messageText = scanner.nextLine();

                        Message message = new Message(
                                messagesProcessed + 1,
                                recipient,
                                messageText
                        );

                        if (!message.checkMessageID()) {
                            System.out.println("Invalid message ID.");
                            continue;
                        }

                        if (!message.checkRecipientCell().equals(
                                "Cell phone number successfully captured")) {
                            System.out.println(message.checkRecipientCell());
                            continue;
                        }

                        if (!message.checkMessageLength()) {
                            System.out.println(
                                    "Please enter a message of less than "
                                    + "250 characters"
                            );
                            continue;
                        }

                        System.out.println("\nMessage preview:");
                        System.out.println(message.printMessage());
                        System.out.println("\nMessage hash: "
                                + message.createMessageHash());

                        System.out.println("\nChoose an action:");
                        System.out.println("1) Send message");
                        System.out.println("2) Disregard message");
                        System.out.println("3) Store message");
                        System.out.print("Your choice: ");

                        String action = scanner.nextLine().trim();

                        switch (action) {
                            case "1":
                                System.out.println(message.SentMessage());
                                if ("Sent".equals(message.getMessageStatus())) {
                                    sentMessages.add(message);
                                }
                                messagesProcessed++;
                                break;

                            case "2":
                                System.out.println(message.disregardMessage());
                                messagesProcessed++;
                                break;

                            case "3":
                                System.out.println(message.storeMessages());
                                messagesProcessed++;
                                break;

                            default:
                                System.out.println(
                                        "Invalid action. Message not processed."
                                );
                                break;
                        }

                        if (messagesProcessed >= messageLimit) {
                            break;
                        }

                        System.out.print(
                                "Would you like to process another message? "
                                + "(yes/no): "
                        );

                        String again = scanner.nextLine().trim();

                        if (!again.equalsIgnoreCase("yes")) {
                            break;
                        }
                    }
                    break;

                case "2":
                    System.out.println("Coming Soon");
                    break;

                case "3":
                    running = false;
                    System.out.println("Thank you for using QuickChat.");
                    break;

                default:
                    System.out.println(
                            "Invalid option. Please choose 1, 2 or 3."
                    );
                    break;
            }
        }

        System.out.println("\nTotal messages sent: "
                + (sentMessages.isEmpty()
                ? 0
                : sentMessages.get(0).returnTotalMessages()));

        scanner.close();
    }

    private static int readMessageLimit(Scanner scanner) {
        while (true) {
            System.out.print("How many messages would you like to process? ");

            try {
                int limit = Integer.parseInt(scanner.nextLine().trim());

                if (limit >= 0) {
                    return limit;
                }

                System.out.println("Please enter zero or a positive number.");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}
