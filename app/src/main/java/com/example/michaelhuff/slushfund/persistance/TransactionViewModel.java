package com.example.michaelhuff.slushfund.persistance;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {
    private TransactionDao transactionDao;
    private LiveData<List<Transaction>> transactionsLiveData;
    public TransactionViewModel(@NonNull Application application) {
        super(application);
        transactionDao = TransactionDatabase.getDatabase(application).transactionDao();
        transactionsLiveData = transactionDao.getTransactions();
    }

    public LiveData<List<Transaction>> getTransactionsList() {
        return transactionsLiveData;
    }

    public void insert(Transaction transaction) {
        transactionDao.insertTransaction(transaction);
    }
    
    public void delete(Transaction transaction) {
        transactionDao.deleteTransaction(transaction);
    }
}
