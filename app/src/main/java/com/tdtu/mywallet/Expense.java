package com.tdtu.mywallet;

public class Expense {
    private String amount;
    private String kind;
    private String description;
    private String date;
    private String time;

    public Expense(String amount,
                   String kind,
                   String description,
                   String date,
                   String time) {

        this.amount = amount;
        this.kind = kind;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public String getKind() {
        return kind;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}