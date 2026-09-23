/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.poe;

import java.util.Scanner;

/**
 *
 * @author Student
 */
public class PoE {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        System.out.print("Enter your South African cell phone number: ");
        String cellPhoneNumber = scanner.nextLine();

        Login login = new Login(
                firstName,
                lastName,
                username,
                password,
                cellPhoneNumber
        );

        System.out.println(login.registerUser());

        System.out.print("Enter your username to login: ");
        String loginUsername = scanner.nextLine();

        System.out.print("Enter your password to login: ");
        String loginPassword = scanner.nextLine();

        System.out.println(login.returnLoginStatus(loginUsername, loginPassword));
    }
}