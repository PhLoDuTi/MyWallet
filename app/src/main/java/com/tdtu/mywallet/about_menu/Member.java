package com.tdtu.mywallet.about_menu;

public class Member {
    private String name;
    private String link;

    public Member(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
