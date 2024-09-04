package com.example.aicommunication;

import java.io.Serializable;

public class User implements Serializable {
    private String user_name;
    private final String user_id;
    private String password;
    private int tokens_used;

    public User(String user_name, String user_id, String password, int tokens_used) {
        this.user_name = user_name;
        this.user_id = user_id;
        this.password = password;
        this.tokens_used = tokens_used;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTokens_used() {
        return tokens_used;
    }

    public void setTokens_used(int tokens_used) {
        this.tokens_used = tokens_used;
    }
}
