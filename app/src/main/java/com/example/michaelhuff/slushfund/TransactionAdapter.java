package com.example.michaelhuff.slushfund;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.michaelhuff.slushfund.persistance.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {


    private List<Transaction> data;
    private int colorPrime = 0;
    private int colorAcc = 0;

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recycler_view_item, viewGroup, false);
        colorPrime = ContextCompat.getColor(viewGroup.getContext(), R.color.colorPrimary);
        colorAcc = ContextCompat.getColor(viewGroup.getContext(), R.color.colorAccent);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder vh, int i) {
        Transaction transaction = data.get(i);
        vh.tvAmount.setText(transaction.amount.toString());
        vh.tvType.setText(transaction.expenseDescription);
        int color = transaction.amount > 0 ? colorPrime : colorAcc;
        vh.tvAmount.setTextColor(color);
        vh.tvType.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        if (data != null) {
          return data.size();
        } else {
            return 0;
        }
    }


    public void setTransactionList(List<Transaction> list) {
        data = list;
    }


    class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView tvType;
        public TextView tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.expense_type);
            tvAmount = itemView.findViewById(R.id.expense_amount);
        }
    }

}
