package com.tdtu.mywallet;

public class ExpenseDaily {
    private int amount;
    private String kind;
    private String description;

    public ExpenseDaily(int amount, String kind, String description) {
        this.amount = amount;
        this.kind = kind;
        this.description = description;
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
}
