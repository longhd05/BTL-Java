package com.librarymanagement.model;

public class User {
    private String username;
    private String passwordHash;
    private Role role;
    private String displayName;
    private String salt;

    public User() {
    }

    public User(String username, String passwordHash, Role role, String displayName, String salt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.displayName = displayName;
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
