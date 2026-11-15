
package com.mycompany.poe;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import org.json.JSONObject;

/**
 * Manages QuickChat message arrays and stored-message operations.
 *
 * @author Student
 */
public class QuickChatManager {

    private Message[] sentMessages;
    private Message[] disregardedMessages;
    private Message[] storedMessages;

    private String[] messageHashes;
    private String[] messageIDs;

    /**
     * Creates the manager and loads the initial stored messages.
     */
    public QuickChatManager(Message[] initialStoredMessages) {

        sentMessages = new Message[0];
        disregardedMessages = new Message[0];
        storedMessages = new Message[0];

        messageHashes = new String[0];
        messageIDs = new String[0];

        if (initialStoredMessages != null) {
            for (Message message : initialStoredMessages) {
                addStoredMessage(message);
            }
        }
    }

    /**
     * Adds a successfully sent message.
     */
    public void addSentMessage(Message message) {

        sentMessages = addMessage(sentMessages, message);
        addMessageDetails(message);
    }

    /**
     * Adds a disregarded message.
     */
    public void addDisregardedMessage(Message message) {

        disregardedMessages = addMessage(
                disregardedMessages, message
        );

        addMessageDetails(message);
    }

    /**
     * Adds a stored message.
     */
    public void addStoredMessage(Message message) {

        if (message == null) {
            return;
        }

        storedMessages = addMessage(storedMessages, message);
        addMessageDetails(message);
    }

    /**
     * Adds the ID and hash for every message.
     * Duplicate hashes are retained because each message
     * must have an entry in the arrays.
     */
    private void addMessageDetails(Message message) {

        if (message == null) {
            return;
        }

        messageIDs = addString(
                messageIDs, message.getMessageID()
        );

        messageHashes = addString(
                messageHashes, message.getMessageHash()
        );
    }

    /**
     * Adds a message to a Message array.
     */
    private Message[] addMessage(
            Message[] messages, Message newMessage) {

        if (newMessage == null) {
            return messages;
        }

        messages = Arrays.copyOf(
                messages, messages.length + 1
        );

        messages[messages.length - 1] = newMessage;

        return messages;
    }

    /**
     * Appends a value to a String array.
     * Duplicate values are allowed.
     */
    private String[] addString(
            String[] values, String newValue) {

        if (newValue == null) {
            return values;
        }

        values = Arrays.copyOf(
                values, values.length + 1
        );

        values[values.length - 1] = newValue;

        return values;
    }

    /**
     * Returns the sent messages.
     */
    public Message[] getSentMessages() {
        return Arrays.copyOf(sentMessages, sentMessages.length);
    }

    /**
     * Returns the disregarded messages.
     */
    public Message[] getDisregardedMessages() {
        return Arrays.copyOf(
                disregardedMessages, disregardedMessages.length
        );
    }

    /**
     * Returns the stored messages.
     */
    public Message[] getStoredMessages() {
        return Arrays.copyOf(storedMessages, storedMessages.length);
    }

    /**
     * Returns all message hashes, including duplicates.
     */
    public String[] getMessageHashes() {
        return Arrays.copyOf(messageHashes, messageHashes.length);
    }

    /**
     * Returns all message IDs.
     */
    public String[] getMessageIDs() {
        return Arrays.copyOf(messageIDs, messageIDs.length);
    }

    /**
     * Finds the longest stored message.
     */
    public Message findLongestStoredMessage() {

        if (storedMessages.length == 0) {
            return null;
        }

        Message longestMessage = storedMessages[0];

        for (Message message : storedMessages) {

            if (message.getMessage().length()
                    > longestMessage.getMessage().length()) {

                longestMessage = message;
            }
        }

        return longestMessage;
    }

    /**
     * Searches for a stored message using its ID.
     */
    public Message searchByMessageID(String messageID) {

        if (messageID == null) {
            return null;
        }

        for (Message message : storedMessages) {

            if (message.getMessageID().equals(messageID)) {
                return message;
            }
        }

        return null;
    }

