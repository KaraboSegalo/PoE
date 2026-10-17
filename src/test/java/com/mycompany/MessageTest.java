
package com.mycompany.poe;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void messageIDMustHaveTenDigits() {
        Message message = new Message(
                1, "+27718693002", "Hello Mike"
        );

        assertTrue(message.checkMessageID());
        assertTrue(message.getMessageID().matches("\\d{10}"));
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
                message.checkRecipientCell().contains("incorrectly formatted")
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

        assertEquals(expected, message.createMessageHash());
    }

    @Test
    void validMessageMustBeMarkedAsSent() {
        Message message = new Message(
                1, "+27718693002", "Hello Mike"
        );

        assertEquals("Message ready to send", message.SentMessage());
        assertEquals("Sent", message.getMessageStatus());
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

        assertEquals("Disregarded", message.getMessageStatus());
    }

    @Test
    void storedMessageMustBeWrittenToJson() throws Exception {
        Message message = new Message(
                1, "+27718693002",
                "Testing JSON message storage."
        );

        String result = message.storeMessages();

        assertTrue(result.startsWith("Message stored successfully at:"));
        assertEquals("Stored", message.getMessageStatus());

        Path jsonFile = Path.of(
                System.getProperty("user.dir"),
                "messages.json"
        );

        assertTrue(Files.exists(jsonFile));

        String contents = Files.readString(jsonFile);

        assertTrue(contents.contains(message.getMessageID()));
        assertTrue(contents.contains("Testing JSON message storage."));
        assertTrue(contents.contains("\"status\":\"Stored\""));
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
        assertTrue(output.contains(message.createMessageHash()));
    }
}
