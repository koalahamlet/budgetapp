package com.example.michaelhuff.slushfund.persistance;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class TransactionRepository {
    private TransactionDao transactionDao;
    private LiveData<List<Transaction>> allTransactions;

    TransactionRepository(Application application) {
        TransactionDatabase db = TransactionDatabase.getDatabase(application);
        transactionDao = db.transactionDao();
        allTransactions = transactionDao.getTransactions();

    }

    public void insert(Transaction tr) {
        new insertAsyncTask(transactionDao).execute(tr);
    }

    private static class insertAsyncTask extends AsyncTask<Transaction, Void, Void> {

        private TransactionDao mAsyncTaskDao;

        insertAsyncTask(TransactionDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Transaction... params) {
            mAsyncTaskDao.insertTransaction(params[0]);
            return null;
        }
    }
}
