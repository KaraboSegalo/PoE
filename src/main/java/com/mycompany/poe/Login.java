package com.mycompany.poe;

/**
 * Handles user registration and login functionality.
 *
 * @author Student
 */
public class Login {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;

    /**
     * Creates a Login object using the user's registration information.
     *
     * @param firstName user's first name
     * @param lastName user's last name
     * @param username user's username
     * @param password user's password
     * @param cellPhoneNumber user's cellphone number
     */
    public Login(String firstName, String lastName, String username,
            String password, String cellPhoneNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    /**
     * Checks whether the username contains an underscore and is no more
     * than five characters long.
     *
     * @return true if the username is correctly formatted, otherwise false
     */
    public boolean checkUserName() {

        return username.contains("_")
                && username.length() <= 5;
    }

    /**
     * Checks whether the password meets the required complexity rules.
     * The password must contain at least eight characters, one capital
     * letter, one number, and one special character.
     *
     * @return true if the password meets the requirements, otherwise false
     */
    public boolean checkPasswordComplexity() {

        boolean hasCapitalLetter = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;

        for (int i = 0; i < password.length(); i++) {

            char character = password.charAt(i);

            if (Character.isUpperCase(character)) {
                hasCapitalLetter = true;
            }

            if (Character.isDigit(character)) {
                hasNumber = true;
            }

            if (!Character.isLetterOrDigit(character)) {
                hasSpecialCharacter = true;
            }
        }

        return password.length() >= 8
                && hasCapitalLetter
                && hasNumber
                && hasSpecialCharacter;
    }

    /**
     * Checks whether the cellphone number follows the required
     * South African international format.
     *
     * Regular expression:
     * ^\\+27\\d{9}$
     *
     * This requires the number to begin with +27 followed by nine digits.
     *
     * Regex format researched from Stack Overflow:
     * "Validate South Africa Cell Phone Number"
     *
     * Source:
     * https://stackoverflow.com/questions/4058001/
     * validate-south-africa-cell-phone-number
     *
     * @return true if the cellphone number is correctly formatted,
     * otherwise false
     */
    public boolean checkCellPhoneNumber() {

        String southAfricanNumberPattern = "^\\+27\\d{9}$";

        return cellPhoneNumber.matches(southAfricanNumberPattern);
    }

    /**
     * Registers the user by checking the username, password and
     * cellphone number.
     *
     * @return an appropriate registration status message
     */
    public String registerUser() {

        if (!checkUserName()) {

            return "Username is not correctly formatted, please ensure "
                    + "that your username contains an underscore and is "
                    + "no more than five characters in length";
        }

        if (!checkPasswordComplexity()) {

            return "Password is not correctly formatted, please ensure "
                    + "that the password contains at least eight characters, "
                    + "a capital letter, a number, and a special character";
        }

        if (!checkCellPhoneNumber()) {

            return "Cell phone number incorrectly formatted or does not "
                    + "contain international code";
        }

        return "Username successfully captured\n"
                + "Password successfully captured\n"
                + "Cell phone number successfully added";
    }

    /**
     * Checks whether the login username and password match the
     * registered user's credentials.
     *
     * @param loginUsername username entered during login
     * @param loginPassword password entered during login
     * @return true if the credentials match, otherwise false
     */
    public boolean loginUser(String loginUsername, String loginPassword) {

        return loginUsername.equals(username)
                && loginPassword.equals(password);
    }

    /**
     * Returns the appropriate login status message.
     *
     * @param loginUsername username entered during login
     * @param loginPassword password entered during login
     * @return welcome message if login is successful, otherwise an
     * incorrect credentials message
     */
    public String returnLoginStatus(
            String loginUsername,
            String loginPassword) {

        if (loginUser(loginUsername, loginPassword)) {

            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you again.";

        } else {

            return "Username or password incorrect, please try again";
        }
    }
}