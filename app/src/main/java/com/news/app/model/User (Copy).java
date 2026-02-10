
package com.news.app.model;

import android.text.TextUtils;
import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {

    // ----------------------
    // Champs principaux
    // ----------------------
    private String id;                   // ID unique Firebase
    private String firstName;            // Prénom
    private String lastName;             // Nom
    private String email;                // Email
    private String password;             // Mot de passe
    private String dateOfBirth;          // Date de naissance "yyyy-MM-dd"
    private List<String> preferences;    // Catégories favorites
    private String profileImageUrl;      // Photo de profil
    private String createdAt;            // Date d’inscription "yyyy-MM-dd"
    private String lastLogin;            // Date dernière connexion "yyyy-MM-dd HH:mm:ss"
    private String fcmToken;             // Token pour notifications push
    private String role;                 // Type ou rôle utilisateur ("user", "admin")

    // ----------------------
    // Constructeurs
    // ----------------------
    public User() {
        preferences = new ArrayList<>();
    }

    public User(String id, String firstName, String lastName, String email, String password,
                String dateOfBirth, List<String> preferences, String profileImageUrl,
                String createdAt, String lastLogin, String fcmToken, String role) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.preferences = preferences != null ? preferences : new ArrayList<>();
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.fcmToken = fcmToken;
        this.role = role;
    }

    // ----------------------
    // Getters et Setters
    // ----------------------
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public List<String> getPreferences() { return preferences; }
    public void setPreferences(List<String> preferences) { this.preferences = preferences; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // ----------------------
    // Méthodes pour gérer les préférences
    // ----------------------
    public void addPreference(String categoryId) {
        if (!preferences.contains(categoryId)) {
            preferences.add(categoryId);
        }
    }

    public void removePreference(String categoryId) {
        preferences.remove(categoryId);
    }

    // ----------------------
    // Méthodes de validation
    // ----------------------
    public boolean isValidEmail() {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPassword() {
        if (password == null) return false;
        String pattern = "^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$";
        return password.matches(pattern);
    }

    public boolean isValidName(String name) {
        if (name == null || name.length() < 3) return false;
        return !Character.isDigit(name.charAt(0));
    }

    public boolean isValidDateOfBirth() {
        if (dateOfBirth == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dob = sdf.parse(dateOfBirth);
            Date limit = sdf.parse("2010-01-01");
            return dob.before(limit);
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isAdult() {
        if (dateOfBirth == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dob = sdf.parse(dateOfBirth);
            long ageMillis = new Date().getTime() - dob.getTime();
            long ageYears = ageMillis / (1000L * 60 * 60 * 24 * 365);
            return ageYears >= 18;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isValidUser() {
        return isValidName(firstName) &&
                isValidName(lastName) &&
                isValidEmail() &&
                isValidPassword() &&
                isValidDateOfBirth();
    }

    // ----------------------
    // Méthodes utilitaires
    // ----------------------
    public String getFullName() {
        return firstName + " " + lastName;
    }
}