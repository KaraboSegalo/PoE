/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poe;

/**
 *
 * @author Student
 */
public class Login {
    private String firstName;
    private String lastName:
    private String username;
    private String password;
    private String cellPhoneNumber;
    
    public Login(String firstName, String lastName, String username, String password, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this. username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }
    
    public boolean checkUserName() {
        return username.contains("_") && username.length() <= 5;
    }
    
}


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
}