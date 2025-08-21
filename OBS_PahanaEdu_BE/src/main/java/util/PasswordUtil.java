/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.security.MessageDigest;

/**
 *
 * @author Sanara
 */
public class PasswordUtil {
    public static String hashPassword(String password) {
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        // Use a standard character encoding like UTF-8
        byte[] hashedBytes = md.digest(password.getBytes("UTF-8")); 
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
}
