
package com.mycompany.poe;

import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for QuickChatManager.
 *
 * Tests message arrays, searching, longest messages,
 * deletion using hashes and message reporting.
 *
 * @author Student
 */
class QuickChatManagerTest {

    @TempDir
    Path temporaryDirectory;

    /**
     * Creates a stored message for testing.
     */
    private Message createStoredMessage(
            String id, int number, String recipient, String text) {

        return new Message(
                id, number, recipient, text, "Stored"
        );
    }

    /**
     * Tests sent, disregarded and stored message arrays.
     */
    @Test
    void testSentDisregardedAndStoredArrays() {

        QuickChatManager manager =
                new QuickChatManager(new Message[0]);

        Message sent = new Message(
                1, "+27834557896", "Did you get the cake?"
        );

        assertEquals("Message ready to send", sent.SentMessage());

        manager.addSentMessage(sent);

        Message disregarded = new Message(
                2, "+27834484567", "Yohoooo, I am at your gate"
        );

        disregarded.disregardMessage();
        manager.addDisregardedMessage(disregarded);

        Message stored = createStoredMessage(
                "1234567890",
                3,
                "+27838884567",
                "Where are you?"
        );

        manager.addStoredMessage(stored);

        assertEquals(1, manager.getSentMessages().length);
        assertEquals(1, manager.getDisregardedMessages().length);
        assertEquals(1, manager.getStoredMessages().length);

        assertEquals(
                "Sent",
                manager.getSentMessages()[0].getMessageStatus()
        );

        assertEquals(
                "Disregarded",
                manager.getDisregardedMessages()[0].getMessageStatus()
        );

        assertEquals(
                "Stored",
                manager.getStoredMessages()[0].getMessageStatus()
        );

        assertEquals(3, manager.getMessageIDs().length);
        assertEquals(3, manager.getMessageHashes().length);
    }

    /**
     * Tests finding the longest stored message.
     */
    @Test
    void testFindLongestStoredMessage() {

        Message shortMessage = createStoredMessage(
                "1234567890", 1,
                "+27834557896", "Hello"
        );

        Message longestMessage = createStoredMessage(
                "2345678901", 2,
                "+27838884567",
                "Where are you? You are late! I have asked you to be on time"
        );

        Message mediumMessage = createStoredMessage(
                "3456789012", 3,
                "+27834484567", "I am outside"
        );

        QuickChatManager manager = new QuickChatManager(
                new Message[]{
                    shortMessage,
                    longestMessage,
                    mediumMessage
                }
        );

        assertEquals(
                longestMessage.getMessageID(),
                manager.findLongestStoredMessage().getMessageID()
        );
    }

    /**
     * Tests searching for a stored message using its ID.
     */
    @Test
    void testSearchByMessageID() {

        Message expectedMessage = createStoredMessage(
                "4567890123",
                1,
                "+27838884567",
                "It is dinner time!"
        );

        QuickChatManager manager = new QuickChatManager(
                new Message[]{expectedMessage}
        );

        Message result = manager.searchByMessageID("4567890123");

        assertNotNull(result);
        assertEquals("+27838884567", result.getRecipient());
        assertEquals("It is dinner time!", result.getMessage());
    }

    /**
     * Tests searching for all stored messages
     * belonging to a particular recipient.
     */
    @Test
    void testSearchByRecipient() {

        Message firstMessage = createStoredMessage(
                "5678901234", 1,
                "+27838884567", "Where are you?"
        );

        Message secondMessage = createStoredMessage(
                "6789012345", 2,
                "+27838884567", "I am waiting."
        );

        Message thirdMessage = createStoredMessage(
                "7890123456", 3,
                "+27834557896", "Did you get the cake?"
        );

        QuickChatManager manager = new QuickChatManager(
                new Message[]{
                    firstMessage,
                    secondMessage,
                    thirdMessage
                }
        );

        Message[] results =
                manager.searchByRecipient("+27838884567");

        assertEquals(2, results.length);

        assertEquals(
                "+27838884567",
                results[0].getRecipient()
        );

        assertEquals(
                "+27838884567",
                results[1].getRecipient()
        );
    }

