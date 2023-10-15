package com.tdtu.mywallet;

import android.os.Parcel;
import android.os.Parcelable;

public class Expense implements Parcelable {

    private long id;
    private String amount;
    private String kind;
    private String description;
    private String date;
    private String time;

    public Expense( long id,
                    String amount,
                   String kind,
                   String description,
                   String date,
                   String time) {

        this.id = id;
        this.amount = amount;
        this.kind = kind;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    //Getters and setters

    public long getId(){
        return id;
    }

    public void setId(long id){
       this.id = id;
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


    // Parcelable constructor
    protected Expense(Parcel in) {
        id = in.readLong();
        amount = in.readString();
        kind = in.readString();
        description = in.readString();
        date = in.readString();
        time = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(amount);
        dest.writeString(kind);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(time);
    }

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };


}
