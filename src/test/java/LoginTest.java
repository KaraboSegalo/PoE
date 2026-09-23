package com.mycompany.poe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Login class.
 *
 * @author Student
 */
public class LoginTest {

    /**
     * Tests a correctly formatted username.
     */
    @Test
    public void testCheckUserName() {

        Login login = new Login(
                "Karabo",
                "Segalo",
                "Ka_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertTrue(login.checkUserName());
    }

    /**
     * Tests an incorrectly formatted username.
     */
    @Test
    public void testCheckUserNameInvalid() {

        Login login = new Login(
                "Karabo",
                "Segalo",
                "Kyle !!!",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertFalse(login.checkUserName());
    }

    /**
     * Tests a correctly formatted password.
     */
    @Test
    public void testCheckPasswordComplexity() {

        Login login = new Login(
                "Karabo",
                "Segalo",
                "Ka_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertTrue(login.checkPasswordComplexity());
    }

    /**
     * Tests an incorrectly formatted password.
     */
    @Test
    public void testCheckPasswordComplexityInvalid() {

        Login login = new Login(
                "Karabo",
                "Segalo",
                "Ka_1",
                "password",
                "+27838968976"
        );

        assertFalse(login.checkPasswordComplexity());
    }

    /**
     * Tests a correctly formatted South African cellphone number.
     */
    @Test
    public void testCheckCellPhoneNumber() {

        Login login = new Login(
                "Karabo",
                "Segalo",
                "Ka_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertTrue(login.checkCellPhoneNumber());
    }

    /**
     * Tests an incorrectly formatted cellphone number.
     */
    @Test
    public void testCheckCellPhoneNumberInvalid() {

        Login login = new Login(
                "Karabo",
                "Segalo",
                "Ka_1",
                "Ch&&sec@ke99!",
                "08966553"
        );

        assertFalse(login.checkCellPhoneNumber());
    }

    /**
     * Tests successful user registration.
     */
    @Test
    public void testRegisterUserSuccessful() {

        Login login = new Login(
                "Karabo",
                "Segalo",
                "Ka_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertEquals(
                "Username successfully captured\n"
                + "Password successfully captured\n"
                + "Cell phone number successfully added",
                login.registerUser()
        );
    }

    /**
     * Tests registration with an invalid password.
     */
    @Test
    public void testRegisterUserInvalidPassword() {

        Login login = new Login(
                "Karabo",
                "Segalo",
                "Ka_1",
                "password",
                "+27838968976"
        );

        assertEquals(
                "Password is not correctly formatted, please ensure "
                + "that the password contains at least eight characters, "
                + "a capital letter, a number, and a special character",
                login.registerUser()
        );
    }

    /**
     * Tests successful login.
     */
    @Test
    public void testLoginSuccessful() {

        Login login = new Login(
                "Karabo",
                "Segalo",
                "Ka_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertTrue(
                login.loginUser(
                        "Ka_1",
                        "Ch&&sec@ke99!"
                )
        );
    }

    /**
     * Tests unsuccessful login.
     */
    @Test
    public void testLoginUnsuccessful() {

        Login login = new Login(
                "Karabo",
                "Segalo",
                "Ka_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertFalse(
                login.loginUser(
                        "Ka_1",
                        "WrongPassword1!"
                )
        );
    }

    /**
     * Tests the successful login status message.
     */
    @Test
    public void testReturnLoginStatusSuccessful() {

        Login login = new Login(
                "Karabo",
                "Segalo",
                "Ka_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertEquals(
                "Welcome Karabo, Segalo it is great to see you again.",
                login.returnLoginStatus(
                        "Ka_1",
                        "Ch&&sec@ke99!"
                )
        );
    }

    /**
     * Tests the unsuccessful login status message.
     */
    @Test
    public void testReturnLoginStatusUnsuccessful() {

        Login login = new Login(
                "Karabo",
                "Segalo",
                "Ka_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertEquals(
                "Username or password incorrect, please try again",
                login.returnLoginStatus(
                        "Ka_1",
                        "WrongPassword1!"
                )
        );
    }
}