package com.example.michaelhuff.slushfund;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.michaelhuff.slushfund.persistance.Transaction;
import com.example.michaelhuff.slushfund.persistance.TransactionViewModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private TransactionViewModel transactionViewModel; // this is smelly code. refactor to an interface.
    private List<Transaction> data;
    private int colorPrime = 0;
    private int colorAcc = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yy");

    TransactionAdapter(TransactionViewModel transactionViewModel) {
        this.transactionViewModel = transactionViewModel;
    }

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
        final Transaction transaction = data.get(i);
        vh.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                transactionViewModel.delete(transaction);
                return false;
            }
        });
        String s = NumberFormat.getCurrencyInstance(Locale.US).format(transaction.amount/ 100.0);
        vh.tvAmount.setText(s);
        vh.tvType.setText(transaction.detail+" - "+dateFormat.format(transaction.dayOfTransaction));

        int color = transaction.wasPositive ? colorPrime : colorAcc;
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
        public RelativeLayout layout;
        public TextView tvType;
        public TextView tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.list_item);
            tvType = itemView.findViewById(R.id.expense_type);
            tvAmount = itemView.findViewById(R.id.expense_amount);
        }
    }

}
