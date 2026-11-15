
package com.mycompany.poe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.json.JSONObject;

/**
 * Represents a QuickChat message.
 *
 * Handles message validation, hash generation,
 * sending, disregarding, JSON storage and retrieval.
 *
 * @author Student
 */
public class Message {

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String message;
    private String messageStatus;

    private static int totalMessagesSent = 0;

    /**
     * Creates a new message with a randomly generated ID.
     */
    public Message(int messageNumber, String recipient, String message) {

        this.messageID = String.valueOf(
                ThreadLocalRandom.current().nextLong(
                        1_000_000_000L, 10_000_000_000L
                )
        );

        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.messageStatus = "Pending";
    }

    /**
     * Restores an existing message from stored data.
     */
    public Message(String messageID, int messageNumber,
                   String recipient, String message,
                   String messageStatus) {

        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.messageStatus = messageStatus;
    }

    /**
     * Checks whether the message ID contains ten digits.
     */
    public boolean checkMessageID() {

        return messageID != null
                && messageID.matches("\\d{10}");
    }

    /**
     * Validates the international recipient number.
     */
    public String checkRecipientCell() {

        if (recipient != null
                && recipient.matches("\\+[1-9]\\d{7,14}")) {

            return "Cell phone number successfully captured";
        }

        return "Cell phone number is incorrectly formatted or "
                + "does not contain an international code. "
                + "Please correct the number and try again";
    }

    /**
     * Checks that the message does not exceed 250 characters.
     */
    public boolean checkMessageLength() {

        return message != null && message.length() <= 250;
    }

    /**
     * Generates the message hash.
     */
    public String createMessageHash() {

        String firstTwoDigits = messageID.substring(0, 2);

        if (message == null || message.isBlank()) {

            return (firstTwoDigits + ":" + messageNumber + ":")
                    .toUpperCase();
        }

        String cleanedMessage = message.trim()
                .replaceAll("[^a-zA-Z0-9\\s]", "");

        if (cleanedMessage.isBlank()) {

            return (firstTwoDigits + ":" + messageNumber + ":")
                    .toUpperCase();
        }

        String[] words = cleanedMessage.split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        return (firstTwoDigits + ":" + messageNumber + ":"
                + firstWord + lastWord).toUpperCase();
    }

    /**
     * Marks a valid message as sent.
     */
    public String SentMessage() {

        if (!checkMessageLength()) {

            if (message == null) {
                return "Message cannot be empty";
            }

            return "Message exceeds 250 characters by "
                    + (message.length() - 250)
                    + ", please reduce the size";
        }

        if (!checkRecipientCell().equals(
                "Cell phone number successfully captured")) {

            return checkRecipientCell();
        }

        messageStatus = "Sent";
        totalMessagesSent++;

        return "Message ready to send";
    }

    /**
     * Marks the message as disregarded.
     */
    public String disregardMessage() {

        messageStatus = "Disregarded";

        return "Message disregarded";
    }

    /**
     * Saves the message to the project JSON file.
     */
    public String storeMessages() {

        try {

            Path projectDirectory = Path.of(
                    System.getProperty("user.dir")
            );

            File jsonFile = projectDirectory
                    .resolve("messages.json").toFile();

            JSONObject messageObject = new JSONObject();

            messageObject.put("messageID", messageID);
            messageObject.put("messageNumber", messageNumber);
            messageObject.put("recipient", recipient);
            messageObject.put("message", message);
            messageObject.put("messageHash", createMessageHash());
            messageObject.put("status", "Stored");

            try (FileWriter writer = new FileWriter(jsonFile, true)) {

                writer.write(messageObject.toString());
                writer.write(System.lineSeparator());
            }

            messageStatus = "Stored";

            return "Message stored successfully at: "
                    + jsonFile.getAbsolutePath();

        } catch (IOException e) {

            return "Error storing message: " + e.getMessage();
        }
    }

    /**
     * Loads stored messages from the application's messages.json file.
     */
    public static Message[] loadStoredMessages() throws IOException {

        Path projectDirectory = Path.of(
                System.getProperty("user.dir")
        );

        Path jsonFile = projectDirectory.resolve("messages.json");

        return loadStoredMessages(jsonFile);
    }

    /**
     * Loads stored messages from a specified JSON file.
     *
     * This overload also allows unit tests to use temporary files.
     */
    public static Message[] loadStoredMessages(Path jsonFile)
            throws IOException {

        List<Message> storedMessages = new ArrayList<>();

        if (!Files.exists(jsonFile)) {

            return new Message[0];
        }

        try (BufferedReader reader = Files.newBufferedReader(jsonFile)) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    continue;
                }

                JSONObject messageObject = new JSONObject(line);

                String status = messageObject.optString(
                        "status", "Stored"
                );

                if (!status.equalsIgnoreCase("Stored")) {
                    continue;
                }

                String messageID =
                        messageObject.getString("messageID");

                int messageNumber =
                        messageObject.getInt("messageNumber");

                String recipient =
                        messageObject.getString("recipient");

                String message =
                        messageObject.getString("message");

                Message restoredMessage = new Message(
                        messageID,
                        messageNumber,
                        recipient,
                        message,
                        status
                );

                storedMessages.add(restoredMessage);
            }
        }

        return storedMessages.toArray(new Message[0]);
    }

    /**
     * Displays the message details.
     */
    public String printMessage() {

        return "Message ID: " + messageID
                + "\nMessage Hash: " + createMessageHash()
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    public int returnTotalMessages() {
        return totalMessagesSent;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return createMessageHash();
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageStatus() {
        return messageStatus;
    }
}
