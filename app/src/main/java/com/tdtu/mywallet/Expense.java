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

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