    /**
     * Finds all stored messages for a particular recipient.
     */
    public Message[] searchByRecipient(String recipient) {

        Message[] results = new Message[0];

        if (recipient == null) {
            return results;
        }

        for (Message message : storedMessages) {

            if (message.getRecipient().equalsIgnoreCase(recipient)) {
                results = addMessage(results, message);
            }
        }

        return results;
    }

    /**
     * Creates a report containing all stored message details.
     */
    public String generateMessageReport() {

        if (storedMessages.length == 0) {
            return "No stored messages found.";
        }

        StringBuilder report = new StringBuilder();

        report.append("QUICKCHAT STORED MESSAGE REPORT\n");
        report.append("================================\n");

        for (Message message : storedMessages) {

            report.append("-------------------------\n");

            report.append("Message ID: ")
                    .append(message.getMessageID()).append("\n");

            report.append("Message Hash: ")
                    .append(message.getMessageHash()).append("\n");

            report.append("Message Number: ")
                    .append(message.getMessageNumber()).append("\n");

            report.append("Recipient: ")
                    .append(message.getRecipient()).append("\n");

            report.append("Message: ")
                    .append(message.getMessage()).append("\n");

            report.append("Status: ")
                    .append(message.getMessageStatus()).append("\n");
        }

        report.append("================================\n");

        report.append("Total stored messages: ")
                .append(storedMessages.length);

        return report.toString();
    }

    /**
     * Deletes a stored message using its hash.
     * Rewrites the JSON file before updating the arrays.
     *
     * @param messageHash the hash of the message to delete
     * @param jsonFile the JSON file containing stored messages
     * @return true if deletion succeeds; otherwise false
     * @throws IOException if the JSON file cannot be updated
     */
    public boolean deleteMessageByHash(
            String messageHash, Path jsonFile) throws IOException {

        if (messageHash == null || jsonFile == null) {
            return false;
        }

        int deleteIndex = -1;

        // Find the message matching the supplied hash.
        for (int i = 0; i < storedMessages.length; i++) {

            if (storedMessages[i].getMessageHash()
                    .equalsIgnoreCase(messageHash)) {

                deleteIndex = i;
                break;
            }
        }

        if (deleteIndex == -1) {
            return false;
        }

        // Create an array without the selected message.
        Message[] updatedMessages =
                new Message[storedMessages.length - 1];

        int newIndex = 0;

        for (int i = 0; i < storedMessages.length; i++) {

            if (i != deleteIndex) {
                updatedMessages[newIndex] = storedMessages[i];
                newIndex++;
            }
        }

        // Resolve the JSON file to an absolute path.
        Path absoluteJsonFile = jsonFile.toAbsolutePath().normalize();

        // Create the parent directory if it does not exist.
        Path parentDirectory = absoluteJsonFile.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        // Rewrite the JSON file with the remaining messages.
        try (BufferedWriter writer = Files.newBufferedWriter(
                absoluteJsonFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            for (Message message : updatedMessages) {

                JSONObject messageObject = new JSONObject();

                messageObject.put(
                        "messageID", message.getMessageID()
                );

                messageObject.put(
                        "messageNumber", message.getMessageNumber()
                );

                messageObject.put(
                        "recipient", message.getRecipient()
                );

                messageObject.put(
                        "message", message.getMessage()
                );

                messageObject.put(
                        "messageHash", message.getMessageHash()
                );

                messageObject.put(
                        "status", message.getMessageStatus()
                );

                writer.write(messageObject.toString());
                writer.newLine();
            }

            writer.flush();
        }

        // Update the stored messages after JSON writing succeeds.
        storedMessages = updatedMessages;

        // Rebuild the message ID and hash arrays.
        rebuildMessageDetails();

        return true;
    }

    /**
     * Rebuilds the ID and hash arrays from all remaining messages.
     */
    private void rebuildMessageDetails() {

        messageIDs = new String[0];
        messageHashes = new String[0];

        for (Message message : sentMessages) {
            addMessageDetails(message);
        }

        for (Message message : disregardedMessages) {
            addMessageDetails(message);
        }

        for (Message message : storedMessages) {
            addMessageDetails(message);
        }
    }
}
