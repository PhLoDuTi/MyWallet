package com.tdtu.mywallet;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Expense {
    private int amount;
    private String kind;
    private String description;
    private Date date;
    SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy");

    public Expense(int amount, String kind, Date date) {

        this.amount = amount;
        this.kind = kind;
        this.date = date;
        description = "des";
    }


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
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

    public Date getDate() {
        return this.date;
    }

    public String getDateString(){
        return fm.format(this.date);
    }


}
