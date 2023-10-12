package com.tdtu.mywallet;

public class ExpenseDaily {
    private String amount;
    private String kind;
    private String description;

    public ExpenseDaily(String amount, String kind, String description) {
        this.amount = amount;
        this.kind = kind;
        this.description = description;
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
}
