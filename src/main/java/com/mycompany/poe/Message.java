
package com.mycompany.poe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import org.json.JSONObject;

public class Message {

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String message;
    private String messageStatus;

    private static int totalMessagesSent = 0;

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

    public boolean checkMessageID() {
        return messageID != null && messageID.matches("\\d{10}");
    }

    public String checkRecipientCell() {
        if (recipient != null
                && recipient.matches("\\+[1-9]\\d{7,14}")) {
            return "Cell phone number successfully captured";
        }

        return "Cell phone number is incorrectly formatted or "
                + "does not contain an international code. "
                + "Please correct the number and try again";
    }

    public boolean checkMessageLength() {
        return message != null && message.length() <= 250;
    }

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

    public String SentMessage() {
        if (!checkMessageLength()) {
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

    public String disregardMessage() {
        messageStatus = "Disregarded";
        return "Message disregarded";
    }

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
