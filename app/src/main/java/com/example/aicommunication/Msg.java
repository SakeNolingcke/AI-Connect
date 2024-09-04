package com.example.aicommunication;

public class Msg {
    private final int avatar_img;
    private final String content;
    private final int owner;
    public static int OWNER_USER = 1;
    public static int OWNER_AI=0;

    public Msg(int avatar_img, String content, int owner) {
        this.avatar_img = avatar_img;
        this.content = content;
        this.owner = owner;
    }

    public Msg(String content, int owner) {
        this.content = content;
        this.owner = owner;
        if(owner==0){
            avatar_img = R.drawable.avatar_ai_slt;
        } else{
            avatar_img = R.drawable.avatar_user_slt;
        }
    }

    public int getAvatar_img() {
        return avatar_img;
    }

    public String getContent() {
        return content;
    }

    public int getOwner() {
        return owner;
    }
}
