
package com.mycompany.poe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    @Test
    void validUsernameMustBeAccepted() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_",
                "Password1!", "+27718693002"
        );

        assertTrue(login.checkUserName());
    }

    @Test
    void usernameWithoutUnderscoreMustBeRejected() {
        Login login = new Login(
                "Karabo", "Segalo", "karab",
                "Password1!", "+27718693002"
        );

        assertFalse(login.checkUserName());
    }

    @Test
    void usernameLongerThanFiveCharactersMustBeRejected() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_ab",
                "Password1!", "+27718693002"
        );

        assertFalse(login.checkUserName());
    }

    @Test
    void validPasswordMustBeAccepted() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_",
                "Password1!", "+27718693002"
        );

        assertTrue(login.checkPasswordComplexity());
    }

    @Test
    void passwordWithoutCapitalLetterMustBeRejected() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_",
                "password1!", "+27718693002"
        );

        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    void passwordWithoutNumberMustBeRejected() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_",
                "Password!", "+27718693002"
        );

        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    void passwordWithoutSpecialCharacterMustBeRejected() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_",
                "Password123", "+27718693002"
        );

        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    void validSouthAfricanInternationalNumberMustBeAccepted() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_",
                "Password1!", "+27718693002"
        );

        assertTrue(login.checkCellPhoneNumber());
    }

    @Test
    void localNumberWithoutCountryCodeMustBeRejected() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_",
                "Password1!", "0718693002"
        );

        assertFalse(login.checkCellPhoneNumber());
    }

    @Test
    void validRegistrationMustReturnSuccessMessage() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_",
                "Password1!", "+27718693002"
        );

        String result = login.registerUser();

        assertTrue(result.contains("Username successfully captured"));
        assertTrue(result.contains("Password successfully captured"));
        assertTrue(result.contains("Cell phone number successfully added"));
    }

    @Test
    void invalidRegistrationUsernameMustReturnError() {
        Login login = new Login(
                "Karabo", "Segalo", "karabo",
                "Password1!", "+27718693002"
        );

        assertTrue(login.registerUser().contains(
                "Username is not correctly formatted"
        ));
    }

    @Test
    void correctCredentialsMustAllowLogin() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_",
                "Password1!", "+27718693002"
        );

        assertTrue(login.loginUser("kar_", "Password1!"));
    }

    @Test
    void incorrectCredentialsMustNotAllowLogin() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_",
                "Password1!", "+27718693002"
        );

        assertFalse(login.loginUser("kar_", "WrongPassword1!"));
    }

    @Test
    void successfulLoginMustReturnWelcomeMessage() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_",
                "Password1!", "+27718693002"
        );

        assertEquals(
                "Welcome Karabo, Segalo it is great to see you again.",
                login.returnLoginStatus("kar_", "Password1!")
        );
    }

    @Test
    void failedLoginMustReturnErrorMessage() {
        Login login = new Login(
                "Karabo", "Segalo", "kar_",
                "Password1!", "+27718693002"
        );

        assertEquals(
                "Username or password incorrect, please try again",
                login.returnLoginStatus("kar_", "WrongPassword1!")
        );
    }
}
