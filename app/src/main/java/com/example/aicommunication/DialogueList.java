package com.example.aicommunication;

import java.io.Serializable;

public class DialogueList implements Serializable {
    private String dialogue_name;
    private int tokens_used;

    public DialogueList(String dialogue_name, int tokens_used) {
        this.dialogue_name = dialogue_name;
        this.tokens_used = tokens_used;
    }

    public String getDialogue_name() {
        return dialogue_name;
    }

    public String getTokens_used() {
        return "tokens:\n" + tokens_used;
    }

    public void setDialogue_name(String dialogue_name) {
        this.dialogue_name = dialogue_name;
    }

    public void setTokens_used(int tokens_used) {
        this.tokens_used = tokens_used;
    }
}
