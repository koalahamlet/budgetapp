package com.example.michaelhuff.slushfund.persistance;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "did")
    public int id;

    @ColumnInfo(name = "was_positive")
    public boolean wasPositive;

    @ColumnInfo(name = "amount")
    public Long amount;

    @ColumnInfo(name = "example_description")
    public String expenseDescription;

    @ColumnInfo(name = "day_of_transaction")
    public Date dayOfTransaction;

    public Transaction(Long amount) {
        this.amount = amount;
    }
}