    /**
     * Tests deleting a stored message using its hash.
     * Also verifies that the JSON file and arrays update.
     */
    @Test
    void testDeleteMessageUsingHash() throws Exception {

        Message messageToDelete = createStoredMessage(
                "8901234567", 1,
                "+27838884567", "Delete this message"
        );

        Message messageToKeep = createStoredMessage(
                "9012345678", 2,
                "+27834557896", "Keep this message"
        );

        QuickChatManager manager = new QuickChatManager(
                new Message[]{
                    messageToDelete,
                    messageToKeep
                }
        );

        Path jsonFile = temporaryDirectory.resolve("messages.json");

        JSONObject firstObject = new JSONObject();

        firstObject.put("messageID", messageToDelete.getMessageID());
        firstObject.put("messageNumber", messageToDelete.getMessageNumber());
        firstObject.put("recipient", messageToDelete.getRecipient());
        firstObject.put("message", messageToDelete.getMessage());
        firstObject.put("messageHash", messageToDelete.getMessageHash());
        firstObject.put("status", "Stored");

        JSONObject secondObject = new JSONObject();

        secondObject.put("messageID", messageToKeep.getMessageID());
        secondObject.put("messageNumber", messageToKeep.getMessageNumber());
        secondObject.put("recipient", messageToKeep.getRecipient());
        secondObject.put("message", messageToKeep.getMessage());
        secondObject.put("messageHash", messageToKeep.getMessageHash());
        secondObject.put("status", "Stored");

        Files.writeString(
                jsonFile,
                firstObject.toString() + System.lineSeparator()
                + secondObject.toString() + System.lineSeparator()
        );

        boolean deleted = manager.deleteMessageByHash(
                messageToDelete.getMessageHash(),
                jsonFile
        );

        assertTrue(deleted);

        assertEquals(1, manager.getStoredMessages().length);

        assertEquals(
                messageToKeep.getMessageID(),
                manager.getStoredMessages()[0].getMessageID()
        );

        assertEquals(1, manager.getMessageIDs().length);
        assertEquals(1, manager.getMessageHashes().length);

        String jsonContent = Files.readString(jsonFile);

        assertTrue(jsonContent.contains(
                messageToKeep.getMessageID()
        ));

        assertFalse(jsonContent.contains(
                messageToDelete.getMessageID()
        ));
    }

    /**
     * Tests generating a report containing all stored message details.
     */
    @Test
    void testGenerateMessageReport() {

        Message message = createStoredMessage(
                "0123456789",
                1,
                "+27838884567",
                "QuickChat report test"
        );

        QuickChatManager manager = new QuickChatManager(
                new Message[]{message}
        );

        String report = manager.generateMessageReport();

        assertTrue(report.contains(message.getMessageID()));
        assertTrue(report.contains(message.getMessageHash()));
        assertTrue(report.contains(message.getRecipient()));
        assertTrue(report.contains(message.getMessage()));
        assertTrue(report.contains("Total stored messages: 1"));
    }

    /**
     * Tests searching for an unknown message ID.
     */
    @Test
    void testSearchByUnknownMessageID() {

        QuickChatManager manager =
                new QuickChatManager(new Message[0]);

        assertNull(manager.searchByMessageID("9999999999"));
    }

    /**
     * Tests that duplicate message hashes are retained in the array.
     */
    @Test
    void testDuplicateMessageHashesAreRetained() {

        Message firstMessage = createStoredMessage(
                "1111111111", 1,
                "+27838884567", "Repeated message"
        );

        Message secondMessage = createStoredMessage(
                "1199999999", 1,
                "+27838884567", "Repeated message"
        );

        QuickChatManager manager = new QuickChatManager(
                new Message[]{firstMessage, secondMessage}
        );

        assertEquals(2, manager.getMessageHashes().length);

        assertEquals(
                manager.getMessageHashes()[0],
                manager.getMessageHashes()[1]
        );

        assertEquals(2, manager.getMessageIDs().length);
    }

    /**
     * Tests that deleting a message with an unknown hash
     * does not change the stored messages or arrays.
     */
    @Test
    void testDeleteMessageUsingUnknownHash() throws Exception {

        Message storedMessage = createStoredMessage(
                "3333333333", 1,
                "+27838884567", "Keep this message"
        );

        QuickChatManager manager = new QuickChatManager(
                new Message[]{storedMessage}
        );

        Path jsonFile = temporaryDirectory.resolve("messages.json");

        boolean deleted = manager.deleteMessageByHash(
                "UNKNOWN_HASH",
                jsonFile
        );

        assertFalse(deleted);

        assertEquals(1, manager.getStoredMessages().length);
        assertEquals(1, manager.getMessageHashes().length);
        assertEquals(1, manager.getMessageIDs().length);

        assertFalse(Files.exists(jsonFile));
    }
}
