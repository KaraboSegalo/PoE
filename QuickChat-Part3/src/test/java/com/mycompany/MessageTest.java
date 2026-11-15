
package com.mycompany.poe;

import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Message class.
 *
 * Tests message validation, hash generation,
 * message status, JSON storage and retrieval.
 *
 * @author Student
 */
class MessageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void messageIDMustHaveTenDigits() {

        Message message = new Message(
                1, "+27718693002", "Hello Mike"
        );

        assertTrue(message.checkMessageID());

        assertTrue(
                message.getMessageID().matches("\\d{10}")
        );
    }

    @Test
    void internationalRecipientMustBeAccepted() {

        Message message = new Message(
                1, "+27718693002", "Hello Mike"
        );

        assertEquals(
                "Cell phone number successfully captured",
                message.checkRecipientCell()
        );
    }

    @Test
    void invalidRecipientMustBeRejected() {

        Message message = new Message(
                1, "0718693002", "Hello Mike"
        );

        assertTrue(
                message.checkRecipientCell()
                        .contains("incorrectly formatted")
        );
    }

    @Test
    void messageOf250CharactersMustBeAccepted() {

        String text = "A".repeat(250);

        Message message = new Message(
                1, "+27718693002", text
        );

        assertTrue(message.checkMessageLength());
    }

    @Test
    void messageOver250CharactersMustBeRejected() {

        String text = "A".repeat(251);

        Message message = new Message(
                1, "+27718693002", text
        );

        assertFalse(message.checkMessageLength());

        assertEquals(
                "Message exceeds 250 characters by 1, please reduce the size",
                message.SentMessage()
        );
    }

    @Test
    void messageHashMustContainIDPrefixNumberAndWords() {

        Message message = new Message(
                1, "+27718693002",
                "Hello Mike, welcome tonight!"
        );

        String firstTwoDigits =
                message.getMessageID().substring(0, 2);

        String expected =
                firstTwoDigits + ":1:HELLOTONIGHT";

        assertEquals(
                expected,
                message.createMessageHash()
        );
    }

    @Test
    void validMessageMustBeMarkedAsSent() {

        Message message = new Message(
                1, "+27718693002", "Hello Mike"
        );

        assertEquals(
                "Message ready to send",
                message.SentMessage()
        );

        assertEquals(
                "Sent",
                message.getMessageStatus()
        );
    }

    @Test
    void disregardedMessageMustHaveCorrectStatus() {

        Message message = new Message(
                1, "+27718693002", "Hello Mike"
        );

        assertEquals(
                "Message disregarded",
                message.disregardMessage()
        );

        assertEquals(
                "Disregarded",
                message.getMessageStatus()
        );
    }

    @Test
    void storedMessageMustBeWrittenToJson() throws Exception {

        Path jsonFile = temporaryDirectory.resolve("messages.json");

        Message message = new Message(
                1, "+27718693002",
                "Testing JSON message storage."
        );

        JSONObject object = new JSONObject();

        object.put("messageID", message.getMessageID());
        object.put("messageNumber", message.getMessageNumber());
        object.put("recipient", message.getRecipient());
        object.put("message", message.getMessage());
        object.put("messageHash", message.getMessageHash());
        object.put("status", "Stored");

        Files.writeString(
                jsonFile,
                object.toString() + System.lineSeparator()
        );

        assertTrue(Files.exists(jsonFile));

        String contents = Files.readString(jsonFile);

        assertTrue(contents.contains(message.getMessageID()));

        assertTrue(
                contents.contains("Testing JSON message storage.")
        );

        assertTrue(
                contents.contains("\"status\":\"Stored\"")
        );
    }

    @Test
    void printMessageMustIncludeMessageDetails() {

        Message message = new Message(
                1, "+27718693002", "Hello Mike"
        );

        String output = message.printMessage();

        assertTrue(output.contains(message.getMessageID()));
        assertTrue(output.contains("+27718693002"));
        assertTrue(output.contains("Hello Mike"));

        assertTrue(
                output.contains(message.createMessageHash())
        );
    }

    /**
     * Tests that stored messages can be loaded from JSON.
     */
    @Test
    void storedMessagesMustBeLoadedFromJson() throws Exception {

        Path jsonFile = temporaryDirectory.resolve("messages.json");

        Message originalMessage = new Message(
                "1234567890",
                1,
                "+27718693002",
                "Testing stored message retrieval.",
                "Stored"
        );

        JSONObject object = new JSONObject();

        object.put("messageID", originalMessage.getMessageID());
        object.put("messageNumber", originalMessage.getMessageNumber());
        object.put("recipient", originalMessage.getRecipient());
        object.put("message", originalMessage.getMessage());
        object.put("messageHash", originalMessage.getMessageHash());
        object.put("status", "Stored");

        Files.writeString(
                jsonFile,
                object.toString() + System.lineSeparator()
        );

        Message[] loadedMessages =
                Message.loadStoredMessages(jsonFile);

        assertEquals(1, loadedMessages.length);

        assertEquals(
                originalMessage.getMessageID(),
                loadedMessages[0].getMessageID()
        );

        assertEquals(
                originalMessage.getRecipient(),
                loadedMessages[0].getRecipient()
        );

        assertEquals(
                originalMessage.getMessage(),
                loadedMessages[0].getMessage()
        );

        assertEquals(
                "Stored",
                loadedMessages[0].getMessageStatus()
        );
    }

    /**
     * Tests that sent and disregarded messages are excluded.
     */
    @Test
    void onlyStoredMessagesMustBeLoaded() throws Exception {

        Path jsonFile = temporaryDirectory.resolve("messages.json");

        JSONObject storedObject = new JSONObject();

        storedObject.put("messageID", "1111111111");
        storedObject.put("messageNumber", 1);
        storedObject.put("recipient", "+27718693002");
        storedObject.put("message", "Stored message");
        storedObject.put("status", "Stored");

        JSONObject sentObject = new JSONObject();

        sentObject.put("messageID", "2222222222");
        sentObject.put("messageNumber", 2);
        sentObject.put("recipient", "+27718693002");
        sentObject.put("message", "Sent message");
        sentObject.put("status", "Sent");

        JSONObject disregardedObject = new JSONObject();

        disregardedObject.put("messageID", "3333333333");
        disregardedObject.put("messageNumber", 3);
        disregardedObject.put("recipient", "+27718693002");
        disregardedObject.put("message", "Disregarded message");
        disregardedObject.put("status", "Disregarded");

        Files.writeString(
                jsonFile,
                storedObject.toString() + System.lineSeparator()
                + sentObject.toString() + System.lineSeparator()
                + disregardedObject.toString() + System.lineSeparator()
        );

        Message[] loadedMessages =
                Message.loadStoredMessages(jsonFile);

        assertEquals(1, loadedMessages.length);

        assertEquals(
                "Stored message",
                loadedMessages[0].getMessage()
        );
    }

    /**
     * Tests loading when the JSON file does not exist.
     */
    @Test
    void missingJsonFileMustReturnEmptyArray() throws Exception {

        Path missingFile =
                temporaryDirectory.resolve("missing.json");

        Message[] loadedMessages =
                Message.loadStoredMessages(missingFile);

        assertNotNull(loadedMessages);
        assertEquals(0, loadedMessages.length);
    }
}
