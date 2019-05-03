package com.example.michaelhuff.slushfund.persistance;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY day_of_transaction DESC")
    LiveData<List<Transaction>> getTransactions();

    @Insert(onConflict = REPLACE)
    void insertTransaction(Transaction transaction);

    @Delete
    void deleteTransaction(Transaction transaction);

    @Query("DELETE FROM transactions")
    void deleteAllTransactions();

}
