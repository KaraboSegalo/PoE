
package com.mycompany.poe;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

        System.out.println(login.registerUser());

        // Log in using the registered credentials.
        System.out.print("Enter your username to login: ");
        String loginUsername = scanner.nextLine().trim();

        System.out.print("Enter your password to login: ");
        String loginPassword = scanner.nextLine();

        String loginStatus = login.returnLoginStatus(
                loginUsername, loginPassword
        );

        System.out.println(loginStatus);

        // Only continue when login succeeds.
        if (!loginUsername.equals(username)
                || !loginPassword.equals(password)
                || !loginStatus.startsWith("Welcome ")) {
            System.out.println("Login failed. QuickChat will close.");
            scanner.close();
            return;
        }

        System.out.println("\nWelcome, " + firstName + "!");

        int messageLimit = readMessageLimit(scanner);
        int messagesProcessed = 0;
        List<Message> sentMessages = new ArrayList<>();

        boolean running = true;

        // Keep displaying the menu until the user chooses Quit.
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

                    // Process the requested number of messages.
                    for (int i = messagesProcessed;
                            i < messageLimit; i++) {

                        // The number advances after each processed message.
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

                        if (!message.checkMessageID()) {
                            System.out.println("Invalid message ID.");
                            i--;
                            continue;
                        }

                        if (!message.checkRecipientCell().equals(
                                "Cell phone number successfully captured")) {
                            System.out.println(
                                    message.checkRecipientCell()
                            );
                            i--;
                            continue;
                        }

                        if (!message.checkMessageLength()) {
                            System.out.println(
                                    "Please enter a message of less than "
                                    + "250 characters"
                            );
                            i--;
                            continue;
                        }

                        System.out.println("\nMessage preview:");
                        System.out.println(message.printMessage());

                        System.out.println("\nChoose an action:");
                        System.out.println("1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println("3) Store Message to send later");
                        System.out.print("Your choice: ");

                        String action = scanner.nextLine().trim();
                        boolean processed = false;

                        switch (action) {
                            case "1":
                                String sendResult = message.SentMessage();

                                if ("Sent".equals(
                                        message.getMessageStatus())) {
                                    sentMessages.add(message);

                                    System.out.println(
                                            "Message successfully sent"
                                    );

                                    // Display details after sending.
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
                                System.out.println(
                                        "Press 0 to delete the message"
                                );
                                processed = true;
                                break;

                            case "3":
                                String storeResult = message.storeMessages();

                                if ("Stored".equals(
                                        message.getMessageStatus())) {
                                    System.out.println(
                                            "Message successfully stored"
                                    );
                                    System.out.println(storeResult);
                                    processed = true;
                                } else {
                                    System.out.println(storeResult);
                                }
                                break;

                            default:
                                System.out.println(
                                        "Invalid action. Please choose "
                                        + "1, 2 or 3."
                                );
                                break;
                        }

                        if (!processed) {
                            // Retry without consuming a message slot.
                            i--;
                            continue;
                        }

                        messagesProcessed++;

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
                    System.out.println(
                            "Thank you for using QuickChat."
                    );
                    break;

                default:
                    System.out.println(
                            "Invalid option. Please choose 1, 2 or 3."
                    );
                    break;
            }
        }

        int totalSent = sentMessages.isEmpty()
                ? 0
                : sentMessages.get(0).returnTotalMessages();

        System.out.println("\nTotal messages sent: " + totalSent);

        scanner.close();
    }

    // Read a valid non-negative message limit.
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
